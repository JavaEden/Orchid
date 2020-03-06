package com.eden.orchid.impl.themes.tags

import com.eden.orchid.api.compilers.TemplateTag
import com.eden.orchid.api.options.annotations.Description
import com.eden.orchid.api.options.annotations.Option
import org.apache.commons.lang3.RandomStringUtils

@Description("Display multiple content sections in an expandable accordion.", name = "Accordion")
class AccordionTag : TemplateTag("accordion", Type.Tabbed, true) {

    @Option
    @Description("The unique id of this accordion. Defaults to a random value.")
    var id: String = ""
        get() {
            if (field.isBlank()) field = RandomStringUtils.random(10, true, true)
            return field
        }

    override fun parameters(): Array<String> {
        return arrayOf("id")
    }

    override fun getNewTab(key: String?, content: String?): TemplateTag.Tab {
        return Tab(key, content)
    }

    class Tab(name: String?, content: String?) : TemplateTag.SimpleTab("accordion", name, content) {

        @Option
        @Description("The title of the section")
        lateinit var title: String

        override fun parameters(): Array<String> {
            return arrayOf("title")
        }
    }

}
