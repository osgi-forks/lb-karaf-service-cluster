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
package lb.hazelcast.common.osgi;

import com.google.common.collect.Maps;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 *
 */
public class HazelcastBundleClassLoader
    extends ClassLoader
{
    public static final Logger LOGGER =
        LoggerFactory.getLogger(HazelcastBundleClassLoader.class);

    private final ConcurrentMap<Long, Bundle> m_bundles;
    private final ConcurrentMap<String,Class<?>> m_classes;

    /**
     * c-tor
     */
    public HazelcastBundleClassLoader() {
        m_bundles = Maps.newConcurrentMap();
        m_classes = Maps.newConcurrentMap();
    }

    /**
     *
     */
    public void init() {
        m_bundles.clear();
        m_classes.clear();
    }

    /**
     *
     */
    public void destroy() {
        m_bundles.clear();
        m_classes.clear();
    }

    /**
     *
     * @param bundle
     */
    public void addBundle(Bundle bundle) {
        m_bundles.put(bundle.getBundleId(),bundle);
        m_classes.clear();
    }

    /**
     *
     * @param bundle
     */
    public void removeBundle(Bundle bundle) {
        m_bundles.remove(bundle.getBundleId());
        m_classes.clear();
    }

    // *************************************************************************
    //
    // *************************************************************************

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Class findClass(String name) throws ClassNotFoundException {
        Class<?> clazz = m_classes.get(name);
        if(clazz == null) {
            for(Map.Entry<Long,Bundle> entry : m_bundles.entrySet()) {
                try {
                    Bundle bundle = entry.getValue();
                    if( bundle.getState() == Bundle.ACTIVE   ||
                        bundle.getState() == Bundle.STARTING ) {
                        clazz = bundle.loadClass(name);
                        m_classes.putIfAbsent(name,clazz);

                        return clazz;
                    }
                } catch(ClassNotFoundException cnfe) {
                }
            }
        } else {
            return clazz;
        }

        throw new ClassNotFoundException(name);
    }

    @Override
    public URL getResource(String name) {
        for (Map.Entry<Long,Bundle> entry : m_bundles.entrySet()) {
            Bundle bundle = entry.getValue();
            if( bundle.getState() == Bundle.ACTIVE   ||
                bundle.getState() == Bundle.STARTING ) {
                URL url = bundle.getResource(name);
                if(url != null) {
                    return url;
                }
            }
        }

        return null;
    }
}
