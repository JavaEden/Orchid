plugins {
    `copper-leaf-base`
    `copper-leaf-version`
    jacoco
    id("com.github.ben-manes.versions") version "0.39.0"
    `copper-leaf-sonatype`
}

// Add check to make sure every release version has a Changelog file
//----------------------------------------------------------------------------------------------------------------------

fun ProjectVersion.toFile(): File {
    return project.file("$rootDir/docs/src/orchid/resources/changelog/$shortVersion/$releaseVersion.md")
}

val checkForChangelogFile by project.tasks.registering {
    doLast {
        if (project.hasProperty("release")) {
            val version = Config.projectVersion(project)
            val changelogFile = version.toFile()

            if (!changelogFile.exists()) {
                throw java.io.FileNotFoundException("There is no changelog entry for this version, expected '${changelogFile.absolutePath}' to exist.")
            }
        }
    }
}

val createChangelogFile by project.tasks.registering {
    doLast {
        val version = Config.projectVersion(project)
        val changelogFile = version.toFile()

        project.mkdir(changelogFile.parentFile)
        changelogFile.writeText(
            """
            |---
            |---
            |
            |- ${version.commits.joinToString(separator = "\n- ")}
            |
            |## Breaking Changes
            |
            |- 
            |
            """.trimMargin()
        )
    }
}

val check by tasks
check.dependsOn(checkForChangelogFile)

val archiveDocumentation by tasks.registering(Zip::class) {
    from("${project(":docs").buildDir}/docs/orchid")
//    from(project.version.toFile())

    include("**/*")

    archiveFileName.set("docs-${project.version}.zip")
    destinationDirectory.set(file("$buildDir/orchid/archives"))
}

val publish by tasks.registering {
    dependsOn(archiveDocumentation)
}
