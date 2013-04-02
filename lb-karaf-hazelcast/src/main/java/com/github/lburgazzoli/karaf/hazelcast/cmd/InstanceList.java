package com.github.lburgazzoli.karaf.hazelcast.cmd;

import com.github.lburgazzoli.karaf.hazelcast.IHazelcastManager;
import com.github.lburgazzoli.osgi.karaf.cmd.AbstractTabularCommand;
import com.github.lburgazzoli.osgi.karaf.cmd.ShellTable;
import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Instance;
import org.apache.felix.gogo.commands.Command;

import java.util.Collection;


/**
 *
 */
@Command(scope = "hz", name = "instance-list", description = "List Hazelcast Instances")
public class InstanceList extends AbstractTabularCommand<IHazelcastManager> {
    /**
     *
     */
    public InstanceList() {
        super("Name","InstanceType","InstanceID");
    }

    @Override
    protected void doExecute(IHazelcastManager service, ShellTable table) throws Exception {
        Collection<Instance> instances = service.getInstance().getInstances();

        if(instances != null) {
            for(Instance instance : instances) {
                switch(instance.getInstanceType()) {
                    case MAP:
                        table.addRow(
                            ((IMap<?, ?>) instance).getName(),
                            instance.getInstanceType().toString(),
                            instance.getId());
                        break;
                    case LIST:
                        table.addRow(
                            ((IList<?>) instance).getName(),
                            instance.getInstanceType().toString(),
                            instance.getId());
                        break;
                    case QUEUE:
                        table.addRow(
                            ((IQueue<?>) instance).getName(),
                            instance.getInstanceType().toString(),
                            instance.getId());
                        break;
                    case TOPIC:
                        table.addRow(
                            ((ITopic<?>) instance).getName(),
                            instance.getInstanceType().toString(),
                            instance.getId());
                        break;
                }
            }
        }
    }
}