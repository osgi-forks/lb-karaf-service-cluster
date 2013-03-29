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
package com.github.lburgazzoli.hazelcast.karaf.cluster;

import com.github.lburgazzoli.JsonUtils;
import com.github.lburgazzoli.cluster.DefaultClusterNode;
import com.github.lburgazzoli.cluster.IClusterAgent;
import com.github.lburgazzoli.cluster.IClusterNode;
import com.github.lburgazzoli.hazelcast.common.osgi.HazelcastAwareObject;
import com.github.lburgazzoli.osgi.IOSGiLifeCycle;
import com.github.lburgazzoli.osgi.IOSGiServiceListener;
import com.github.lburgazzoli.osgi.OSGiUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class ClusterAgent
    extends HazelcastAwareObject
    implements IClusterAgent, IOSGiServiceListener, IOSGiLifeCycle, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterAgent.class);

    private String m_clusterId;
    private Map<String,ClusteredServiceGroup> m_serviceGroups;

    private ILock m_clusterLock;
    private ScheduledExecutorService m_scheduler;
    private ScheduledFuture<?> m_schedulerHander;
    private AtomicBoolean m_leader;

    /**
     *
     */
    public ClusterAgent() {
        m_clusterId       = null;
        m_clusterLock     = null;
        m_schedulerHander = null;
        m_serviceGroups   = Maps.newConcurrentMap();
        m_scheduler       = Executors.newScheduledThreadPool(1);
        m_leader          = new AtomicBoolean(false);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param clusterId
     */
    public void setClusterId(String clusterId) {
        m_clusterId = clusterId;
    }

    /**
     *
     * @param clusterGroups
     */
    public void setClusterGroups(String clusterGroups) {
        String[] groups = StringUtils.split(clusterGroups,',');
        for(String group : groups) {
            m_serviceGroups.put(group,new ClusteredServiceGroup(getBundleContext(),group));
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void init() {
        LOGGER.debug("Agent == init");
        m_clusterLock = getHazelcastManager().getLock(Constants.CLUSTER_LOCK);
        m_schedulerHander = m_scheduler.scheduleAtFixedRate(this,30,60,TimeUnit.SECONDS);

        String jsonString = JsonUtils.encode(
            new DefaultClusterNode(
                m_clusterId,
                getHazelcastManager().getLocalAddress().getHostAddress()));

        if(StringUtils.isNotBlank(jsonString)) {
            getClusterRegistry().put(m_clusterId,jsonString);
        }

    }

    @Override
    public void destroy() {
        LOGGER.debug("Agent == destroy");
        if(m_clusterLock != null) {
            if(m_clusterLock.isLocked()) {
                m_clusterLock.forceUnlock();
            }

            m_clusterLock = null;
        }

        m_scheduler.shutdown();

        try {
            LOGGER.debug("Awaiting termination");
            m_scheduler.awaitTermination(60,TimeUnit.SECONDS);
            LOGGER.debug("Termination done");
        } catch (InterruptedException e) {
            LOGGER.warn("Exception",e);
        }

        m_schedulerHander = null;

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
        LOGGER.debug("Node <{}> is now the leader", m_clusterId);

        for(ClusteredServiceGroup csg : m_serviceGroups.values()) {
            csg.activate();
        }
    }

    private void deactivate() {
        LOGGER.debug("==== DEACTIVATE ====");
        LOGGER.debug("Node <{}> is not more the leader",m_clusterId);

        for(ClusteredServiceGroup csg : m_serviceGroups.values()) {
            csg.deactivate();
        }

        m_leader.set(false);
    }


    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public String getId() {
        return m_clusterId;
    }

    @Override
    public IClusterNode getLocalNode() {
        return getClusterNode(m_clusterId);
    }

    @Override
    public Collection<IClusterNode> getNodes() {
        Map<String,String> registry = getClusterRegistry();
        List<IClusterNode> nodes = Lists.newArrayListWithCapacity(registry.size());

        for(String key : registry.keySet()) {
            IClusterNode node = getClusterNode(key);
            if(node != null) {
                nodes.add(node);
            }
        }

        return nodes;
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
        if(reference != null) {
            String grp = OSGiUtils.getString(reference,Constants.SERVICE_GROUP);
            String id  = OSGiUtils.getString(reference,Constants.SERVICE_ID);

            if(StringUtils.isNotBlank(grp) && StringUtils.isNotBlank(id)) {
                ClusteredServiceGroup services = m_serviceGroups.get(grp);
                if(services != null) {
                    services.bind(reference);
                }
            }
        }
    }

    /**
     *
     * @param reference
     */
    @Override
    public void unbind(ServiceReference reference) {
        if(reference != null) {
            String grp = OSGiUtils.getString(reference,Constants.SERVICE_GROUP);
            String id  = OSGiUtils.getString(reference,Constants.SERVICE_ID);

            if(StringUtils.isNotBlank(grp) && StringUtils.isNotBlank(id)) {
                ClusteredServiceGroup services = m_serviceGroups.get(grp);
                if(services != null) {
                    services.unbind(reference);
                }
            }
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    private IMap<String,String> getClusterRegistry() {
        return getHazelcastManager().getMap(Constants.REGISTRY_NODES);
    }

    /**
     *
     * @param id
     * @return
     */
    private IClusterNode getClusterNode(String id) {
        String jsonData = getClusterRegistry().get(m_clusterId);
        return StringUtils.isNotBlank(jsonData)
            ? JsonUtils.decode(jsonData,DefaultClusterNode.class)
            : null;
    }

    /**
     *
     * @return
     */
    private IMap<String,String> getGroupRegistry() {
        return getHazelcastManager().getMap(Constants.REGISTRY_GROUPS);
    }
}
