package org.jboss.as.jdr.commands;

public abstract class JdrCommand {
    JdrEnvironment env;

    public void setEnvironment(JdrEnvironment env) {
        this.env = env;
    }

    public abstract void execute() throws Exception;
}
