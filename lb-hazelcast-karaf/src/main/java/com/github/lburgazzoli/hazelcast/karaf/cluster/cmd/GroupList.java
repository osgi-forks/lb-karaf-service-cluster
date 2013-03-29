package com.github.lburgazzoli.hazelcast.karaf.cluster.cmd;

import com.github.lburgazzoli.cluster.IClusterAgent;
import com.github.lburgazzoli.osgi.karaf.cmd.AbstractTabularCommand;
import com.github.lburgazzoli.osgi.karaf.cmd.ShellTable;
import org.apache.felix.gogo.commands.Command;

/**
 *
 */
@Command(scope = "cluster", name = "group-list", description = "List Nodes")
public class GroupList extends AbstractTabularCommand<IClusterAgent> {
    /**
     *
     */
    public GroupList() {
        super("IsLocal","GroupID","GroupState","NodeID");
    }

    @Override
    protected void doExecute(IClusterAgent service, ShellTable table) throws Exception {
    }
}
