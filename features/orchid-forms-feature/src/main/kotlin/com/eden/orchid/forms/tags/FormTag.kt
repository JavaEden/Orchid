package com.eden.orchid.forms.tags

import com.eden.orchid.api.compilers.TemplateTag
import com.eden.orchid.api.options.annotations.Description
import com.eden.orchid.api.options.annotations.Option
import com.eden.orchid.forms.model.Form

@Description("Render one of your predefined forms", name = "Form")
class FormTag : TemplateTag("form", Type.Simple, true) {

    @Option
    @Description(
        "The Form to render. Can be either a key to an indexed form definition, or a complete form " +
            "definition for a one-off use."
    )
    var form: Form? = null

    override fun parameters() = arrayOf(::form.name)
}
