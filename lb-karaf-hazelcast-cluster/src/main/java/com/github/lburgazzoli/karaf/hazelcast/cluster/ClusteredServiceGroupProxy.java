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

import com.github.lburgazzoli.cluster.IClusteredService;
import com.github.lburgazzoli.cluster.IClusteredServiceGroup;
import com.github.lburgazzoli.karaf.hazelcast.data.JsonDataProxy;
import com.google.common.collect.Maps;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 *
 */
public class ClusteredServiceGroupProxy extends JsonDataProxy implements IClusteredServiceGroup {

    private Map<String,IClusteredService> m_services;

    /**
     * c-tor
     *
     * @param cacheKey
     * @param cacheData;
     */
    public ClusteredServiceGroupProxy(String cacheKey, IMap<String, String> cacheData) {
        super(cacheKey,cacheData);
        super.setValue(ClusterConstants.K_ITEM_TYPE, ClusterConstants.K_ITEM_TYPE_SERVICE_GROUP);

        m_services = Maps.newHashMap();
    }

    // *************************************************************************
    // IClusteredServiceGroup
    // *************************************************************************

    public ClusteredServiceGroupProxy setNodeId(String clusterId) {
        setValue(ClusterConstants.K_NODE_ID, clusterId);
        return this;
    }

    @Override
    public String getNodeId() {
        return getValue(ClusterConstants.K_NODE_ID);
    }

    public ClusteredServiceGroupProxy setGroupId(String id) {
        setValue(ClusterConstants.K_GROUP_ID,id);
        return this;
    }

    @Override
    public String getGroupId() {
        return getValue(ClusterConstants.K_GROUP_ID);
    }

    public ClusteredServiceGroupProxy setGroupStatus(String status) {
        setValue(ClusterConstants.K_GROUP_STATUS,status);
        return this;
    }

    @Override
    public String getGrpupStatus() {
        return getValue(ClusterConstants.K_GROUP_STATUS);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @return
     */
    public boolean hasNode() {
        return StringUtils.isNotBlank(getNodeId());
    }

    /**
     *
     * @param serviceId
     */
    public void registerService(String serviceId) {
        if(!m_services.containsKey(serviceId)) {
            m_services.put(serviceId,null);
        }
    }

    /**
     *
     * @param service
     */
    public void registerService(IClusteredService service) {
        if(m_services.containsKey(service.getServiceId())) {
            m_services.put(service.getServiceId(),service);
        }
    }

    /**
     *
     * @param serviceId
     */
    public void unregisterService(String serviceId) {
        if(m_services.containsKey(serviceId)) {
            m_services.put(serviceId,null);
        }
    }

    /**
     *
     * @param service
     */
    public void unregisterService(IClusteredService service) {
        if(m_services.containsKey(service.getServiceId())) {
            m_services.put(service.getServiceId(),null);
        }
    }
}
