package com.eden.orchid.writersblocks.tags

import com.eden.orchid.api.compilers.TemplateTag
import com.eden.orchid.api.options.annotations.Description
import com.eden.orchid.api.options.annotations.Option

@Description("Embed a Github Gist in your page.", name = "Github Gist")
class GistTag : TemplateTag("gist", Type.Simple, true) {

    @Option
    @Description("The Github username who owns the Gist.")
    lateinit var user: String

    @Option
    @Description("The Github Gist Id.")
    lateinit var id: String

    @Option
    @Description("An optional file in the Gist, to only show that one file instead of all files in the Gist.")
    lateinit var file: String

    override fun parameters() = arrayOf(::user.name, ::id.name, ::file.name)
}
