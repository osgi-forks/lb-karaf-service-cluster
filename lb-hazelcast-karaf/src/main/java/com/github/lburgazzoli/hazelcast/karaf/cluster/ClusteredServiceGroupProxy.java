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

import com.github.lburgazzoli.cluster.IClusterNode;
import com.github.lburgazzoli.cluster.IClusteredServiceGroup;
import com.hazelcast.core.IMap;

/**
 *
 */
public class ClusteredServiceGroupProxy extends DataProxy implements IClusteredServiceGroup {
    /**
     * c-tor
     *
     * @param cacheKey
     * @param cacheData;
     */
    public ClusteredServiceGroupProxy(String cacheKey, IMap<String, String> cacheData) {
        super(cacheKey,cacheData);
    }

    public ClusteredServiceGroupProxy setGroupId(String id) {
        setValue(Constants.K_GROUP_ID,id);
        return this;
    }

    @Override
    public String getGroupId() {
        return getValue(Constants.K_GROUP_ID);
    }

    public ClusteredServiceGroupProxy setGroupStatus(String status) {
        setValue(Constants.K_GROUP_STATUS,status);
        return this;
    }

    @Override
    public String getGrpupStatus() {
        return getValue(Constants.K_GROUP_STATUS);
    }

    public ClusteredServiceGroupProxy setNodeId(String clusterId) {
        setValue(Constants.K_NODE_ID,clusterId);
        return this;
    }

    @Override
    public String getNodeId() {
        return getValue(Constants.K_NODE_ID);
    }
}
