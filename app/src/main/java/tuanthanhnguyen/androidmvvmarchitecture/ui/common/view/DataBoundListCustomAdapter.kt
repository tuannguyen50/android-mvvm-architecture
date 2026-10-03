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

package tuanthanhnguyen.androidmvvmarchitecture.ui.common.view

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import tuanthanhnguyen.androidmvvmarchitecture.util.scheduler.SchedulerProvider

abstract class DataBoundListCustomAdapter<T, V : ViewDataBinding>(
    schedulerProvider: SchedulerProvider,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, DataBoundViewHolder<V>>(
    AsyncDifferConfig.Builder<T>(diffCallback)
        .setBackgroundThreadExecutor { command ->
            // 1. Why managing the returned disposable is not needed?
            // - Short-lived task: DiffUtil computation is a closed-loop operation that completes
            //   almost instantly (few milliseconds) and releases itself. It doesn't run
            //   infinitely.
            // - Non-interruptible: DiffUtil's algorithm doesn't check the isDisposed flag inside
            //   its loops. Calling dispose() sends an interrupt signal, but the execution will
            //   run until completion anyway.
            // - Handled by ListAdapter: ListAdapter tracks data generations internally. If a new
            //   list is submitted, it automatically ignores and discards the stale diff results
            //   once they finish.
            //
            // 2. Why adding the disposal logic to the view model is wrong?
            // - Reverse Memory Leak: ViewModel outlives the Activity/Fragment/Adapter. Adding an
            //   Adapter-scoped disposable to the ViewModel's CompositeDisposable creates a strong
            //   reference back to the Adapter. When the screen is destroyed or rotated, the
            //   Adapter cannot be garbage collected, causing a severe leak.
            // - Memory Bloat: This executor runs every time submitList() is triggered.
            //   Continuously adding these short-lived disposables into the ViewModel without
            //   clearing them will result in thousands of dead references, wasting RAM layout.
            schedulerProvider.computation().scheduleDirect(command)
        }
        .build()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<V> {
        val binding = createBinding(parent, viewType)
        return DataBoundViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup, viewType: Int): V

    override fun onBindViewHolder(holder: DataBoundViewHolder<V>, position: Int) {
        bind(holder.binding, getItem(position))
        holder.binding.executePendingBindings()
    }

    protected abstract fun bind(binding: V, item: T)
}