package org.jenkinsci.plugins.habitat;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class HabitatExecutor extends Builder implements SimpleBuildStep {

    private String task;
    private String channel;
    private String directory;
    private String lastBuildFile;
    private String artifact;
    private String origin;
    private String bldrUrl;
    private String authToken;

    private VirtualChannel slave;


    @DataBoundConstructor
    public HabitatExecutor(
            String task, String directory, String artifact, String channel,
            String origin, String bldrUrl, String authToken, String lastBuildFile
    ) {
        this.setTask(task);
        this.setArtifact(artifact);
        this.setDirectory(directory);
        this.setChannel(channel);
        this.setOrigin(origin);
        this.setBldrUrl(bldrUrl);
        this.setAuthToken(authToken);
        this.setLastBuildFile(lastBuildFile);
    }

    public String getLastBuildFile() {
        return lastBuildFile;
    }

    @DataBoundSetter
    public void setLastBuildFile(String lastBuildFile) {
        this.lastBuildFile = lastBuildFile;
    }

    public String getOrigin() {
        return origin;
    }

    @DataBoundSetter
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getBldrUrl() {
        return bldrUrl;
    }

    @DataBoundSetter
    public void setBldrUrl(String bldrUrl) {
        this.bldrUrl = bldrUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    @DataBoundSetter
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getArtifact() {
        return artifact;
    }

    @DataBoundSetter
    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public String getChannel() {
        return channel;
    }

    @DataBoundSetter
    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTask() {
        return task;
    }

    @DataBoundSetter
    public void setTask(String task) {
        this.task = task;
    }

    public String getDirectory() {
        return directory;
    }

    @DataBoundSetter
    public void setDirectory(String directory) {
        this.directory = directory;
    }


    private String command(PrintStream log) throws Exception {
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");

        switch (this.getTask().trim()) {
            case "build":
                return this.buildCommand(isWindows);
            case "promote":
                return this.promoteCommand(isWindows, log);
            case "upload":
                return this.uploadCommand(isWindows, log);
            default:
                throw new Exception("Task not yet implemented");
        }
    }

    private String buildCommand(boolean isWindows) {
        if (isWindows) {
            return String.format("hab studio build %s", this.getDirectory());
        } else {
            return String.format("hab studio build %s", this.getDirectory());
        }
    }

    private String promoteCommand(boolean isWindows, PrintStream log) throws Exception {
        String pkgIdent = this.getArtifact();
        if (pkgIdent == null) {
            LastBuild lastBuild = this.slave.call(new LastBuildSlaveRetriever(this.lastBuildPath(log)));
            pkgIdent = lastBuild.getIdent();
        }

        String channel = this.getChannel();
        if (channel == null) {
            throw new Exception("Channel cannot be null");
        }

        if (isWindows) {
            return String.format("hab pkg promote %s %s", pkgIdent, channel);
        } else {
            return String.format("hab pkg promote %s %s", pkgIdent, channel);
        }
    }

    private String uploadCommand(boolean isWindows, PrintStream log) throws Exception {
        String lastPackage = this.getLatestPackage(log);
        log.println("Last Package: " + lastPackage);
        if (!this.slave.call(new FileExistence(lastPackage))) {
            throw new Exception("Could not find hart " + lastPackage);
        }
        if (isWindows) {
            return String.format("hab pkg upload %s", lastPackage);
        } else {
            return String.format("hab pkg upload %s", lastPackage);
        }
    }

    private String getLatestPackage(PrintStream log) throws Exception {
        LastBuild lastBuild = this.slave.call(new LastBuildSlaveRetriever(this.lastBuildPath(log)));
        String artifact = lastBuild.getArtifact();
        log.println("Artifact " + artifact + " found in: " + new File(this.getDirectory()).getAbsolutePath());
        return new File(this.getDirectory()).getAbsolutePath() + File.separator + artifact;
//        return this.slave.call(new FileFinder(new File(this.getDirectory()).getAbsolutePath(), artifact));
    }

    private String lastBuildPath(PrintStream log) throws Exception {
        if (this.getLastBuildFile() != null) {
            if (this.getDirectory() == null) {
                String dir = this.getLastBuildFile();
                dir = dir.replace("last_build.env", "");
                log.println("Setting Directory: " + dir);
                this.setDirectory(dir);
            }
            return this.getLastBuildFile();
        } else {
            return this.slave.call(new FileFinder(new File(this.getDirectory()).getAbsolutePath(), "last_build.env"));
        }
    }


    private Map<String, String> getEnv(PrintStream log) throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("HAB_NOCOLORING", "true");

        if (this.getOrigin() == null) {
            if (this.getTask().equalsIgnoreCase("build")) {
                throw new Exception("cannot build without specifying an origin");
            }
        } else {
            env.put("HAB_ORIGIN", this.getOrigin());
        }

        if (this.getAuthToken() == null) {
            if (this.needsAuthToken()) {
                throw new Exception("this task needs an auth token to be set");
            }
        } else {
            env.put("HAB_AUTH_TOKEN", this.getAuthToken());
        }

        if (this.getBldrUrl() != null) {
            env.put("HAB_BLDR_URL", this.getBldrUrl());
        }

        return env;
    }

    private boolean needsAuthToken() {
        return this.getTask().equalsIgnoreCase("upload") || this.getTask().equalsIgnoreCase("promote");
    }

    @Override
    public void perform(@Nonnull Run<?, ?> build, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        PrintStream log = listener.getLogger();
        Map<String, String> otherEnvs = build.getEnvironment(TaskListener.NULL);

        Proc proc = null;
        try {
            Map<String, String> env = this.getEnv(log);
            env.putAll(otherEnvs);

            log.println("Build Environment Variables");
            env.forEach((k, v) -> log.println(k + ": " + v));
            this.slave = launcher.getChannel();
            Launcher.ProcStarter starter = launcher.launch().pwd(workspace).envs(env).cmdAsSingleString(this.command(log));
            starter.stdout(log);
            proc = launcher.launch(starter);
        } catch (Exception e) {
            log.println(e.getMessage());
        }
        int exitCode = proc.join();
        if (exitCode != 0) {
            throw new IOException("Failed to execute " + this.getTask());
        }

    }

    @Symbol("habitat")
    @Extension
    public static class Descriptor extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return FreeStyleProject.class.isAssignableFrom(jobType);
        }

        @Override
        public String getDisplayName() {
            return "Habitat Executor";
        }
    }
}
