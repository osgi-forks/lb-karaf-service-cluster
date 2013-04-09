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

import com.github.lburgazzoli.cluster.IClusteredNode;
import com.github.lburgazzoli.karaf.hazelcast.data.JsonDataProxy;
import com.hazelcast.core.IMap;

import java.net.InetAddress;

/**
 *
 */
public class ClusteredNodeProxy extends JsonDataProxy implements IClusteredNode {
    /**
     * c-tor
     *
     * @param cacheKey
     * @param cacheData;
     */
    public ClusteredNodeProxy(String cacheKey,IMap<String,String> cacheData) {
        super(cacheKey,cacheData);
        super.setValue(ClusterConstants.K_ITEM_TYPE, ClusterConstants.K_ITEM_TYPE_NODE);
    }

    // *************************************************************************
    // IClusteredNode
    // *************************************************************************

    public ClusteredNodeProxy setNodeId(String id) {
        setValue(ClusterConstants.K_NODE_ID, id);
        return this;
    }

    @Override
    public String getNodeId() {
        return getValue(ClusterConstants.K_NODE_ID);
    }

    public ClusteredNodeProxy setNodeAddress(InetAddress address) {
        return setNodeAddress(address.getHostAddress());
    }

    public ClusteredNodeProxy setNodeAddress(String address) {
       setValue(ClusterConstants.K_NODE_ADDRESS,address);
       return this;
    }

    @Override
    public String getNodeAddress() {
        return getValue(ClusterConstants.K_NODE_ADDRESS);
    }
}
