/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Tuan Thanh Nguyen refactored this code based on the solution on GitHub. This link to the
// article about this solution is https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView,
// and this code implements the RecyclerView and is refactored based on this solution.

package tuanthanhnguyen.androidmvvmarchitecture.ui.common.view

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class BoundedRecyclerViewLoadMoreAfterLoadPageOneScrollListener
    : RecyclerView.OnScrollListener {

    private var visibleThreshold = 5

    private var pageStartLoadMoreFromTwo = 2

    // The total number of items previous
    private var previousTotalItemCount = 0

    private var loadingAfterLoadPageOne = false

    private var totalItemCountBeforeLoadPageOne = 0

    private var layoutManager: RecyclerView.LayoutManager

    constructor(
        linearLayoutManager: LinearLayoutManager,
        totalItemCountBeforeLoadPageOne: Int
    ) {
        this.layoutManager = linearLayoutManager
        this.totalItemCountBeforeLoadPageOne = totalItemCountBeforeLoadPageOne
    }

    constructor(
        gridLayoutManager: GridLayoutManager,
        totalItemCountBeforeLoadPageOne: Int
    ) {
        this.layoutManager = gridLayoutManager
        this.totalItemCountBeforeLoadPageOne = totalItemCountBeforeLoadPageOne
        visibleThreshold = visibleThreshold * gridLayoutManager.getSpanCount()
    }

    constructor(
        staggeredGridLayoutManager: StaggeredGridLayoutManager,
        totalItemCountBeforeLoadPageOne: Int
    ) {
        this.layoutManager = staggeredGridLayoutManager
        this.totalItemCountBeforeLoadPageOne = totalItemCountBeforeLoadPageOne
        visibleThreshold = visibleThreshold * staggeredGridLayoutManager.getSpanCount()
    }

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    // Note: onScrolled is trigger only when has new item added to the list
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = layoutManager.getItemCount()

        if (layoutManager is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions = (layoutManager as StaggeredGridLayoutManager)
                .findLastVisibleItemPositions(null)
            // Get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
        } else if (layoutManager is GridLayoutManager) {
            lastVisibleItemPosition = (layoutManager as GridLayoutManager)
                .findLastVisibleItemPosition()
        } else if (layoutManager is LinearLayoutManager) {
            lastVisibleItemPosition = (layoutManager as LinearLayoutManager)
                .findLastVisibleItemPosition()
        }

        if (totalItemCount > previousTotalItemCount) {
            if (totalItemCount > totalItemCountBeforeLoadPageOne) {
                loadingAfterLoadPageOne = false
            }
            previousTotalItemCount = totalItemCount
        }

        if (!loadingAfterLoadPageOne
            && (lastVisibleItemPosition + visibleThreshold) > totalItemCount
            && totalItemCount > totalItemCountBeforeLoadPageOne
        ) {
            loadingAfterLoadPageOne = true
            onLoadMoreAfterLoadPageOne(
                pageStartLoadMoreFromTwo,
                totalItemCount,
                recyclerView
            )
            pageStartLoadMoreFromTwo++
        }
    }

    fun resetStateBeforeLoadPageOne() {
        this.pageStartLoadMoreFromTwo = 2
        this.loadingAfterLoadPageOne = false
    }

    abstract fun onLoadMoreAfterLoadPageOne(
        pageStartLoadMoreFromTwo: Int,
        totalItemsCount: Int,
        recyclerView: RecyclerView
    )
}