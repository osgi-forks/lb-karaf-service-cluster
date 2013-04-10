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
import com.github.lburgazzoli.karaf.hazelcast.data.JsonDataProxy;
import com.google.common.collect.ImmutableMap;
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
            new JsonDataProxy(nodeId,getClusterRegistry()).setValues(
                ImmutableMap.of(
                    ClusterConstants.K_NODE_ID,
                        nodeId,
                    ClusterConstants.K_NODE_ADDRESS,
                        getHazelcastManager().getLocalAddress().getHostAddress()
                )
            );
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
        if(!getServiceGroupRegistry().containsKey(groupId)) {
            new JsonDataProxy(nodeId,getServiceGroupRegistry()).setValues(
                ImmutableMap.of(
                    ClusterConstants.K_NODE_ID,
                        nodeId,
                    ClusterConstants.K_GROUP_ID,
                        groupId,
                    ClusterConstants.K_GROUP_STATUS,
                        ClusterConstants.GROUP_STATUS_UNKNOWN
                )
            );
        }

        return new ClusteredServiceGroupProxy(groupId, getServiceGroupRegistry());
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
        if(!getServiceRegistry().containsKey(serviceId)) {
            new JsonDataProxy(nodeId,getServiceGroupRegistry()).setValues(
                ImmutableMap.of(
                    ClusterConstants.K_NODE_ID,
                        nodeId,
                    ClusterConstants.K_GROUP_ID,
                        groupId,
                    ClusterConstants.K_SERVICE_ID,
                        serviceId,
                    ClusterConstants.K_SERVICE_STATUS,
                        ClusterConstants.SERVICE_STATUS_UNKNOWN
                )
            );
        }

        return new ClusteredServiceProxy(serviceId,getServiceRegistry());
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
    public IMap<String,String> getServiceGroupRegistry() {
        return getHazelcastMap(ClusterConstants.REGISTRY_GROUPS);
    }

    /**
     *
     * @return
     */
    public IMap<String,String> getServiceRegistry() {
        return getHazelcastMap(ClusterConstants.REGISTRY_SERVICES);
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
