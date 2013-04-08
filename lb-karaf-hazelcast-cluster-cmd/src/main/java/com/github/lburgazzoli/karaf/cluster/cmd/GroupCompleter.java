package com.github.lburgazzoli.karaf.cluster.cmd;

import com.github.lburgazzoli.cluster.IClusterAgent;
import com.github.lburgazzoli.cluster.IClusteredServiceGroup;
import com.github.lburgazzoli.osgi.karaf.cmd.AbstractServiceCompleter;
import org.apache.karaf.shell.console.completer.StringsCompleter;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public class GroupCompleter extends AbstractServiceCompleter<IClusterAgent> {
    @Override
    public int doComplete(IClusterAgent service,String buffer, int cursor, List<String> candidates) {
        StringsCompleter delegate = new StringsCompleter();
        Collection<IClusteredServiceGroup> values = service.getServiceGroups();
        for(IClusteredServiceGroup value : values) {
            delegate.getStrings().add(value.getGroupId());
        }

        return delegate.complete(buffer, cursor, candidates);
    }
}
