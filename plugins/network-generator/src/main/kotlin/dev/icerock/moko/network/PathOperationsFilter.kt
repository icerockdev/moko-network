/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.network

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.Paths

object PathOperationsFilter {

    private val pathOperationsFilterSet = mutableSetOf<String>()

    fun addTagToFilter(tag: String) {
        pathOperationsFilterSet.add(tag)
    }

    internal fun filterPaths(paths: Paths?) {
        paths?.forEach { (_, pathItem) ->
            with(pathItem) {
                if (get.needFilterOperation(pathOperationsFilterSet)) {
                    get = null
                }
                if (put.needFilterOperation(pathOperationsFilterSet)) {
                    put = null
                }
                if (post.needFilterOperation(pathOperationsFilterSet)) {
                    post = null
                }
                if (delete.needFilterOperation(pathOperationsFilterSet)) {
                    delete = null
                }
                if (options.needFilterOperation(pathOperationsFilterSet)) {
                    options = null
                }
                if (head.needFilterOperation(pathOperationsFilterSet)) {
                    head = null
                }
                if (patch.needFilterOperation(pathOperationsFilterSet)) {
                    patch = null
                }
                if (trace.needFilterOperation(pathOperationsFilterSet)) {
                    trace = null
                }
            }
        }
    }

    private fun Operation?.needFilterOperation(filterTagNameSet: Set<String>): Boolean {
        return this?.tags?.any(filterTagNameSet::contains) == true
    }
}
