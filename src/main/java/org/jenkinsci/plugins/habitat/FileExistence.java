package org.jenkinsci.plugins.habitat;

import jenkins.security.MasterToSlaveCallable;

import java.io.File;

public class FileExistence extends MasterToSlaveCallable<Boolean, RuntimeException> {
    private String file;

    public FileExistence(String file) {
        this.file = file;
    }

    @Override
    public Boolean call() throws RuntimeException {
        return new File(this.file).exists();
    }
}
