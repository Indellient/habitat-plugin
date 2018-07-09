package org.jenkinsci.plugins.habitat;

import jenkins.security.MasterToSlaveCallable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class LastBuildSlaveRetriever extends MasterToSlaveCallable<LastBuild,RuntimeException> {

    private String path;

    public LastBuildSlaveRetriever(String path) {
        this.path = path;
    }

    @Override
    public LastBuild call() {
        LastBuild last = null;
        try {
            last = this.parseFile(this.path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return last;
    }


    private LastBuild parseFile(String file) throws Exception {
        LastBuild lastBuild = new LastBuild();
        if (!new File(file).exists()) {
            throw new NullPointerException("Could not find file " + file);
        }

        File f = new File(file);
        FileReader fileReader = new FileReader(f);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuffer stringBuffer = new StringBuffer();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("pkg_artifact")) {
                lastBuild.setArtifact(Arrays.asList(line.split("=")).get(1));
            }

            if (line.contains("pkg_origin")) {
                lastBuild.setOrigin(Arrays.asList(line.split("=")).get(1));
            }

            if (line.contains("pkg_name")) {
                lastBuild.setName(Arrays.asList(line.split("=")).get(1));
            }

            if (line.contains("pkg_version")) {
                lastBuild.setVersion(Arrays.asList(line.split("=")).get(1));
            }

            if (line.contains("pkg_release")) {
                lastBuild.setRelease(Arrays.asList(line.split("=")).get(1));
            }

            if (line.contains("pkg_ident")) {
                lastBuild.setIdent(Arrays.asList(line.split("=")).get(1));
            }

            if (line.contains("pkg_artifact")) {
                lastBuild.setArtifact(Arrays.asList(line.split("=")).get(1));
            }

            if (line.contains("pkg_sha256sum")) {
                lastBuild.setSha256sum(Arrays.asList(line.split("=")).get(1));
            }

            if (line.contains("pkg_blake2bsum")) {
                lastBuild.setBlake2bsum(Arrays.asList(line.split("=")).get(1));
            }

            stringBuffer.append(line);
            stringBuffer.append("\n");
        }
        fileReader.close();
        System.out.println("Contents of file:");
        System.out.println(stringBuffer.toString());

        return lastBuild;

    }
}
