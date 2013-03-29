package com.github.lburgazzoli.cluster;

/**
 *
 */
public class DefaultClusterNode implements IClusterNode {
    private String m_nodeId;
    private String m_address;

    /**
     *
     */
    public DefaultClusterNode() {
        this(null,null);
    }

    /**
     * c-tor
     *
     * @param nodeId
     * @param address
     */
    public DefaultClusterNode(String nodeId,String address) {
        m_nodeId = nodeId;
        m_address = address;
    }

    public void setId(String id) {
        m_nodeId = id;
    }

    @Override
    public String getId() {
        return m_nodeId;
    }

    public void setAddress(String address) {
        m_address = address;
    }

    @Override
    public String getAddress() {
        return m_address;
    }
}
