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
package lb.hazelcast.karaf.cluster;

import lb.cluster.IClusteredService;
import lb.osgi.OSGiUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 *
 */
public class ClusteredServiceWrapper implements IClusteredService, Comparable<ClusteredServiceWrapper> {
    private final Integer m_serviceRanking;
    private final String m_serviceId;
    private final IClusteredService m_clusteredService;
    private final ServiceReference m_serviceReference;
    private final BundleContext m_bundleContext;

    /**
     * c-tor
     *
     * @param bundleContext
     * @param serviceReference
     */
    public ClusteredServiceWrapper(BundleContext bundleContext, ServiceReference serviceReference) {
        m_bundleContext    = bundleContext;
        m_serviceReference = serviceReference;
        m_clusteredService = (IClusteredService)m_bundleContext.getService(m_serviceReference);
        m_serviceRanking   = OSGiUtils.getInteger(m_serviceReference, Constants.SERVICE_RANK);
        m_serviceId        = OSGiUtils.getString(m_serviceReference, Constants.SERVICE_ID);
    }

        /**
         *
         * @param serviceId
         * @return
         */
    public boolean is(String serviceId) {
        return StringUtils.equals(m_serviceId, serviceId);
    }

    /**
     *
     */
    @Override
    public void activate() {
        m_clusteredService.activate();
    }

    /**
     *
     */
    @Override
    public void deactivate() {
        m_clusteredService.deactivate();
    }

    /**
     *
     * @return
     */
    public void unget() {
        m_bundleContext.ungetService(m_serviceReference);
    }

    /**
     *
     * @param service
     * @return
     */
    @Override
    public int compareTo(ClusteredServiceWrapper service) {
        return m_serviceRanking.compareTo(service.m_serviceRanking) ;
    }
}
