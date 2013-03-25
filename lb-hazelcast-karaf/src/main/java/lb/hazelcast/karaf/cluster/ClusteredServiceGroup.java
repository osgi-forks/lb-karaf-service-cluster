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

import com.google.common.collect.Sets;
import lb.osgi.IOSGiServiceListener;
import lb.osgi.OSGiUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.Set;

/**
 *
 */
public class ClusteredServiceGroup implements IClusteredObject, IOSGiServiceListener{

    /**
     *
     */
    private class ServiceWrapper implements IClusteredService, Comparable<ServiceWrapper> {
        private final Integer m_serviceRanking;
        private final String m_serviceId;
        private final IClusteredService m_clusteredService;
        private final ServiceReference m_serviceReference;

        /**
         * c-tor
         *
         * @param serviceRanking
         * @param serviceId
         * @param clusteredService
         * @param serviceReference
         */
        public ServiceWrapper(
            Integer serviceRanking,String serviceId,IClusteredService clusteredService,ServiceReference serviceReference) {
            m_serviceRanking = serviceRanking;
            m_serviceId = serviceId;
            m_clusteredService = clusteredService;
            m_serviceReference = serviceReference;
        }

        /**
         *
         * @param serviceId
         * @return
         */
        public boolean is(String serviceId) {
            return StringUtils.equals(m_serviceId,serviceId);
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
         * @param service
         * @return
         */
        @Override
        public int compareTo(ServiceWrapper service) {
            return m_serviceRanking.compareTo(service.m_serviceRanking) ;
        }

        /**
         *
         * @return
         */
        public ServiceReference getServiceReference() {
            return m_serviceReference;
        }

    }

    private BundleContext m_bundleContext;
    private String m_groupId;
    private Set<ServiceWrapper> m_services;

    /**
     * c-tor
     *
     * @param bundelContext
     * @param groupId
     */
    public ClusteredServiceGroup(BundleContext bundelContext,String groupId) {
        m_bundleContext = bundelContext;
        m_groupId = m_groupId;
        m_services = Sets.newTreeSet();
    }

    /**
     *
     */
    @Override
    public void activate() {
    }

    /**
     *
     */
    @Override
    public void deactivate() {
    }

    /**
     *
     * @param reference
     */
    @Override
    public void bind(ServiceReference reference) {
        String  grp  = OSGiUtils.getString(reference,Constants.SERVICE_GROUP);
        String  id   = OSGiUtils.getString(reference,Constants.SERVICE_ID);
        Integer rank = OSGiUtils.getInteger(reference, Constants.SERVICE_RANK);

        if(StringUtils.isNotBlank(grp) && StringUtils.isNotBlank(id)) {
            m_bundleContext.getService(reference);

            m_services.add(
                new ServiceWrapper(
                    rank,
                    id,
                    (IClusteredService)m_bundleContext.getService(reference),reference)
            );
        }
    }

    /**
     *
     * @param reference
     */
    @Override
    public void unbind(ServiceReference reference) {
        String  grp  = OSGiUtils.getString(reference,Constants.SERVICE_GROUP);
        String  id   = OSGiUtils.getString(reference,Constants.SERVICE_ID);
        Integer rank = OSGiUtils.getInteger(reference,Constants.SERVICE_RANK);

        if(StringUtils.isNotBlank(grp) && StringUtils.isNotBlank(id)) {
            ServiceWrapper sw = findService(id);
            if(sw != null) {
                m_services.remove(sw);
                m_bundleContext.ungetService(sw.getServiceReference());
            }
        }
    }

    /**
     *
     * @param serviceId
     * @return
     */
    private ServiceWrapper findService(String serviceId) {
        ServiceWrapper wrapper = null;
        for(ServiceWrapper sw : m_services) {
            if(sw.is(serviceId)) {
                wrapper = sw;
                break;
            }
        }

        return wrapper;
    }
}
