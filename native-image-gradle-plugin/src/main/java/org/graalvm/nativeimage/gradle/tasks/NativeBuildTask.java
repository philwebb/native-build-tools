/*
 * Copyright (c) 2021, 2021 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.graalvm.nativeimage.gradle.tasks;

import org.graalvm.nativeimage.gradle.GradleUtils;
import org.graalvm.nativeimage.gradle.NativeImageService;
import org.graalvm.nativeimage.gradle.Utils;
import org.graalvm.nativeimage.gradle.dsl.NativeImageOptions;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.AbstractExecTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceSet;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

import java.io.File;
import java.util.List;

@SuppressWarnings("unused")
public abstract class NativeBuildTask extends AbstractExecTask<NativeBuildTask> {
    public static final String TASK_NAME = "buildNative";

    protected NativeImageOptions options;

    public NativeBuildTask() {
        super(NativeBuildTask.class);
        dependsOn("classes");
        options = getProject().getExtensions().findByType(NativeImageOptions.class);
        setExecutable(Utils.getNativeImage());
        setWorkingDir(getProject().getBuildDir());
        setDescription("Builds native-image from this project.");
        setGroup(LifecycleBasePlugin.BUILD_GROUP);
    }

    @InputFiles
    public FileCollection getInputFiles() {
        return GradleUtils.getClassPath(getProject(), SourceSet.MAIN_SOURCE_SET_NAME);
    }

    @Input
    public List<String> getArgs() {
        options.configure(getProject());
        return options.getArgs().get();
    }

    @OutputFile
    public File getOutputFile() {
        return GradleUtils.getTargetDir(getProject()).resolve(options.getImageName().get()).toFile();
    }

    // This property provides access to the service instance
    @Internal
    public abstract Property<NativeImageService> getServer();

    @Override
    @SuppressWarnings("ConstantConditions")
    public void exec() {
        this.args(getArgs());
        getServer().get();
        super.exec();
        System.out.println("Native Image written to: " + getOutputFile());
    }
}
