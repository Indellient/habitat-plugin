package org.jenkinsci.plugins.habitat;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Proc;
import hudson.model.*;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HabitatExecutor extends Builder implements SimpleBuildStep {

    private String task;
    private String channel;
    private String directory;

    @DataBoundConstructor
    public HabitatExecutor(String task, String directory) {
        this.task = task;
        this.directory = directory;
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

        switch (this.task.trim()) {
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

    private String promoteCommand(boolean isWindows, PrintStream log) {
        String pkgIdent = this.getLastBuild(this.lastBuildPath(), log).getIdent();
        String channel = this.getChannel();

        if (isWindows) {
            return String.format("hab pkg promote %s %s", pkgIdent, channel);
        } else {
            return String.format("hab pkg promote %s %s", pkgIdent, channel);
        }
    }

    private String uploadCommand(boolean isWindows, PrintStream log) throws Exception {
        String lastPackage = this.getLatestPackage(log);
        log.println("Last Package: " + lastPackage);
        if (!(new File(lastPackage).exists())) {
            throw new Exception("Could not find hart " + lastPackage);
        }
        if (isWindows) {
            return String.format("hab pkg upload %s", lastPackage);
        } else {
            return String.format("hab pkg upload %s", lastPackage);
        }
    }

    private String lastBuildPath() {
        return this.findFile(new File(this.directory).getAbsolutePath(), "last_build.env");
    }

    private String getLatestPackage(PrintStream log) {
        String artifact = this.getLastBuild(this.lastBuildPath(), log).getArtifact();
        return this.findFile(new File(this.directory).getAbsolutePath(), artifact);
    }

    private LastBuild getLastBuild(String path, PrintStream log) {
        LastBuild last = new LastBuild(path, log);
        log.println(last.toString());
        return last;
    }

    private String findFile(String startPath, String file) {

        Collection files = FileUtils.listFiles(new File(startPath), null, true);

        String result = "";
        for (Iterator iterator = files.iterator(); iterator.hasNext(); ) {
            File f = (File) iterator.next();
            if (f.getName().equals(file)) {
                result = f.getAbsolutePath();
            }

        }
        return result;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> build, @Nonnull FilePath workspace, @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws InterruptedException, IOException {
        PrintStream log = listener.getLogger();

        Map<String, String> buildEnv = build.getEnvVars();
        Map<String, String> env = new HashMap<>();
        env.put("HAB_NOCOLORING", "true");
        env.putAll(buildEnv);
        Proc proc = null;
        try {
            proc = launcher.launch(this.command(log), env, listener.getLogger(), workspace);
        } catch (Exception e) {
            log.println(e.getMessage());
        }
        int exitCode = proc.join();
        if (exitCode != 0) {
            throw new IOException("Failed to execute " + this.task);
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
