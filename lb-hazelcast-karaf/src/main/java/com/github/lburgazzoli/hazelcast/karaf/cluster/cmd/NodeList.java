package com.github.lburgazzoli.hazelcast.karaf.cluster.cmd;

import com.github.lburgazzoli.cluster.IClusterAgent;
import com.github.lburgazzoli.cluster.IClusterNode;
import com.github.lburgazzoli.osgi.karaf.cmd.AbstractTabularCommand;
import com.github.lburgazzoli.osgi.karaf.cmd.ShellTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.gogo.commands.Command;

import java.util.Collection;

/**
 *
 */
@Command(scope = "cluster", name = "node-list", description = "List Nodes")
public class NodeList extends AbstractTabularCommand<IClusterAgent> {
    /**
     *
     */
    public NodeList() {
        super("IsLocal","NodeID","HostName");
    }

    @Override
    protected void doExecute(IClusterAgent service, ShellTable table) throws Exception {
        Collection<IClusterNode> nodes = service.getNodes();
        if(nodes != null) {
            for(IClusterNode node : nodes) {
                table.addRow(
                    StringUtils.equals(node.getId(),service.getId()) ? "*" : "",
                    node.getId(),
                    node.getAddress());
            }
        }
    }
}
