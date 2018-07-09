package org.jenkinsci.plugins.habitat;

import java.io.*;

public class LastBuild implements Serializable {

    private String origin;
    private String name;
    private String version;
    private String release;
    private String ident;
    private String artifact;
    private String sha256sum;
    private String blake2bsum;

    public LastBuild() {
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
                '}';
    }
}
