package org.jenkinsci.plugins.habitat;

import jenkins.security.MasterToSlaveCallable;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class FileFinder extends MasterToSlaveCallable<String,RuntimeException> {
    private String start;
    private String file;

    public FileFinder(String start, String file) {

    }
    @Override
    public String call() throws RuntimeException {
        Collection files = FileUtils.listFiles(new File(this.start), null, true);

        String result = "";
        for (Iterator iterator = files.iterator(); iterator.hasNext(); ) {
            File f = (File) iterator.next();
            if (f.getName().equals(this.file)) {
                result = f.getAbsolutePath();
            }
        }
        return result;
    }
}
