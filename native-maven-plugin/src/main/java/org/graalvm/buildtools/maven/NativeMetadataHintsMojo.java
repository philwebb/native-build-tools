package org.graalvm.buildtools.maven;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.graalvm.reachability.DirectoryConfiguration;


@Mojo(name = "add-metadata-hints", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, requiresDependencyResolution = ResolutionScope.RUNTIME, requiresDependencyCollection = ResolutionScope.RUNTIME)
public class NativeMetadataHintsMojo extends AbstractNativeMojo {

    // attach property

    private static final List<String> SCOPES = Collections.unmodifiableList(
            Arrays.asList(Artifact.SCOPE_COMPILE, Artifact.SCOPE_RUNTIME, Artifact.SCOPE_COMPILE_PLUS_RUNTIME));

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        configureMetadataRepository();
        Set<Artifact> artifacts = project.getArtifacts();
        for (Artifact artifact : artifacts) {
            System.out.println(artifact);
        }
        project.getArtifacts().stream().filter(this::isInScope)
                .forEach(dependency -> maybeAddDependencyMetadata(dependency, null));
        if (isMetadataRepositoryEnabled() && !metadataRepositoryConfigurations.isEmpty()) {
            for (DirectoryConfiguration configuration : metadataRepositoryConfigurations) {
                System.out.println(configuration.getDirectory().toAbsolutePath());
            }
        }
    }

    private boolean isInScope(Artifact artifact) {
        return SCOPES.contains(artifact.getScope());
    }

}
