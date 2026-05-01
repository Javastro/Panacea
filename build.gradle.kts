

// ---------------------------------------------------------------------------
// Aggregated Javadoc across all Java submodules
// ---------------------------------------------------------------------------

val aggregateJavadoc by tasks.registering(Javadoc::class) {
    description = "Generates aggregated Javadoc API documentation for all modules."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    setDestinationDir(layout.buildDirectory.dir("docs/javadoc").get().asFile)
    title = "${rootProject.name} ${rootProject.version} API"
    (options as StandardJavadocDocletOptions).tags("TODO:a:\"To Do:\".")
}

subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("java") || plugins.hasPlugin("java-library")) {
            val mainSourceSet = extensions
                .getByType<JavaPluginExtension>()
                .sourceSets
                .getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            rootProject.tasks.named<Javadoc>("aggregateJavadoc") {
                source(mainSourceSet.allJava)
                classpath += mainSourceSet.compileClasspath
                dependsOn(tasks.named("compileJava"))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Sphinx documentation configuration
// ---------------------------------------------------------------------------

tasks.register<Exec>("sphinx") {
    description = "Runs sphinx command. N.B sphinx needs to be already installed on the path"
    val sourceDir = layout.projectDirectory.dir("doc")
    val outputDir = layout.buildDirectory.dir("site").get()

    executable = "sphinx-build"

    // Arguments for sphinx-build: [options] <sourcedir> <outputdir>
    args("-v","-b", "html", sourceDir.asFile.absolutePath, outputDir.asFile.absolutePath)

    inputs.dir(sourceDir)
    outputs.dir(outputDir)

    dependsOn(aggregateJavadoc)
    doLast {
        copy {
            from(layout.buildDirectory.dir("docs/javadoc"))
            into(layout.buildDirectory.dir("site/javadoc"))
        }
    }
}

// Convenience task to build all documentation
tasks.register("docs") {
    description = "Builds the complete documentation (Sphinx + aggregated Javadoc)."
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    dependsOn("sphinx")
}
