package com.eden.orchid.wiki

import com.eden.common.util.EdenUtils
import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.options.OptionArchetype
import com.eden.orchid.api.options.annotations.Description
import com.eden.orchid.wiki.pages.WikiPage
import com.eden.orchid.wiki.pages.WikiSummaryPage
import org.json.JSONObject
import javax.inject.Inject

@Description(
    "Configure all pages in the same wiki section. Additional configuration values from the same object " +
        "which configures the Wiki Generator, at a sub-object for that page's wiki section.",
    name = "Wiki Section"
)
class WikiSectionArchetype
@Inject
constructor(
    val context: OrchidContext
) : OptionArchetype {

    override fun getOptions(target: Any, archetypeKey: String): Map<String, Any>? {
        if (target !is WikiPage && target !is WikiSummaryPage) return null

        if (target is WikiPage) {
            if (!EdenUtils.isEmpty(target.section)) {
                val contextOptions = context.query("$archetypeKey.${target.section}")
                if (EdenUtils.elementIsObject(contextOptions)) {
                    return (contextOptions?.element as? JSONObject)?.toMap()
                }
            }
        } else if (target is WikiSummaryPage) {
            if (!EdenUtils.isEmpty(target.section)) {
                val contextOptions = context.query("$archetypeKey.${target.section}")
                if (EdenUtils.elementIsObject(contextOptions)) {
                    return (contextOptions?.element as? JSONObject)?.toMap()
                }
            }
        }

        return null
    }
}
