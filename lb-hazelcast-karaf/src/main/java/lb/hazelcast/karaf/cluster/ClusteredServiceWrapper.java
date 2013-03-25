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

import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.ServiceReference;

/**
 *
 */
public class ClusteredServiceWrapper implements IClusteredService, Comparable<ClusteredServiceWrapper> {
    private String m_group;
    private String m_id;
    private Integer m_serviceRank;
    private IClusteredService m_clusteredService;
    private ServiceReference m_serviceReference;

    /**
     *
     * @param group
     * @param id
     * @param clusteredService
     * @param serviceRank
     * @param serviceReference
     */
    public ClusteredServiceWrapper(
        String group,String id,IClusteredService clusteredService,int serviceRank,ServiceReference serviceReference) {
        m_group = group;
        m_id = id;
        m_clusteredService = clusteredService;
        m_serviceRank = serviceRank;
        m_serviceReference = serviceReference;
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
     * @param o
     * @return
     */
    @Override
    public int compareTo(ClusteredServiceWrapper o) {
        return m_serviceRank.compareTo(o.m_serviceRank) ;
    }

    /**
     *
     * @return
     */
    public ServiceReference getServiceReference() {
        return m_serviceReference;
    }

    /**
     *
     * @param group
     * @param id
     * @return
     */
    public boolean is(String group,String id) {
        return StringUtils.equals(m_group,group) && StringUtils.equals(m_id,id);
    }

    /**
     *
     * @return
     */
    public IClusteredService getDelegate() {
        return m_clusteredService;
    }
}
