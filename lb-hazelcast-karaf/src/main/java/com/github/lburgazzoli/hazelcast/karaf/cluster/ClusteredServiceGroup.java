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
package com.github.lburgazzoli.hazelcast.karaf.cluster;

import com.github.lburgazzoli.cluster.IClusteredObject;
import com.github.lburgazzoli.osgi.IOSGiServiceListener;
import com.github.lburgazzoli.osgi.OSGiUtils;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 *
 */
public class ClusteredServiceGroup implements IClusteredObject, IOSGiServiceListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusteredServiceGroup.class);

    private BundleContext m_bundleContext;
    private String m_groupId;
    private Set<ClusteredServiceWrapper> m_services;

    /**
     * c-tor
     *
     * @param bundelContext
     * @param groupId
     */
    public ClusteredServiceGroup(BundleContext bundelContext,String groupId) {
        m_groupId = groupId;
        m_bundleContext = bundelContext;
        m_services = Sets.newTreeSet();
    }

    /**
     *
     * @return
     */
    public String getGroupId() {
        return m_groupId;
    }

    /**
     *
     */
    @Override
    public void activate() throws Exception {
        LOGGER.debug("Activate <{}>",m_groupId);
    }

    /**
     *
     */
    @Override
    public void deactivate() throws Exception {
        LOGGER.debug("Deactivate <{}>",m_groupId);
    }

    /**
     *
     * @param reference
     */
    @Override
    public void bind(ServiceReference reference) {
        String grp = OSGiUtils.getString(reference,Constants.SERVICE_GROUP);
        String id  = OSGiUtils.getString(reference,Constants.SERVICE_ID);

        if(StringUtils.isNotBlank(grp) && StringUtils.isNotBlank(id)) {
            if(StringUtils.equals(m_groupId,grp)) {
                m_services.add(new ClusteredServiceWrapper(m_bundleContext,reference));
            }
        }
    }

    /**
     *
     * @param reference
     */
    @Override
    public void unbind(ServiceReference reference) {
        String grp = OSGiUtils.getString(reference,Constants.SERVICE_GROUP);
        String id  = OSGiUtils.getString(reference,Constants.SERVICE_ID);

        if(StringUtils.isNotBlank(grp) && StringUtils.isNotBlank(id)) {
            if(StringUtils.equals(m_groupId,grp)) {
                ClusteredServiceWrapper service = findService(id);
                if(service != null) {
                    service.unget();
                    m_services.remove(service);
                }
            }
        }
    }

    /**
     *
     * @param serviceId
     * @return
     */
    private ClusteredServiceWrapper findService(String serviceId) {
        ClusteredServiceWrapper wrapper = null;
        for(ClusteredServiceWrapper sw : m_services) {
            if(sw.is(serviceId)) {
                wrapper = sw;
                break;
            }
        }

        return wrapper;
    }
}
