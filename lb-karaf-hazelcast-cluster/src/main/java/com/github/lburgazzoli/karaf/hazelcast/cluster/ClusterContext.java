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

/**
 *
 */
public class ClusterContext extends HazelcastAwareObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterContext.class);

    private final Map<String,ClusteredNodeProxy> m_nodes;
    private final Map<String,ClusteredServiceGroupProxy> m_groups;
    private final Map<String,ClusteredServiceProxy> m_services;

    /**
     *
     */
    public ClusterContext() {
        m_nodes    = Maps.newConcurrentMap();
        m_groups   = Maps.newConcurrentMap();
        m_services = Maps.newConcurrentMap();
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param id
     * @return
     */
    public ClusteredNodeProxy getNode(String id) {
        if(!m_nodes.containsKey(id)) {
            if(!getClusterRegistry().containsKey(id)) {
                m_nodes.put(
                    id,
                    new ClusteredNodeProxy(id,getClusterRegistry())
                        .setNodeId(id)
                        .setNodeAddress(getHazelcastManager().getLocalAddress()));
            } else {
                m_nodes.put(
                    id,
                    new ClusteredNodeProxy(id,getClusterRegistry()));
            }
        }

        return m_nodes.get(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public ClusteredServiceGroupProxy getServiceGroup(String id) {
        if(!m_groups.containsKey(id)) {
            if(!getClusterRegistry().containsKey(id)) {
                m_groups.put(
                    id,
                    new ClusteredServiceGroupProxy(id, getClusterRegistry())
                        .setGroupStatus(Constants.GROUP_STATE_INACTIVE));
            } else {
                m_groups.put(
                    id,
                    new ClusteredServiceGroupProxy(id,getClusterRegistry()));
            }
        }

        return m_groups.get(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public ClusteredServiceProxy getService(String id) {
        if(!m_services.containsKey(id)) {
            if(!getClusterRegistry().containsKey(id)) {
                m_services.put(
                    id,
                    new ClusteredServiceProxy(id,getClusterRegistry())
                        .setServiceStatus(Constants.SERVICE_STATE_INACTIVE));
            } else {
                m_services.put(
                    id,
                    new ClusteredServiceProxy(id,getClusterRegistry()));
            }
        }

        return m_services.get(id);
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
        return getHazelcastMap(Constants.REGISTRY_NODES);
    }

    /**
     *
     * @return
     */
    public IMap<String,String> getGroupRegistry() {
        return getHazelcastMap(Constants.REGISTRY_GROUPS);
    }

    /**
     *
     * @return
     */
    public IMap<String,String> getLockRegistry() {
        return getHazelcastMap(Constants.REGISTRY_LOCKS);
    }
}
