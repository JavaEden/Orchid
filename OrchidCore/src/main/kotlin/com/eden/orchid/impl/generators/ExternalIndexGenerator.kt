package com.eden.orchid.impl.generators

import com.eden.common.util.EdenUtils
import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.generators.OrchidCollection
import com.eden.orchid.api.generators.OrchidGenerator
import com.eden.orchid.api.generators.PageCollection
import com.eden.orchid.api.indexing.OrchidIndex
import com.eden.orchid.api.options.OptionsHolder
import com.eden.orchid.api.options.annotations.Archetype
import com.eden.orchid.api.options.annotations.Description
import com.eden.orchid.api.options.annotations.ImpliedKey
import com.eden.orchid.api.options.annotations.Option
import com.eden.orchid.api.options.archetypes.ConfigArchetype
import com.eden.orchid.api.theme.pages.OrchidPage
import org.apache.commons.io.FilenameUtils
import org.json.JSONObject


@Description(value = "Index external Orchid sites to create strong links between sites.", name = "External Indices")
@Archetype(value = ConfigArchetype::class, key = "services.generators")
class ExternalIndexGenerator :
    OrchidGenerator<ExternalIndexGenerator.ExternalIndexModel>(GENERATOR_KEY, Stage.META) {

    @Option
    @Description(
        "The indices generated by an Orchid site can be included in the build of another Orchid site, to " +
                "make strong links between the two sites. The index at `meta/indices.json` will crawl all the sub-indices " +
                "of that site, or just a single one of that site's sub-indices can be included."
    )
    @ImpliedKey("url")
    lateinit var externalIndices: List<ExternalIndex>

    override fun startIndexing(context: OrchidContext): ExternalIndexModel {
        val allPages = mutableMapOf<String, OrchidIndex>()

        if (!EdenUtils.isEmpty(externalIndices)) {
            for (externalIndex in externalIndices) {
                val indexJson = context.loadAdditionalFile(externalIndex.url)
                if (indexJson != null) {
                    val index = OrchidIndex.fromJSON(context, JSONObject(indexJson))
                    allPages[externalIndex.indexKey] = index
                }
            }
        }

        return ExternalIndexModel(allPages)
    }

    override fun startGeneration(context: OrchidContext, model: ExternalIndexModel) {

    }

    override fun getCollections(
        context: OrchidContext,
        model: ExternalIndexModel
    ): List<OrchidCollection<*>> {
        return model.indexMap.map { (key, value) ->
            PageCollection(this, key, value.allPages)
        }
    }

    class ExternalIndexModel(
        val indexMap: Map<String, OrchidIndex>
    ) : Model {

        override val allPages: List<OrchidPage> = indexMap.values.flatMap { it.allPages }
    }

    class ExternalIndex : OptionsHolder {

        @Option
        lateinit var collectionId: String

        @Option
        lateinit var url: String

        val indexKey: String
            get() {
                return if (collectionId.isNotBlank()) collectionId
                else FilenameUtils.getBaseName(url).replace(".index", "")
            }
    }

    companion object {
        val GENERATOR_KEY = "external"
    }
}
