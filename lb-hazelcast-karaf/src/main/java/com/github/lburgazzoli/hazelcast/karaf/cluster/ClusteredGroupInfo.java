package com.github.lburgazzoli.hazelcast.karaf.cluster;

/**
 *
 */
public class ClusteredGroupInfo {
    private String m_groupId;
    private String m_grpupState;
    private String m_clusterId;

    /**
     * c-tor
     */
    public ClusteredGroupInfo() {
        this(null,null,null);
    }

    /**
     * c-tor
     *
     * @param groupId
     * @param grpupState
     * @param clusterId
     */
    public ClusteredGroupInfo(String groupId, String grpupState, String clusterId) {
        m_groupId = groupId;
        m_grpupState = grpupState;
        m_clusterId = clusterId;
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
     * @param groupId
     */
    public void setGroupId(String groupId) {
        m_groupId = groupId;
    }

    /**
     *
     * @return
     */
    public String getGrpupState() {
        return m_grpupState;
    }

    /**
     *
     * @param grpupState
     */
    public void setGrpupState(String grpupState) {
        m_grpupState = grpupState;
    }

    /**
     *
     * @return
     */
    public String getClusterId() {
        return m_clusterId;
    }

    /**
     *
     * @param clusterId
     */
    public void setClusterId(String clusterId) {
        m_clusterId = clusterId;
    }
}
