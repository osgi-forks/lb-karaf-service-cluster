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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class HazelcastBundleListener implements SynchronousBundleListener {

    public static final Logger LOGGER =
        LoggerFactory.getLogger(HazelcastBundleListener.class);

    private BundleContext m_bundleContext;
    private HazelcastBundleClassLoader m_classLoader;

    /**
     * c-tor
     *
     * @param bundleContext
     * @param classLoader
     */
    public HazelcastBundleListener(
        BundleContext bundleContext,
        HazelcastBundleClassLoader classLoader) {
        m_bundleContext = bundleContext;
        m_classLoader   = classLoader;
    }

    /**
     *
     */
    public void scanExistingBundles() {
        Bundle[] bundles = m_bundleContext.getBundles();
        for(Bundle bundle : bundles) {
            if(isBundleEligible(bundle)) {
                m_classLoader.addBundle(bundle);
            }
        }
    }

    /**
     *
     * @param event
     */
    @Override
    public void bundleChanged(BundleEvent event) {
        switch (event.getType()) {
            case BundleEvent.STARTING:
            case BundleEvent.STARTED:
                if(isBundleEligible(event.getBundle())) {
                    m_classLoader.addBundle(event.getBundle());
                }
                break;
            case BundleEvent.STOPPING:
            case BundleEvent.STOPPED:
            case BundleEvent.RESOLVED:
            case BundleEvent.UNINSTALLED:
                if(isBundleEligible(event.getBundle())) {
                    m_classLoader.removeBundle(event.getBundle());
                }
                break;
        }
    }

    /**
     *
     * @param bundle
     * @return
     */
    public boolean isBundleEligible(Bundle bundle) {
        if(bundle != null) {
            return bundle.getSymbolicName().startsWith("bt");
        }

        return false;
    }
}
