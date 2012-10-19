package org.jboss.as.jdr.commands;

import org.jboss.as.jdr.util.FSTree;

/**
 * Created with IntelliJ IDEA.
 * User: jhjaggars
 * Date: 10/18/12
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeCommand extends JdrCommand {

    @Override
    public void execute() throws Exception {
        FSTree tree = new FSTree(this.env.getJbossHome());
        this.env.getZip().add(tree.toString(), "tree.txt");
    }
}
