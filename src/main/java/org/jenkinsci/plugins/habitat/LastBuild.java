package org.jenkinsci.plugins.habitat;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LastBuild {

    private String origin;
    private String name;
    private String version;
    private String release;
    private String ident;
    private String artifact;
    private String sha256sum;
    private String blake2bsum;

    private PrintStream log;
    public LastBuild(String file, PrintStream log) {
        this.log = log;
        this.parseFile(file);
    }

    private void parseFile(String file) {
        this.setArtifact(this.getLastBuildField(file, "pkg_artifact"));
        this.setOrigin(this.getLastBuildField(file, "pkg_origin"));
        this.setName(this.getLastBuildField(file, "pkg_name"));
        this.setVersion(this.getLastBuildField(file, "pkg_version"));
        this.setRelease(this.getLastBuildField(file, "pkg_release"));
        this.setIdent(this.getLastBuildField(file, "pkg_ident"));
        this.setArtifact(this.getLastBuildField(file, "pkg_artifact"));
        this.setSha256sum(this.getLastBuildField(file, "pkg_sha256sum"));
        this.setBlake2bsum(this.getLastBuildField(file, "pkg_blake2bsum"));
    }

    private String getLastBuildField(String file, String tag) {
        List<String> lines = null;
        try {
            lines = Files.lines(Paths.get(file)).collect(Collectors.toList());

        } catch (IOException e) {
            log.println(e.getMessage());
        }
        for (String line : lines) {
            if (line.contains(tag)) {
                return Arrays.asList(line.split("=")).get(1);
            }
        }
        return null;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getSha256sum() {
        return sha256sum;
    }

    public void setSha256sum(String sha256sum) {
        this.sha256sum = sha256sum;
    }

    public String getBlake2bsum() {
        return blake2bsum;
    }

    public void setBlake2bsum(String blake2bsum) {
        this.blake2bsum = blake2bsum;
    }

    @Override
    public String toString() {
        return "LastBuild{" +
                "origin='" + origin + '\'' +
                ", name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", release='" + release + '\'' +
                ", ident='" + ident + '\'' +
                ", artifact='" + artifact + '\'' +
                ", sha256sum='" + sha256sum + '\'' +
                ", blake2bsum='" + blake2bsum + '\'' +
                ", log=" + log +
                '}';
    }
}
