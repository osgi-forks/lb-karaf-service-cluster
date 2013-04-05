/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.lburgazzoli.karaf.hazelcast.cluster;

import com.github.lburgazzoli.Utils;
import com.github.lburgazzoli.cluster.IClusterAgent;
import com.github.lburgazzoli.cluster.IClusteredNode;
import com.github.lburgazzoli.cluster.IClusteredServiceGroup;
import com.github.lburgazzoli.karaf.hazelcast.IHazelcastManager;
import com.github.lburgazzoli.osgi.IOSGiLifeCycle;
import com.github.lburgazzoli.osgi.IOSGiServiceListener;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class ClusterAgent
    implements IClusterAgent, IOSGiServiceListener, IOSGiLifeCycle, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterAgent.class);

    private String m_nodeId;

    private ILock m_clusterLock;
    private ScheduledExecutorService m_scheduler;
    private ScheduledFuture<?> m_schedulerHander;
    private AtomicBoolean m_leader;
    private String[] m_groupIDs;

    private ClusterContext m_clusterContex;


    /**
     * c-tor
     */
    public ClusterAgent() {
        m_nodeId = null;
        m_clusterLock = null;
        m_schedulerHander = null;
        m_scheduler = Executors.newScheduledThreadPool(1);
        m_leader = new AtomicBoolean(false);
        m_groupIDs = ArrayUtils.EMPTY_STRING_ARRAY;
        m_clusterContex = null;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param clusterContex
     */
    public void setClusterContext(ClusterContext clusterContex) {
        m_clusterContex = clusterContex;
    }

    /**
     *
     * @param clusterId
     */
    public void setNodeId(String clusterId) {
        m_nodeId = clusterId;
    }

    /**
     *
     * @param nodeGroups
     */
    public void setNodeGroups(String nodeGroups) {
        m_groupIDs = StringUtils.split(nodeGroups, ',');
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void init() {
        LOGGER.debug("Agent == init");
        m_clusterLock = getHazelcastManager().getLock(Constants.CLUSTER_LOCK);
        m_schedulerHander = m_scheduler.scheduleAtFixedRate(this,30,60,TimeUnit.SECONDS);

        m_clusterContex.getNode(m_nodeId);

        for(String group : m_groupIDs) {
            String[] items = StringUtils.split(group,':');
            LOGGER.debug("Items: {}",items.toString());

            m_clusterContex.getServiceGroup(items[0]);
        }
    }

    @Override
    public void destroy() {
        LOGGER.debug("Agent == destroy");

        m_scheduler.shutdown();

        try {
            LOGGER.debug("Awaiting scheduler termination");
            m_scheduler.awaitTermination(60,TimeUnit.SECONDS);
            LOGGER.debug("Scheduler termination done");
        } catch (InterruptedException e) {
            LOGGER.warn("Exception",e);
        }

        m_schedulerHander = null;

        try {
            LOGGER.debug("Agent == remove {} from nodes", m_nodeId);
            getLockRegistry().lock(m_nodeId);
            getClusterRegistry().remove(m_nodeId);
            LOGGER.debug("Agent == {} removed", m_nodeId);
        } finally {
            getLockRegistry().unlock(m_nodeId);
        }

        if(m_clusterLock != null) {
            if(m_clusterLock.isLocked()) {
                m_clusterLock.forceUnlock();
            }

            m_clusterLock = null;
        }

        deactivate();
    }

    @Override
    public void run() {
        if(!m_leader.get()) {
            try {
                if(m_clusterLock.tryLock(1,TimeUnit.SECONDS)) {
                    activate();
                }
            } catch (InterruptedException e) {
                LOGGER.warn("Exception",e);
            }
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    private void activate() {
        m_leader.set(true);

        LOGGER.debug("==== ACTIVATE ====");
        LOGGER.debug("Node <{}> is now the leader", m_nodeId);
    }

    private void deactivate() {
        LOGGER.debug("==== DEACTIVATE ====");
        LOGGER.debug("Node <{}> is not more the leader", m_nodeId);

        m_leader.set(false);
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public String getId() {
        return m_nodeId;
    }

    @Override
    public IClusteredNode getLocalNode() {
        return m_clusterContex.getNode(m_nodeId);
    }

    @Override
    public Collection<IClusteredNode> getNodes() {
        return Utils.downCastCollection(m_clusterContex.getNodes(),IClusteredNode.class);
    }

    @Override
    public Collection<IClusteredServiceGroup> getServiceGroups() {
        return Utils.downCastCollection(m_clusterContex.getServiceGroups(),IClusteredServiceGroup.class);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param reference
     */
    @Override
    public void bind(ServiceReference reference) {
        //TODO: needed?
    }

    /**
     *
     * @param reference
     */
    @Override
    public void unbind(ServiceReference reference) {
        //TODO: needed?
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    private IMap<String,String> getClusterRegistry() {
        return m_clusterContex.getHazelcastMap(Constants.REGISTRY_NODES);
    }

    /**
     *
     * @return
     */
    private IMap<String,String> getGroupRegistry() {
        return m_clusterContex.getHazelcastMap(Constants.REGISTRY_GROUPS);
    }

    /**
     *
     * @return
     */
    private IMap<String,String> getLockRegistry() {
        return m_clusterContex.getHazelcastMap(Constants.REGISTRY_LOCKS);
    }
    /**
     *
     * @return
     */
    public IHazelcastManager getHazelcastManager() {
        return m_clusterContex.getHazelcastManager();
    }
}
