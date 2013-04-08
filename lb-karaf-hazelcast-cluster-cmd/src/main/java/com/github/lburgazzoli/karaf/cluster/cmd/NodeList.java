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
package com.github.lburgazzoli.karaf.cluster.cmd;

import com.github.lburgazzoli.cluster.IClusterAgent;
import com.github.lburgazzoli.cluster.IClusteredNode;
import com.github.lburgazzoli.osgi.karaf.cmd.AbstractTabularCommand;
import com.github.lburgazzoli.osgi.karaf.cmd.CommandConstants;
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
        super("IsLocal","NodeID","NodeAddress");
    }

    @Override
    protected void doExecute(IClusterAgent service, ShellTable table) throws Exception {
        Collection<IClusteredNode> values = service.getNodes();
        for(IClusteredNode value : values) {
            table.addRow(
                StringUtils.equals(value.getNodeId(),service.getId())
                    ? CommandConstants.FLAG_YES
                    : CommandConstants.FLAG_NO,
                value.getNodeId(),
                value.getNodeAddress());
        }
    }
}
