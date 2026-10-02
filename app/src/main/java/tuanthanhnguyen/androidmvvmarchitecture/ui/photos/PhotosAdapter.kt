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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import tuanthanhnguyen.androidmvvmarchitecture.databinding.ItemLoadMoreBinding
import tuanthanhnguyen.androidmvvmarchitecture.databinding.ItemPhotoBinding
import tuanthanhnguyen.androidmvvmarchitecture.R
import tuanthanhnguyen.androidmvvmarchitecture.ui.common.listener.RetryListener
import tuanthanhnguyen.androidmvvmarchitecture.ui.common.result.Status
import tuanthanhnguyen.androidmvvmarchitecture.ui.common.view.DataBoundListCustomAdapter
import tuanthanhnguyen.androidmvvmarchitecture.ui.model.PhotoModel
import tuanthanhnguyen.androidmvvmarchitecture.util.scheduler.SchedulerProvider

class PhotosAdapter(
    schedulerProvider: SchedulerProvider,
    private val retryListener: RetryListener
) : DataBoundListCustomAdapter<Any, ViewDataBinding>(
    schedulerProvider = schedulerProvider,
    diffCallback = object : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is PhotoModel && newItem is PhotoModel) {
                return oldItem.id == newItem.id
            }
            // Item has view type is LOAD_MORE will be handled in handleLoadMore function
            return true
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            if (oldItem is PhotoModel && newItem is PhotoModel) {
                return oldItem.id == newItem.id
            }
            // Item has view type is LOAD_MORE will be handled in handleLoadMore function
            return false
        }
    }
) {

    private enum class ItemType(val value: Int) {
        ITEM(1),
        LOAD_MORE(2)
    }

    private var status: Status = Status.LOADING
    private var errorMessage: String? = null

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        val layoutInflater = LayoutInflater.from(parent.context)
        val layoutId = when (viewType) {
            ItemType.ITEM.value -> R.layout.item_photo
            else -> R.layout.item_load_more
        }
        return DataBindingUtil.inflate(
            layoutInflater,
            layoutId,
            parent,
            false
        )
    }

    override fun bind(binding: ViewDataBinding, item: Any) {
        when (binding) {
            is ItemPhotoBinding -> {
                if (item is PhotoModel) {
                    binding.photoModel = item
                }
            }
            is ItemLoadMoreBinding -> {
                binding.retryListener = retryListener
                binding.status = status
                binding.errorMessage = errorMessage
            }
        }
    }

    fun handleLoadMore(status: Status, errorMessage: String?) {
        this.status = status
        this.errorMessage = errorMessage
        notifyItemChanged(itemCount - 1)
    }

    override fun getItemCount(): Int = currentList.size + ItemType.entries.size - 1

    override fun getItem(position: Int): Any {
        if (position < itemCount - ItemType.entries.size + 1) {
            return currentList.get(position)
        } else {
            return Any()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount - ItemType.entries.size + 1) {
            ItemType.ITEM.value
        } else {
            ItemType.LOAD_MORE.value
        }
    }

    fun getTotalItemCountBeforeLoadPageOne(): Int {
        return ItemType.entries.size - 1
    }
}