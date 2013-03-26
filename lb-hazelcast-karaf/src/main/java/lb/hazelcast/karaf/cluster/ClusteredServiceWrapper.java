package lb.hazelcast.karaf.cluster;

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
