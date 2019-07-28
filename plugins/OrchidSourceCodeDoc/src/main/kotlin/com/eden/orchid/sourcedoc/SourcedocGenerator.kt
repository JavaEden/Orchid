package com.eden.orchid.sourcedoc

import com.caseyjbrooks.clog.Clog
import com.copperleaf.kodiak.common.DocElement
import com.copperleaf.kodiak.common.DocInvoker
import com.copperleaf.kodiak.common.ModuleDoc
import com.eden.common.util.IOStreamUtils
import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.generators.OrchidGenerator
import com.eden.orchid.api.options.OptionsExtractor
import com.eden.orchid.api.options.annotations.BooleanDefault
import com.eden.orchid.api.options.annotations.Description
import com.eden.orchid.api.options.annotations.Option
import com.eden.orchid.sourcedoc.model.SourceDocModel
import com.eden.orchid.sourcedoc.page.SourceDocPage
import com.eden.orchid.utilities.OrchidUtils
import java.io.File
import java.nio.file.Path
import javax.inject.Named

abstract class SourcedocGenerator<U : ModuleDoc>(
    key: String,
    @Named("src") val resourcesDir: String,
    private val invoker: DocInvoker<U>,
    private val extractor: OptionsExtractor
) : OrchidGenerator<SourceDocModel>(key, PRIORITY_EARLY) {

    @Option
    @Description("The source directories to document.")
    lateinit var sourceDirs: List<String>

    @Option
    @BooleanDefault(true)
    @Description("Whether to reuse outputs from the cache, or rerun each build")
    var fromCache: Boolean = true

    private val cacheDir: Path by lazy { OrchidUtils.getCacheDir("sourcedoc-$key") }
    private val outputDir: Path by lazy { OrchidUtils.getTempDir("sourcedoc-$key", true) }

    override fun startIndexing(context: OrchidContext): SourceDocModel {
        extractor.extractOptions(invoker, allData)

        Clog.v("Running {} generator", key)
        val modelPageMap = loadFromCacheOrRun()
            ?.let {
                Clog.v("{} generator returned {} top-level nodes", key, it.nodes.size)
                val allNodePages = it.nodes.map { node ->
                    val nodeName: String = node.prop.name
                    val nodeElements: List<DocElement> = node.getter()

                    Clog.v("{} generator node {} returned {} elements", key, nodeName, nodeElements.size)
                    val nodePages = nodeElements.map { element ->
                        SourceDocPage(this@SourcedocGenerator, context, element, nodeName, element.name)
                    }

                    node to nodePages
                }
                allNodePages.toMap()
            }
            ?: emptyMap()

        return SourceDocModel(modelPageMap)
    }

    override fun startGeneration(context: OrchidContext, model: SourceDocModel) {
        model.allPages.forEach { context.renderTemplate(it) }
    }

// helpers
//----------------------------------------------------------------------------------------------------------------------

    private fun loadFromCacheOrRun(): U? {
        if (fromCache) {
            val moduleDoc = invoker.loadCachedModuleDoc(outputDir)
            if (moduleDoc != null) {
                return moduleDoc
            }
        }

        return invoker.getModuleDoc(
            sourceDirs.map { File(resourcesDir).toPath().resolve(it) },
            outputDir,
            emptyList()
//        ) { inputStream -> IOStreamUtils.InputStreamIgnorer(inputStream) }
        ) { inputStream -> IOStreamUtils.InputStreamPrinter(inputStream, null) }
    }

}
