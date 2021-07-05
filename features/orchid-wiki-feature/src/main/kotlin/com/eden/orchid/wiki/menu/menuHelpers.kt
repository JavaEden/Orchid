package com.eden.orchid.wiki.menu

import com.eden.orchid.api.theme.menus.MenuItem
import com.eden.orchid.wiki.pages.WikiPage

internal var wikiMenuItemComparator = Comparator { o1: MenuItem, o2: MenuItem ->
    var o1WikiPage = getWikiPageFromMenuItem(o1)
    var o2WikiPage = getWikiPageFromMenuItem(o2)

    var o1Order = if (o1WikiPage != null) o1WikiPage.order else 0
    var o2Order = if (o2WikiPage != null) o2WikiPage.order else 0

    o1Order.compareTo(o2Order)
}

private fun getWikiPageFromMenuItem(item: MenuItem): WikiPage? {
    if (item.page != null && item.page is WikiPage) {
        return item.page as WikiPage
    } else if (item.children.isNotEmpty() && item.children.first().page is WikiPage) {
        return item.children.first().page as WikiPage
    }

    return null
}
