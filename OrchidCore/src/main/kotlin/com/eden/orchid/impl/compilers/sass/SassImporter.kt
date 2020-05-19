package com.eden.orchid.impl.compilers.sass

import com.caseyjbrooks.clog.Clog
import com.eden.common.util.EdenPair
import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.resources.resource.OrchidResource
import com.eden.orchid.utilities.OrchidUtils
import io.bit3.jsass.importer.Import
import io.bit3.jsass.importer.Importer
import org.apache.commons.io.FilenameUtils
import javax.inject.Inject

class SassImporter @Inject
constructor(private val context: OrchidContext) : Importer {

    private val ignoredPreviousValues = arrayOf("stdin")

    override fun apply(url: String, previous: Import): Collection<Import>? {
        val (isAbsolute, cleanedCurrentImportName) = cleanInputName(url)
        val preferredInputExtension = getPreferredInputExtension(url)
        val currentDirectory = getCurrentDirectory(url, previous, isAbsolute)

        val availableFiles = when (preferredInputExtension) {
            SassCompiler.CompilerSyntax.SCSS -> cleanResourcePaths(
                getScssPatterns(
                    currentDirectory,
                    cleanedCurrentImportName
                )
            )
            SassCompiler.CompilerSyntax.SASS -> cleanResourcePaths(
                getSassPatterns(
                    currentDirectory,
                    cleanedCurrentImportName
                )
            )
            SassCompiler.CompilerSyntax.UNSPECIFIED -> cleanResourcePaths(
                getAnyPatterns(
                    currentDirectory,
                    cleanedCurrentImportName
                )
            )
        }

        for (availableFile in availableFiles) {
            val importedResource = context.getDefaultResourceSource(null, context.theme).getResourceEntry(context, availableFile)

            if (importedResource != null) {
                var content = importedResource.content
                if (importedResource.shouldPrecompile()) {
                    content = context.compile(
                        importedResource,
                        importedResource.precompilerExtension,
                        content,
                        importedResource.embeddedData
                    )
                }

                try {
                    val baseUri = "" + OrchidUtils.normalizePath(
                        FilenameUtils.removeExtension("$currentDirectory/$cleanedCurrentImportName")
                    )
                    val relativeUri = "" + OrchidUtils.normalizePath(
                        FilenameUtils.removeExtension("$currentDirectory/$cleanedCurrentImportName").split("/").last() + preferredInputExtension.ext
                    )

                    if (importedResource.reference.extension == "sass") {
                        content = convertSassToScss(importedResource, content, baseUri)
                    }

                    val newImport = Import(relativeUri, baseUri, content)

                    return listOf(newImport)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        return null
    }

    private fun splitPath(originalName: String): EdenPair<String, String> {
        val name = cleanPath(originalName)

        if (name.contains("/")) {
            val pieces = name.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var path = ""

            for (i in 0 until pieces.size - 1) {
                path += pieces[i].replace("_".toRegex(), "") + "/"
            }
            val fileName = pieces[pieces.size - 1].replace("_".toRegex(), "")

            return EdenPair(
                OrchidUtils.normalizePath(path),
                OrchidUtils.normalizePath(FilenameUtils.removeExtension(fileName))
            )
        } else {
            return EdenPair("", FilenameUtils.removeExtension(originalName))
        }
    }

    private fun cleanPath(originalName: String): String {
        var name = originalName
        name = name.replace("\\\\\\\\".toRegex(), "/")
        name = name.replace("\\\\".toRegex(), "/")

        return name
    }

    private fun getScssPatterns(baseDir: String, importPath: String): Array<String> {
        return arrayOf(
            "$baseDir/$importPath.scss",
            "$baseDir/_$importPath.scss"
        )
    }

    private fun getSassPatterns(baseDir: String, importPath: String): Array<String> {
        return arrayOf(
            "$baseDir/$importPath.sass",
            "$baseDir/_$importPath.sass"
        )
    }

    private fun getAnyPatterns(baseDir: String, importPath: String): Array<String> {
        return arrayOf(
            *getScssPatterns(baseDir, importPath),
            *getSassPatterns(baseDir, importPath)
        )
    }


// helpers
//----------------------------------------------------------------------------------------------------------------------

    private fun cleanInputName(import: String): Pair<Boolean, String> {
        return Pair(
            import.trim().startsWith("/"),
            splitPath(import).second
        )
    }

    private fun getPreferredInputExtension(input: String): SassCompiler.CompilerSyntax {
        return when (FilenameUtils.getExtension(input)) {
            SassCompiler.CompilerSyntax.SCSS.ext -> SassCompiler.CompilerSyntax.SCSS
            SassCompiler.CompilerSyntax.SASS.ext -> SassCompiler.CompilerSyntax.SASS
            else                                 -> SassCompiler.CompilerSyntax.UNSPECIFIED
        }
    }

    private fun getCurrentDirectory(import: String, previous: Import, isAbsolute: Boolean): String {
        val baseDirectory = if (ignoredPreviousValues.any { it == previous.absoluteUri.normalize().toString() }) {
            "assets/css"
        } else {
            previous.absoluteUri.normalize().toString().split("/").dropLast(1).joinToString("/")
        }

        val importDirectory = splitPath(import).first

        return if(isAbsolute) {
            importDirectory
        }
        else {
            if(importDirectory.isBlank()) {
                OrchidUtils.normalizePath(baseDirectory)
            }
            else {
                OrchidUtils.normalizePath("$baseDirectory/$importDirectory")
            }
        }
    }

    private fun cleanResourcePaths(inputs: Array<String>): List<String> {
        return inputs.map { OrchidUtils.normalizePath(it) }
    }

    private fun convertSassToScss(resource: OrchidResource?, input: String, baseUri: String): String {
        // Importing Sass syntax is not natively supported, we must compile it ourselves manually. And since
        // we are going outside the normal importing flow, we have to add a comment signalling the import's
        // context. Unfortunately, this means that each Sass-style import is compiled in isolation, so variables,
        // macros, etc. are not available in imported files, currently.
        //
        // In the future, there will hopefully be the `sass2scss()` native function will be exported by jsass, so the
        // content passed here can just be converted to SCSS, instead of compiled fully to CSS and included.
        return context.compile(resource, "sass", Clog.format("// CONTEXT={}\n{}", baseUri, input), null)
    }

}
