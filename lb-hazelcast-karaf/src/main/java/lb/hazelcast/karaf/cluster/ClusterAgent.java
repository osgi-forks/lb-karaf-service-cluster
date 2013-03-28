package lb.hazelcast.karaf.cluster;

import com.google.common.collect.Maps;
import com.hazelcast.core.ILock;
import lb.cluster.IClusterAgent;
import lb.hazelcast.common.osgi.HazelcastAwareObject;
import lb.osgi.IOSGiLifeCycle;
import lb.osgi.IOSGiServiceListener;
import lb.osgi.OSGiUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 */
public class ClusterAgent
    extends HazelcastAwareObject
    implements IClusterAgent, IOSGiServiceListener, IOSGiLifeCycle, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterAgent.class);

    private String m_clusterId;
    private Map<String,ClusteredServiceGroup> m_serviceGroups;

    private ILock m_clusterLock;
    private ScheduledExecutorService m_scheduler;
    private ScheduledFuture<?> m_schedulerHander;
    private AtomicBoolean m_leader;

    /**
     *
     */
    public ClusterAgent() {
        m_clusterId       = null;
        m_clusterLock     = null;
        m_schedulerHander = null;
        m_serviceGroups   = Maps.newConcurrentMap();
        m_scheduler       = Executors.newScheduledThreadPool(1);
        m_leader          = new AtomicBoolean(false);
    }

    // *************************************************************************
    //
    // *************************************************************************

    /**
     *
     * @param clusterId
     */
    public void setClusterId(String clusterId) {
        m_clusterId = clusterId;
    }

    /**
     *
     * @param clusterGroups
     */
    public void setClusterGroups(String clusterGroups) {
        String[] groups = StringUtils.split(clusterGroups,',');
        for(String group : groups) {
            m_serviceGroups.put(group,new ClusteredServiceGroup(getBundleContext(),group));
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public void init() {
        m_clusterLock = getHazelcastManager().getLock(Constants.CLUSTER_LOCK);
        m_schedulerHander = m_scheduler.scheduleAtFixedRate(this,30,60,TimeUnit.SECONDS);
    }

    @Override
    public void destroy() {
        if(m_clusterLock != null) {
            if(m_clusterLock.isLocked()) {
                m_clusterLock.forceUnlock();
            }

            m_clusterLock = null;
        }

        m_scheduler.shutdown();

        try {
            LOGGER.debug("Awaiting termination");
            m_scheduler.awaitTermination(60,TimeUnit.SECONDS);
            LOGGER.debug("Termination done");
        } catch (InterruptedException e) {
            LOGGER.warn("Exception",e);
        }

        m_schedulerHander = null;

        deactivate();
    }

    @Override
    public void run() {
        if(!m_leader.get()) {
            try {
                if(m_clusterLock.tryLock(1,TimeUnit.SECONDS)) {
                    activate();
                }
            } catch (InterruptedException e) {
                LOGGER.warn("Exception",e);
            }
        }
    }

    // *************************************************************************
    //
    // *************************************************************************

    private void activate() {
        m_leader.set(true);

        LOGGER.debug("==== ACTIVATE ====");
        LOGGER.debug("Node <{}> is now the leader", m_clusterId);

        for(ClusteredServiceGroup csg : m_serviceGroups.values()) {
            csg.activate();
        }
    }

    private void deactivate() {
        LOGGER.debug("==== DEACTIVATE ====");
        LOGGER.debug("Node <{}> is not more the leader",m_clusterId);

        for(ClusteredServiceGroup csg : m_serviceGroups.values()) {
            csg.deactivate();
        }

        m_leader.set(false);
    }

    // *************************************************************************
    //
    // *************************************************************************

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
                if(services != null) {
                    services.bind(reference);
                }
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