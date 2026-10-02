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

package tuanthanhnguyen.androidmvvmarchitecture.di.module.main

import androidx.lifecycle.ViewModel
import tuanthanhnguyen.androidmvvmarchitecture.di.factory.ViewModelKey
import tuanthanhnguyen.androidmvvmarchitecture.ui.photos.PhotosFragment
import tuanthanhnguyen.androidmvvmarchitecture.ui.photos.PhotosViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface MainActivityFragmentBuildersModule {

    @ContributesAndroidInjector
    fun contributePhotosFragment(): PhotosFragment

    @Binds
    @IntoMap
    @ViewModelKey(PhotosViewModel::class)
    fun bindPhotosViewModel(
        photosViewModel: PhotosViewModel
    ): ViewModel
}