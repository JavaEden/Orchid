package com.eden.orchid.testhelpers

import com.eden.common.util.EdenUtils
import com.eden.orchid.api.OrchidContext

class TestResults(
    val testContext: OrchidContext?,
    val renderedPageMap: Map<String, TestRenderer.TestRenderedPage>,
    val collections: List<TestRenderer.TestIndexedCollection>,
    val isRenderingSuccess: Boolean,
    val thrownException: Throwable?
) {

    override fun toString(): String {
        val v1 = if (isRenderingSuccess && thrownException == null) "success" else "failure"
        return "TestResults: " + v1 + " with " + renderedPageMap.size + " pages"
    }

    fun showResults(): String {
        return if (renderedPageMap.isEmpty()) {
            "(empty site)"
        } else {
            renderedPageMap.keys.sorted().joinToString("\n")
        }
    }

    fun printResults(): TestResults {
        println(showResults())
        return this
    }

    // Helpers
// ---------------------------------------------------------------------------------------------------------------------

    fun getCollections(
        collectionType: String,
        collectionId: String
    ): List<TestRenderer.TestIndexedCollection> {
        var stream = collections
        if (!EdenUtils.isEmpty(collectionType)) {
            stream = stream.filter { collectionType == it.collectionType }
        }
        if (!EdenUtils.isEmpty(collectionId)) {
            stream = stream.filter { collectionId == it.collectionId }
        }
        return stream
    }
}
