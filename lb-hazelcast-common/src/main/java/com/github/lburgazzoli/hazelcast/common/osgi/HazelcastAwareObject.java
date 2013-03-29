package com.github.lburgazzoli.hazelcast.common.osgi;

import org.osgi.framework.BundleContext;

/**
 *
 */
public class HazelcastAwareObject {
    private BundleContext m_bundleContext;
    private IHazelcastManager m_hazelcastManager;

    /**
     *
     */
    public HazelcastAwareObject() {
        m_hazelcastManager = null;
        m_bundleContext = null;
    }

    /**
     *
     * @param bundleContext
     */
    public void setBundleContext(BundleContext bundleContext) {
        m_bundleContext = bundleContext;
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
    public BundleContext getBundleContext() {
        return m_bundleContext;
    }

    /**
     *
     * @return
     */
    public IHazelcastManager getHazelcastManager() {
        return m_hazelcastManager;
    }
}
