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
package com.github.lburgazzoli.karaf.hazelcast.cmd;

import com.github.lburgazzoli.karaf.hazelcast.IHazelcastManager;
import com.github.lburgazzoli.osgi.karaf.cmd.AbstractTabularCommand;
import com.github.lburgazzoli.osgi.karaf.cmd.ShellTable;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.Member;
import org.apache.felix.gogo.commands.Command;

import java.util.Collection;


/**
 *
 */
@Command(scope = "hz", name = "member-list", description = "List Hazelcast Members")
public class MemberList extends AbstractTabularCommand<IHazelcastManager> {
    /**
     * c-tor
     */
    public MemberList() {
        super("MemberID","MemberAddress","IsLocal","IsLite");
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
                    member.localMember()
                        ? "Y"
                        : "N",
                    member.isLiteMember()
                        ? "Y"
                        : "N");
            }
        }
    }
}