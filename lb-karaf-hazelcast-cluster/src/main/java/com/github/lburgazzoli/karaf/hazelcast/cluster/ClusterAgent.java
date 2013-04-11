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

import com.github.lburgazzoli.cluster.IClusterAgent;
import com.github.lburgazzoli.cluster.IClusteredNode;
import com.github.lburgazzoli.cluster.IClusteredService;
import com.github.lburgazzoli.cluster.IClusteredServiceGroup;
import com.github.lburgazzoli.osgi.IOSGiLifeCycle;
import com.github.lburgazzoli.osgi.IOSGiServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class ClusterAgent implements IClusterAgent, IOSGiServiceListener, IOSGiLifeCycle, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterAgent.class);

    private ScheduledExecutorService m_scheduler;
    private ScheduledFuture<?> m_schedulerHander;
    private AtomicBoolean m_leader;
    private ClusterContext m_clusterContex;
    private String m_nodeId;
    private String m_nodeConfig;
    private boolean m_leaderEligible;
    private int m_leadershipCheckInterval;
    private int m_leadershipCheckDelay;

    /**
     * c-tor
     */
    public ClusterAgent() {
        m_schedulerHander = null;
        m_scheduler = Executors.newScheduledThreadPool(1);
        m_leader = new AtomicBoolean(false);
        m_clusterContex = null;
        m_nodeId = null;
        m_nodeConfig = null;
        m_leaderEligible = false;
        m_leadershipCheckInterval = 60;
        m_leadershipCheckDelay = 20;
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
     * @param nodeConfig
     */
    public void setNodeConfiguration(String nodeConfig) {
        m_nodeConfig = nodeConfig;
    }

    /**
     *
     * @param nodeId
     */
    public void setNodeId(String nodeId) {
        m_nodeId = nodeId;
    }

    /**
     *
     * @param leaderEligible
     */
    public void setLeaderEligible(boolean leaderEligible) {
        m_leaderEligible = leaderEligible;
    }

    /**
     *
     * @param leadershipCheckInterval
     */
    public void setLeadershipCheckInterval(int leadershipCheckInterval) {
        m_leadershipCheckInterval = leadershipCheckInterval;
    }

    /**
     *
     * @param leadershipCheckDelay
     */
    public void setLeadershipCheckDelay(int leadershipCheckDelay) {
        leadershipCheckDelay = leadershipCheckDelay;
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void init() {
        LOGGER.debug("Agent == init (eligible={})",m_leaderEligible);

        if(m_leaderEligible) {
            m_schedulerHander = m_scheduler.scheduleAtFixedRate(
                this,m_leadershipCheckDelay,m_leadershipCheckInterval,TimeUnit.SECONDS);
        }

        m_clusterContex.createNode(m_nodeId);
    }

    @Override
    public void destroy() {
        LOGGER.debug("Agent == destroy (eligible={})",m_leaderEligible);

        if(m_leaderEligible) {
            m_scheduler.shutdown();

            try {
                LOGGER.debug("Awaiting scheduler termination");
                m_scheduler.awaitTermination(60,TimeUnit.SECONDS);
                LOGGER.debug("Scheduler termination done");
            } catch (InterruptedException e) {
                LOGGER.warn("Exception",e);
            }
        }

        m_schedulerHander = null;

        try {
            LOGGER.debug("Agent == remove {} from nodes", m_nodeId);
            m_clusterContex.lock(m_nodeId);
            m_clusterContex.getClusterRegistry().remove(m_nodeId);
            LOGGER.debug("Agent == {} removed", m_nodeId);
        } finally {
            m_clusterContex.unlock(m_nodeId);
        }

        if(m_leaderEligible) {
            if(m_leader.get()) {
                m_clusterContex.unlock(ClusterConstants.LOCK_CLUSTER);
                m_leader.set(false);
            }
        }

        deactivate();
    }

    @Override
    public void run() {
        if(!m_leader.get()) {
            if(m_clusterContex.tryLock(ClusterConstants.LOCK_CLUSTER,1,TimeUnit.SECONDS)) {
                m_leader.set(true);
                activate();
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
    public Collection<IClusteredNode> getNodes() {
        return new ArrayList<IClusteredNode>();
        //return Utils.downCastCollection(
        //    m_clusterContex.getNodes(),IClusteredNode.class);
    }

    @Override
    public Collection<IClusteredServiceGroup> getServiceGroups() {
        return new ArrayList<IClusteredServiceGroup>();
        //return Utils.downCastCollection(
        //    m_clusterContex.getServiceGroups(),IClusteredServiceGroup.class);
    }

    @Override
    public Collection<IClusteredService> getServices() {
        return new ArrayList<IClusteredService>();
        //Utils.downCastCollection(
        //    m_clusterContex.getServices(),IClusteredService.class);
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
        /*
        String serviceId = (String)reference.getProperty(ClusteredServiceConstants.SERVICE_ID);
        if(StringUtils.isNotBlank(serviceId)) {
            ClusteredServiceProxy service = m_clusterContex.getService(serviceId);
            if(service != null) {
                String groupId = service.getGroupId();
                ClusteredServiceGroupProxy serviceGroup = m_clusterContex.getServiceGroup(groupId);
                if(serviceGroup != null) {
                }
            }
        }
        */
    }

    /**
     *
     * @param reference
     */
    @Override
    public void unbind(ServiceReference reference) {
        /*
        String serviceId = (String)reference.getProperty(ClusteredServiceConstants.SERVICE_ID);
        if(StringUtils.isNotBlank(serviceId)) {
            ClusteredServiceProxy service = m_clusterContex.getService(serviceId);
            if(service != null) {
                String groupId = service.getGroupId();
                ClusteredServiceGroupProxy serviceGroup = m_clusterContex.getServiceGroup(groupId);
                if(serviceGroup != null) {
                }
            }
        }
        */
    }
}
