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

// Tuan Thanh Nguyen modified this file.

package tuanthanhnguyen.androidmvvmarchitecture.ui.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerFragment
import tuanthanhnguyen.androidmvvmarchitecture.databinding.FragmentPhotosBinding
import tuanthanhnguyen.androidmvvmarchitecture.R
import tuanthanhnguyen.androidmvvmarchitecture.di.factory.ViewModelFactory
import tuanthanhnguyen.androidmvvmarchitecture.ui.common.listener.RetryListener
import tuanthanhnguyen.androidmvvmarchitecture.ui.common.result.Status
import tuanthanhnguyen.androidmvvmarchitecture.ui.common.view.BoundedRecyclerViewLoadMoreAfterLoadPageOneScrollListener
import tuanthanhnguyen.androidmvvmarchitecture.ui.common.view.GridSpacingItemDecoration
import tuanthanhnguyen.androidmvvmarchitecture.util.scheduler.SchedulerProvider
import tuanthanhnguyen.androidmvvmarchitecture.util.ui.AutoClearedValue
import javax.inject.Inject

class PhotosFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    internal var binding by AutoClearedValue<FragmentPhotosBinding>(this)

    internal var adapter by AutoClearedValue<PhotosAdapter>(this)

    internal val photosViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(PhotosViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(
            inflater,
            container,
            false
        )
        binding.viewModel = photosViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        photosViewModel.photoDataModelListState.observe(viewLifecycleOwner, Observer { resource ->
            if (resource.status == Status.SUCCESS) {
                adapter.handleLoadMore(resource.status, resource.errorMessage)
                adapter.submitList(resource.data)
            } else {
                // Post this UI operation to the next UI frame to fix this warning "Cannot call
                // this method in a scroll callback. Scroll callbacks mightbe run during a measure
                // & layout pass where you cannot change theRecyclerView data. Any method call
                // that might change the structureof the RecyclerView or the adapter contents
                // should be postponed tothe next frame.".
                binding.photosRecyclerView.post(object : Runnable {
                    override fun run() {
                        adapter.handleLoadMore(resource.status, resource.errorMessage)
                    }
                })
            }
            binding.photosSwipeRefreshLayout.isRefreshing = false
        })

        photosViewModel.getPhotosPageNumber(1)
    }

    private fun initRecyclerView() {
        adapter = PhotosAdapter(
            schedulerProvider,
            object : RetryListener {
                override fun retry() {
                    photosViewModel.retry()
                }
            }
        )

        // Fix bind items for the first page then the Recycler View does not fully display the
        // first item
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.photosRecyclerView.scrollToPosition(0)
                }
            }
        })
        val spanCount = 2
        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.photosRecyclerView.layoutManager = gridLayoutManager

        val scrollListener =
            object : BoundedRecyclerViewLoadMoreAfterLoadPageOneScrollListener(
                gridLayoutManager,
                adapter.getTotalItemCountBeforeLoadPageOne()
            ) {
                override fun onLoadMoreAfterLoadPageOne(
                    pageStartLoadMoreFromTwo: Int,
                    totalItemsCount: Int,
                    recyclerView: RecyclerView
                ) {
                    photosViewModel.getPhotosPageNumber(pageStartLoadMoreFromTwo)
                }
            }
        photosViewModel.refresh.observe(viewLifecycleOwner, Observer {
            adapter.submitList(emptyList())
            adapter.handleLoadMore(Status.LOADING, null)
            scrollListener.resetStateBeforeLoadPageOne()

            binding.photosRecyclerView.post {
                photosViewModel.getPhotosPageNumber(1)
            }
        })
        val gridSpacingItemInPixels =
            resources.getDimensionPixelSize(R.dimen.grid_photo_item_spacing)
        val includeEdge = true
        binding.photosRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount,
                gridSpacingItemInPixels,
                includeEdge
            )
        )
        binding.photosRecyclerView.addOnScrollListener(scrollListener)
        binding.photosRecyclerView.adapter = adapter
    }
}