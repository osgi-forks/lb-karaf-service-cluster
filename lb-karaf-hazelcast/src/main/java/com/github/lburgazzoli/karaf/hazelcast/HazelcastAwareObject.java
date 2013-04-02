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
package com.github.lburgazzoli.karaf.hazelcast;

import com.github.lburgazzoli.osgi.BundleContextAware;
import com.hazelcast.core.IMap;

/**
 *
 */
public class HazelcastAwareObject extends BundleContextAware {
    private IHazelcastManager m_hazelcastManager;

    /**
     *
     */
    public HazelcastAwareObject() {
        m_hazelcastManager = null;
    }

    /**
     *
     * @param hazelcastManager
     */
    public void setHazelcastManager(IHazelcastManager hazelcastManager) {
        m_hazelcastManager = hazelcastManager;
    }

    /**
     *
     * @return
     */
    public IHazelcastManager getHazelcastManager() {
        return m_hazelcastManager;
    }

    /**
     *
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    protected <K,V> IMap<K,V> getHazelcastMap(String key) {
        return m_hazelcastManager.getMap(key);
    }
}
