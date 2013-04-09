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

import com.github.lburgazzoli.karaf.hazelcast.HazelcastAwareObject;
import com.google.common.collect.Maps;
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ClusterContext extends HazelcastAwareObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterContext.class);

    private final Map<String,ClusteredNodeProxy> m_nodes;
    private final Map<String,ClusteredServiceGroupProxy> m_groups;
    private final Map<String,ClusteredServiceProxy> m_services;

    private String m_nodeId;

    /**
     *
     */
    public ClusterContext() {
        m_nodeId   = null;
        m_nodes    = Maps.newConcurrentMap();
        m_groups   = Maps.newConcurrentMap();
        m_services = Maps.newConcurrentMap();
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param nodeId
     */
    public void setNodeId(String nodeId){
        m_nodeId = nodeId;
    }

    /**
     *
     * @return
     */
    public String getNodeId(){
        return m_nodeId;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param nodeId
     * @return
     */
    public ClusteredNodeProxy createNode(String nodeId) {
        if(!m_nodes.containsKey(nodeId)) {
            if(!getClusterRegistry().containsKey(nodeId)) {
                m_nodes.put(
                    nodeId,
                    new ClusteredNodeProxy(nodeId,getClusterRegistry())
                        .setNodeId(nodeId)
                        .setNodeAddress(getHazelcastManager().getLocalAddress()));
            } else {
                m_nodes.put(
                    nodeId,
                    new ClusteredNodeProxy(nodeId,getClusterRegistry()));
            }
        }

        return m_nodes.get(nodeId);
    }

    /**
     *
     * @param groupId
     *
     * @return
     */
    public ClusteredServiceGroupProxy createServiceGroup(String groupId) {
        if(!m_groups.containsKey(groupId)) {
            if(!getClusterRegistry().containsKey(groupId)) {
                m_groups.put(
                    groupId,
                    new ClusteredServiceGroupProxy(groupId, getClusterRegistry())
                        .setGroupId(groupId)
                        .setGroupStatus(ClusterConstants.GROUP_STATE_INACTIVE));
            } else {
                m_groups.put(
                    groupId,
                    new ClusteredServiceGroupProxy(groupId,getClusterRegistry()));
            }
        }

        return m_groups.get(groupId);
    }

    /**
     *
     * @param groupId
     * @param serviceId
     *
     * @return
     */
    public ClusteredServiceProxy createService(String groupId,String serviceId) {
        if(!m_services.containsKey(serviceId)) {
            if(!getClusterRegistry().containsKey(serviceId)) {
                m_services.put(
                    serviceId,
                    new ClusteredServiceProxy(serviceId,getClusterRegistry())
                        .setGroupId(groupId)
                        .setServiceId(serviceId)
                        .setServiceStatus(ClusterConstants.SERVICE_STATE_INACTIVE));
            } else {
                m_services.put(
                    serviceId,
                    new ClusteredServiceProxy(serviceId,getClusterRegistry()));
            }
        }

        return m_services.get(serviceId);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param nodeId
     * @return
     */
    public ClusteredNodeProxy getNode(String nodeId) {
        return m_nodes.get(nodeId);
    }

    /**
     * @param groupId
     *
     * @return
     */
    public ClusteredServiceGroupProxy getServiceGroup(String groupId) {
        return m_groups.get(groupId);
    }

    /**
     * @param serviceId
     *
     * @return
     */
    public ClusteredServiceProxy getService(String serviceId) {
        return m_services.get(serviceId);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    public Collection<ClusteredNodeProxy> getNodes() {
        return m_nodes.values();
    }

    /**
     *
     * @return
     */
    public Collection<ClusteredServiceGroupProxy> getServiceGroups() {
        return m_groups.values();
    }

    /**
     *
     * @return
     */
    public Collection<ClusteredServiceProxy> getServices() {
        return m_services.values();
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    public IMap<String,String> getClusterRegistry() {
        return getHazelcastMap(ClusterConstants.REGISTRY_NODES);
    }

    /**
     *
     * @return
     */
    public IMap<String,String> getGroupRegistry() {
        return getHazelcastMap(ClusterConstants.REGISTRY_GROUPS);
    }

    /**
     *
     * @return
     */
    public IMap<String,String> getLockRegistry() {
        return getHazelcastMap(ClusterConstants.REGISTRY_LOCKS);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param key
     */
    public void lock(String key) {
        getLockRegistry().lock(key);
    }

    /**
     *
     * @param key
     */
    public boolean tryLock(String key,long value,TimeUnit unit) {
        return getLockRegistry().tryLock(key,value,unit);
    }

    /**
     *
     * @param key
     */
    public void unlock(String key) {
        getLockRegistry().unlock(key);
    }
}
