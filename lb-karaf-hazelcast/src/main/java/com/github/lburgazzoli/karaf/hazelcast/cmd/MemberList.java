package com.github.lburgazzoli.karaf.hazelcast.cmd;

import com.github.lburgazzoli.karaf.hazelcast.IHazelcastManager;
import com.github.lburgazzoli.osgi.karaf.cmd.AbstractTabularCommand;
import com.github.lburgazzoli.osgi.karaf.cmd.ShellTable;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.Member;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.gogo.commands.Command;

import java.util.Collection;


/**
 *
 */
@Command(scope = "hz", name = "member-list", description = "List Hazelcast Members")
public class MemberList extends AbstractTabularCommand<IHazelcastManager> {
    /**
     *
     */
    public MemberList() {
        super("MemberID","MemberAddress","IsLocal","IsLite","InstanceName");
    }

    @Override
    protected void doExecute(IHazelcastManager service, ShellTable table) throws Exception {
        Cluster cluster = service.getInstance().getCluster();
        Collection<Member> members = cluster.getMembers();

        if(members != null) {
            for(Member member : members) {
                table.addRow(
                    member.getUuid(),
                    String.format("%s:%d",
                        member.getInetSocketAddress().getHostString(),
                        member.getInetSocketAddress().getPort()),
                    member.localMember()  ? "*" : StringUtils.EMPTY,
                    member.isLiteMember() ? "Y" : "N");
            }
        }
    }
}