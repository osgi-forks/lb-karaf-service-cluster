package com.github.lburgazzoli.hazelcast.karaf.cluster.cmd;

import com.github.lburgazzoli.cluster.IClusterAgent;
import com.github.lburgazzoli.cluster.IClusteredServiceGroup;
import com.github.lburgazzoli.osgi.karaf.cmd.AbstractTabularCommand;
import com.github.lburgazzoli.osgi.karaf.cmd.ShellTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.gogo.commands.Command;

import java.util.Collection;

/**
 *
 */
@Command(scope = "cluster", name = "group-list", description = "List Groups")
public class GroupList extends AbstractTabularCommand<IClusterAgent> {
    /**
     *
     */
    public GroupList() {
        super("IsLocal","NodeID","GroupID","GroupState");
    }

    @Override
    protected void doExecute(IClusterAgent service, ShellTable table) throws Exception {
        Collection<IClusteredServiceGroup> groups = service.getServiceGroups();
        if(groups != null) {
            for(IClusteredServiceGroup group : groups) {
                table.addRow(
                    StringUtils.equals(group.getNodeId(), service.getId()) ? "*" : "",
                    group.getNodeId(),
                    group.getGroupId(),
                    group.getGrpupStatus());
            }
        }
    }
}
