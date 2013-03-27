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

import com.google.common.collect.Maps;
import lb.hazelcast.common.osgi.HazelcastAwareObject;
import lb.osgi.IOSGiServiceListener;
import lb.osgi.OSGiUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.ServiceReference;

import java.util.Map;

/**
 *
 */
public class ClusteredServiceManager
    extends HazelcastAwareObject
    implements IOSGiServiceListener, IClusteredServiceManager {

    private Map<String,ClusteredServiceGroup> m_serviceGroups;

    /**
     * c-tor
     */
    public ClusteredServiceManager() {
        m_serviceGroups = Maps.newConcurrentMap();

    }

    /**
     *
     * @param reference
     */
    @Override
    public void bind(ServiceReference reference) {
        if(reference != null) {
            String grp = OSGiUtils.getString(reference,Constants.SERVICE_GROUP);
            String id  = OSGiUtils.getString(reference,Constants.SERVICE_ID);

            if(StringUtils.isNotBlank(grp) && StringUtils.isNotBlank(id)) {
                ClusteredServiceGroup services = m_serviceGroups.get(grp);
                if(services == null) {
                    services = new ClusteredServiceGroup(getBundleContext(),grp);
                    m_serviceGroups.put(grp,services);
                }

                services.bind(reference);
            }
        }
    }

    /**
     *
     * @param reference
     */
    @Override
    public void unbind(ServiceReference reference) {
        if(reference != null) {
            String grp = OSGiUtils.getString(reference,Constants.SERVICE_GROUP);
            String id  = OSGiUtils.getString(reference,Constants.SERVICE_ID);

            if(StringUtils.isNotBlank(grp) && StringUtils.isNotBlank(id)) {
                ClusteredServiceGroup services = m_serviceGroups.get(grp);
                if(services != null) {
                    services.unbind(reference);
                }
            }
        }
    }
}
