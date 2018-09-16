package com.eden.orchid.testhelpers.com.eden.orchid.api.indexing

import com.caseyjbrooks.clog.Clog
import com.eden.common.util.EdenPair
import com.eden.orchid.api.OrchidContext
import com.eden.orchid.api.indexing.OrchidIndex
import com.eden.orchid.api.indexing.OrchidInternalIndex
import com.eden.orchid.api.indexing.OrchidRootInternalIndex
import com.eden.orchid.api.options.OptionsExtractor
import com.eden.orchid.api.resources.resource.StringResource
import com.eden.orchid.api.theme.pages.OrchidPage
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import strikt.api.expect
import strikt.assertions.contains
import strikt.assertions.containsExactlyInAnyOrder
import strikt.assertions.isNotNull
import strikt.assertions.isSameInstanceAs

class OrchidIndexText {

    private lateinit var context: OrchidContext
    private lateinit var extractor: OptionsExtractor

    private var wikiKey = "wiki"
    private lateinit var wikiPages: List<OrchidPage>
    private lateinit var wikiIndex: OrchidInternalIndex
    private lateinit var wiki_summary: OrchidPage
    private lateinit var wiki_userManual_summary: OrchidPage
    private lateinit var wiki_userManual_inner_page1: OrchidPage
    private lateinit var wiki_userManual_inner_page2: OrchidPage
    private lateinit var wiki_userManual_inner_page3: OrchidPage
    private lateinit var wiki_userManual_inner_deep_page1: OrchidPage
    private lateinit var wiki_userManual_inner_deep_page2: OrchidPage
    private lateinit var wiki_developersGuide_summary: OrchidPage
    private lateinit var wiki_developersGuide_inner_page1: OrchidPage
    private lateinit var wiki_developersGuide_inner_page2: OrchidPage
    private lateinit var wiki_developersGuide_inner_page3: OrchidPage
    private lateinit var wiki_developersGuide_inner_deep_page1: OrchidPage
    private lateinit var wiki_developersGuide_inner_deep_page2: OrchidPage

    private var pagesKey = "pages"
    private lateinit var pagesPages: List<OrchidPage>
    private lateinit var pagesIndex: OrchidInternalIndex
    private lateinit var pages_page1: OrchidPage
    private lateinit var pages_page12: OrchidPage
    private lateinit var pages_page2: OrchidPage
    private lateinit var pages_page22: OrchidPage
    private lateinit var pages_page3: OrchidPage
    private lateinit var pages_page32: OrchidPage

    private lateinit var rootIndex: OrchidRootInternalIndex

    @BeforeEach
    fun setUp() {
        Clog.getInstance().setMinPriority(Clog.Priority.VERBOSE)

        context = mock(OrchidContext::class.java)
        extractor = mock(OptionsExtractor::class.java)
        `when`(context.getEmbeddedData(anyString(), anyString())).thenReturn(EdenPair("", emptyMap()))
        `when`(context.resolve(OptionsExtractor::class.java)).thenReturn(extractor)
        rootIndex = OrchidRootInternalIndex()

        // emulated Wiki generator
        wiki_summary = OrchidPage(StringResource(context, "wiki/index.md", ""), "", "")
        wiki_userManual_summary = OrchidPage(StringResource(context, "wiki/user-manual/summary.md", ""), "", "")
        wiki_userManual_inner_page1 = OrchidPage(StringResource(context, "wiki/user-manual/inner/overview1.md", ""), "", "")
        wiki_userManual_inner_page2 = OrchidPage(StringResource(context, "wiki/user-manual/inner/overview2.md", ""), "", "")
        wiki_userManual_inner_page3 = OrchidPage(StringResource(context, "wiki/user-manual/inner/overview3.md", ""), "", "")
        wiki_userManual_inner_deep_page1 = OrchidPage(StringResource(context, "wiki/user-manual/inner/deep/overview1.md", ""), "", "")
        wiki_userManual_inner_deep_page2 = OrchidPage(StringResource(context, "wiki/user-manual/inner/deep/overview2.md", ""), "", "")
        wiki_developersGuide_summary = OrchidPage(StringResource(context, "wiki/developers-guide/summary.md", ""), "", "")
        wiki_developersGuide_inner_page1 = OrchidPage(StringResource(context, "wiki/developers-guide/inner/overview1.md", ""), "", "")
        wiki_developersGuide_inner_page2 = OrchidPage(StringResource(context, "wiki/developers-guide/inner/overview2.md", ""), "", "")
        wiki_developersGuide_inner_page3 = OrchidPage(StringResource(context, "wiki/developers-guide/inner/overview3.md", ""), "", "")
        wiki_developersGuide_inner_deep_page1 = OrchidPage(StringResource(context, "wiki/developers-guide/inner/deep/overview1.md", ""), "", "")
        wiki_developersGuide_inner_deep_page2 = OrchidPage(StringResource(context, "wiki/developers-guide/inner/deep/overview2.md", ""), "", "")
        wikiPages = listOf(
                wiki_summary,
                wiki_userManual_summary,
                wiki_userManual_inner_page1,
                wiki_userManual_inner_page2,
                wiki_userManual_inner_page3,
                wiki_userManual_inner_deep_page1,
                wiki_userManual_inner_deep_page2,
                wiki_developersGuide_summary,
                wiki_developersGuide_inner_page1,
                wiki_developersGuide_inner_page2,
                wiki_developersGuide_inner_page3,
                wiki_developersGuide_inner_deep_page1,
                wiki_developersGuide_inner_deep_page2
        )
        wikiIndex = OrchidInternalIndex(wikiKey)
        wikiPages.forEach { page -> wikiIndex.addToIndex("$wikiKey/${page.reference.path}", page) }
        rootIndex.addChildIndex(wikiKey, wikiIndex)

        // emulated Pages generator
        pages_page1 = OrchidPage(StringResource(context, "page1.md", ""), "", "")
        pages_page12 = OrchidPage(StringResource(context, "page1/page2.md", ""), "", "")
        pages_page2 = OrchidPage(StringResource(context, "page2.md", ""), "", "")
        pages_page22 = OrchidPage(StringResource(context, "page2/page2.md", ""), "", "")
        pages_page3 = OrchidPage(StringResource(context, "page3.md", ""), "", "")
        pages_page32 = OrchidPage(StringResource(context, "page3/page3.md", ""), "", "")
        pagesPages = listOf(
                pages_page1,
                pages_page12,
                pages_page2,
                pages_page22,
                pages_page3,
                pages_page32
        )
        pagesIndex = OrchidInternalIndex(pagesKey)
        pagesPages.forEach { page -> pagesIndex.addToIndex("$pagesKey/${page.reference.path}", page) }
        rootIndex.addChildIndex(pagesKey, pagesIndex)
    }

    @TestFactory
    fun testIndexBuiltCorrectly(): List<DynamicTest> {
        return listOf(
                DynamicTest.dynamicTest("wikiIndex has all of wikiPages") {
                    expect {
                        that(wikiIndex)
                                .map(OrchidIndex::getAllPages)
                                .containsExactlyInAnyOrder(wikiPages)
                    }
                },
                DynamicTest.dynamicTest("pagesIndex has all of pagesPages") {
                    expect {
                        that(pagesIndex)
                                .map(OrchidIndex::getAllPages)
                                .containsExactlyInAnyOrder(pagesPages)
                    }
                },
                DynamicTest.dynamicTest("rootIndex has all of wikiIndex and pagesPages") {
                    expect {
                        that(rootIndex)
                                .map(OrchidIndex::getAllPages)
                                .containsExactlyInAnyOrder(wikiPages + pagesPages)
                    }
                }
        )
    }

    @TestFactory
    fun testFindPage(): List<DynamicTest> {
        return (wikiPages + pagesPages).map { page ->
            DynamicTest.dynamicTest("when looking up 'findPage ${page.reference.path}' from its path, the index returns the same instance") {
                expect {
                    that(rootIndex)
                            .map { it.findPage(page.reference.path) }
                            .isSameInstanceAs(page)
                }
            }
        }
    }

    @TestFactory
    fun testFind(): List<DynamicTest> {
        return (wikiPages + pagesPages).map { page ->
            DynamicTest.dynamicTest("when looking up 'find ${page.reference.path}' from its path, the index returns a list containing the same instance") {
                expect {
                    that(rootIndex)
                            .map { it.find(page.reference.path) }
                            .contains(page)
                }
            }
        }
    }

    @TestFactory
    fun testFindIndex(): List<DynamicTest> {
        return (wikiPages + pagesPages).map { page ->
            DynamicTest.dynamicTest("when looking up 'findIndex ${page.reference.path}' from its path, the index returns an index containing the same instance as its own page") {
                expect {
                    that(rootIndex)
                            .map { it.findIndex(page.reference.path) }
                            .map(OrchidIndex::getOwnPages)
                            .isNotNull()
                            .contains(page)
                }
            }
        }
    }

    @TestFactory
    fun testFindSubtrees(): List<DynamicTest> {
        return listOf(
                DynamicTest.dynamicTest("the subtree of 'wiki/user-manual' has 5 pages total") {
                    expect {
                        that(rootIndex)
                                .map { it.findIndex("wiki/user-manual") }
                                .map(OrchidIndex::getAllPages)
                                .isNotNull()
                                .containsExactlyInAnyOrder(
                                        wiki_userManual_summary,
                                        wiki_userManual_inner_page1,
                                        wiki_userManual_inner_page2,
                                        wiki_userManual_inner_page3,
                                        wiki_userManual_inner_deep_page1,
                                        wiki_userManual_inner_deep_page2
                                )
                    }
                },
                DynamicTest.dynamicTest("the subtree of 'wiki/developers-guide' has 5 pages total") {
                    expect {
                        that(rootIndex)
                                .map { it.findIndex("wiki/developers-guide") }
                                .map(OrchidIndex::getAllPages)
                                .isNotNull()
                                .containsExactlyInAnyOrder(
                                        wiki_developersGuide_summary,
                                        wiki_developersGuide_inner_page1,
                                        wiki_developersGuide_inner_page2,
                                        wiki_developersGuide_inner_page3,
                                        wiki_developersGuide_inner_deep_page1,
                                        wiki_developersGuide_inner_deep_page2
                                )
                    }
                }
        )
    }

}