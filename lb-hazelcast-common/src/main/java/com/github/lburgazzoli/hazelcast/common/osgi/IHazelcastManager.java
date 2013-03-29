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
package com.github.lburgazzoli.hazelcast.common.osgi;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.ITopic;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
public interface IHazelcastManager {
    /**
     *
     * @return
     */
    public HazelcastInstance getInstance();

    /**
     *
     * @return
     */
    public HazelcastNode getNode();

    /**
     *
     * @return
     */
    public Collection<HazelcastNode> listNodes();

    /**
     *
     * @param mapName
     * @param <K>
     * @param <V>
     * @return
     */
    public <K,V> Map<K,V> getMap(String mapName);

    /**
     *
     * @param listName
     * @param <T>
     * @return
     */
    public <T> List<T> getList(String listName);

    /**
     *
     * @param lockName
     * @return
     */
    public ILock getLock(String lockName);

    /**
     *
     * @param topicName
     * @param <E>
     * @return
     */
    public <E> ITopic<E> getTopic(String topicName);
}