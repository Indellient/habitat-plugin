package org.jenkinsci.plugins.habitat;

import jenkins.security.MasterToSlaveCallable;

public class CommandExecutor extends MasterToSlaveCallable<Boolean,RuntimeException> {

    private String command;

    public CommandExecutor(String command) {
        this.command = command;
    }
    @Override
    public Boolean call() throws RuntimeException {

        return true;
    }
}
