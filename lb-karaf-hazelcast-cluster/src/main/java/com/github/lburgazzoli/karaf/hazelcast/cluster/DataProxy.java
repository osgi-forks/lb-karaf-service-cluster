package com.github.lburgazzoli.karaf.hazelcast.cluster;

import com.hazelcast.core.IMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 *
 */
public class DataProxy {
    private final String m_cacheKey;
    private final IMap<String,String> m_cacheData;

    /**
     * c-tor
     *
     * @param cacheKey
     * @param cacheData;
     */
    public DataProxy(String cacheKey,IMap<String,String> cacheData) {
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
        try {
            m_cacheData.lock(m_cacheKey);

            Map<String,String> values = getCachedMap();
            values.put(key,val);

            String cacheData = DataUtils.encode(values);
            if(StringUtils.isNotBlank(cacheData)) {
                m_cacheData.put(m_cacheKey,cacheData);
            }
        } finally {
            m_cacheData.unlock(m_cacheKey);
        }
    }

    /**
     *
     * @return
     */
    protected Map<String,String> getCachedMap() {
        return DataUtils.decode(m_cacheData.get(m_cacheKey));
    }
}
