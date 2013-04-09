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
package com.github.lburgazzoli.karaf.hazelcast.data;

import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Map;

/**
 *
 */
public class JsonDataProxy {
    private final String m_cacheKey;
    private final IMap<String,String> m_cacheData;

    /**
     * c-tor
     *
     * @param cacheKey
     * @param cacheData;
     */
    public JsonDataProxy(String cacheKey, IMap<String, String> cacheData) {
        m_cacheKey = cacheKey;
        m_cacheData = cacheData;
    }

    /**
     *
     * @param key
     * @return
     */
    protected String getValue(String key) {
        String result = StringUtils.EMPTY;
        try {
            m_cacheData.lock(m_cacheKey);

            Map<String,String> values = getCachedMap();
            if(values.containsKey(key)) {
                result = values.get(key);
            }
        } finally {
            m_cacheData.unlock(m_cacheKey);
        }

        return result;
    }

    /**
     *
     * @param key
     * @param val
     */
    protected void setValue(String key,String val) {
        if(StringUtils.isNotBlank(key) && StringUtils.isNotBlank(val)) {
            try {
                m_cacheData.lock(m_cacheKey);

                Map<String,String> values = getCachedMap();
                values.put(key,val);

                String cacheData = JsonUtils.encode(values);
                if(StringUtils.isNotBlank(cacheData)) {
                    m_cacheData.put(m_cacheKey,cacheData);
                }
            } finally {
                m_cacheData.unlock(m_cacheKey);
            }
        }
    }

    /**
     *
     * @return
     */
    protected Map<String,String> getCachedMap() {
        return JsonUtils.decode(m_cacheData.get(m_cacheKey));
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }

        JsonDataProxy rhs = (JsonDataProxy) obj;
        return new EqualsBuilder()
            .appendSuper(super.equals(obj))
            .append(m_cacheKey, rhs.m_cacheKey)
            .isEquals();
    }
}
