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
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Instance;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

import java.util.Collection;


/**
 *
 */
@Command(scope = "hz", name = "instance-info", description = "Instance Info")
public class InstanceInfo extends AbstractTabularCommand<IHazelcastManager> {
    @Argument(
        index       = 0,
        name        = "name",
        description = "The Instance name",
        required    = true,
        multiValued = false)
    String instanceName = null;

    /**
     *
     */
    public InstanceInfo() {
        super("Key","Value");
    }

    @Override
    protected void doExecute(IHazelcastManager service, ShellTable table) throws Exception {
        Collection<Instance> instances = service.getInstance().getInstances();
        for(Instance instance : instances) {
            switch(instance.getInstanceType()) {
                case MAP: {
                        IMap<?,?> data = (IMap<?, ?>)instance;
                        if(StringUtils.equals(data.getName(),instanceName)) {
                            table.addRow("instance.name",data.getName());
                            table.addRow("instance.id"  ,data.getId());
                            table.addRow("instance.type",data.getInstanceType().name());
                            table.addRow("size"         ,data.size());
                        }
                    }
                    break;
                case LIST: {
                        IList<?> data = (IList<?>)instance;
                        if(StringUtils.equals(data.getName(),instanceName)) {
                            table.addRow("instance.name",data.getName());
                            table.addRow("instance.id"  ,data.getId());
                            table.addRow("instance.type",data.getInstanceType().name());
                            table.addRow("size"         ,data.size());
                        }
                    }
                    break;
                case QUEUE: {
                        IQueue<?> data = (IQueue<?>)instance;
                        if(StringUtils.equals(data.getName(),instanceName)) {
                            table.addRow("instance.name",data.getName());
                            table.addRow("instance.id"  ,data.getId());
                            table.addRow("instance.type",data.getInstanceType().name());
                            table.addRow("size"         ,data.size());
                        }
                    }
                    break;
                case TOPIC: {
                        ITopic<?> data = (ITopic<?>)instance;
                        if(StringUtils.equals(data.getName(),instanceName)) {
                            table.addRow("instance.name",data.getName());
                            table.addRow("instance.id"  ,data.getId());
                            table.addRow("instance.type",data.getInstanceType().name());
                        }
                    }
                    break;
            }
        }
    }
}