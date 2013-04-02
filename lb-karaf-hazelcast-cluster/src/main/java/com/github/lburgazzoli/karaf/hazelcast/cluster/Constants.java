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

/**
 *
 */
public class Constants {
    public static final String REGISTRY_NODES  = "lb.karaf.cluster.registry.nodes";
    public static final String REGISTRY_GROUPS = "lb.karaf.cluster.registry.groups";
    public static final String SERVICE_ID      = "lb.karaf.cluster.service.id";
    public static final String SERVICE_GROUP   = "lb.karaf.cluster.service.group";
    public static final String SERVICE_RANK    = "lb.karaf.cluster.service.rank";
    public static final String CLUSTER_LOCK    = "lb.karaf.cluster.lock";

    public static final String GROUP_STATE_ACTIVE   = "ACTIVE";
    public static final String GROUP_STATE_INACTIVE = "INACTIVE";


    public static final String K_NODE_ID       = "node_id";
    public static final String K_NODE_ADDRESS  = "node_address";
    public static final String K_GROUP_ID      = "group_id";
    public static final String K_GROUP_STATUS  = "group_status";
}

