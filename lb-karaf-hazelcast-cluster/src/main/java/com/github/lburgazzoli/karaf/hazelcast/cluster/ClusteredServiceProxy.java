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
import com.github.lburgazzoli.karaf.hazelcast.data.JsonDataProxy;
import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
public class ClusteredServiceProxy extends JsonDataProxy implements IClusteredService {

    /**
     * c-tor
     *
     * @param cacheKey
     * @param cacheData
     */
    public ClusteredServiceProxy(String cacheKey, IMap<String, String> cacheData) {
        super(cacheKey,cacheData);
        super.setValue(ClusterConstants.K_ITEM_TYPE, ClusterConstants.K_ITEM_TYPE_SERVICE);
    }

    public ClusteredServiceProxy setNodeId(String id) {
        setValue(ClusterConstants.K_NODE_ID, id);
        return this;
    }

    // *************************************************************************
    // IClusteredService
    // *************************************************************************

    @Override
    public String getNodeId() {
        return getValue(ClusterConstants.K_NODE_ID);
}

    public ClusteredServiceProxy setGroupId(String id) {
        setValue(ClusterConstants.K_GROUP_ID,id);
        return this;
    }

    @Override
    public String getGroupId() {
        return getValue(ClusterConstants.K_GROUP_ID);
    }

    public ClusteredServiceProxy setServiceId(String id) {
        setValue(ClusterConstants.K_SERVICE_ID,id);
        return this;
    }

    @Override
    public String getServiceId() {
        return getValue(ClusterConstants.K_SERVICE_ID);
    }

    public ClusteredServiceProxy setServiceStatus(String status) {
        setValue(ClusterConstants.K_SERVICE_STATUS,status);
        return this;
    }

    @Override
    public String getServiceStatus() {
        return getValue(ClusterConstants.K_SERVICE_STATUS);
    }

    // *************************************************************************
    //
    // *************************************************************************

    public boolean hasNode() {
        return StringUtils.isNotBlank(getNodeId());
    }

    public boolean hasGroup() {
        return StringUtils.isNotBlank(getGroupId());
    }
}
