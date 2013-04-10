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
import com.hazelcast.core.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class ClusterContext extends HazelcastAwareObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterContext.class);



    /**
     *
     */
    public ClusterContext() {
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
        if(!getClusterRegistry().containsKey(nodeId)) {
            new ClusteredNodeProxy(nodeId,getClusterRegistry())
                .setNodeId(nodeId)
                .setNodeAddress(getHazelcastManager().getLocalAddress());
        }

        return new ClusteredNodeProxy(nodeId,getClusterRegistry());
    }

    /**
     *
     * @param nodeId
     * @param groupId
     *
     * @return
     */
    public ClusteredServiceGroupProxy createServiceGroup(String nodeId,String groupId) {
        if(!getClusterRegistry().containsKey(groupId)) {
            new ClusteredServiceGroupProxy(groupId, getClusterRegistry())
                .setNodeId(nodeId)
                .setGroupId(groupId)
                .setGroupStatus(ClusterConstants.GROUP_STATE_INACTIVE);
        }

        return new ClusteredServiceGroupProxy(groupId, getClusterRegistry());
    }

    /**
     *
     * @param nodeId
     * @param groupId
     * @param serviceId
     *
     * @return
     */
    public ClusteredServiceProxy createService(String nodeId,String groupId,String serviceId) {
        if(!getClusterRegistry().containsKey(serviceId)) {
            new ClusteredServiceProxy(serviceId,getClusterRegistry())
                .setNodeId(nodeId)
                .setGroupId(groupId)
                .setServiceId(serviceId)
                .setServiceStatus(ClusterConstants.SERVICE_STATE_INACTIVE);
        }

        return new ClusteredServiceProxy(serviceId,getClusterRegistry());
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
        return null;
    }

    /**
     * @param groupId
     *
     * @return
     */
    public ClusteredServiceGroupProxy getServiceGroup(String groupId) {
        return null;
    }

    /**
     * @param serviceId
     *
     * @return
     */
    public ClusteredServiceProxy getService(String serviceId) {
        return null;
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    public Collection<ClusteredNodeProxy> getNodes() {
        return new ArrayList<ClusteredNodeProxy>();
    }

    /**
     *
     * @return
     */
    public Collection<ClusteredServiceGroupProxy> getServiceGroups() {
        return new ArrayList<ClusteredServiceGroupProxy>();
    }

    /**
     *
     * @return
     */
    public Collection<ClusteredServiceProxy> getServices() {
        return new ArrayList<ClusteredServiceProxy>();
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
