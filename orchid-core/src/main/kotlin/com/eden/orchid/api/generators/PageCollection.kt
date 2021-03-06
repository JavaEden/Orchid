package com.eden.orchid.api.generators

import com.eden.orchid.api.options.annotations.Description
import com.eden.orchid.api.theme.pages.OrchidPage

@Description(
    value = "A File Collection represents a collection of OrchidPages that come from individually-specified pages in " +
        "your resources. A page is matched from a File Collection with an 'itemId' matching the page's title.",
    name = "File Collection"
)
class PageCollection<T : OrchidPage>(
    generator: OrchidGenerator<*>,
    collectionId: String,
    items: List<T>
) : OrchidCollection<T>(generator, collectionId, { items.stream() })
