package org.jboss.as.jdr.plugins;

import org.jboss.as.jdr.commands.JdrCommand;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface JdrPlugin {
    List<JdrCommand> getCommands() throws Exception;
}
