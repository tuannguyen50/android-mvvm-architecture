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

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tuanthanhnguyen.androidmvvmarchitecture.data.repository.PhotoRepository
import tuanthanhnguyen.androidmvvmarchitecture.ui.common.result.Resource
import tuanthanhnguyen.androidmvvmarchitecture.util.scheduler.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import tuanthanhnguyen.androidmvvmarchitecture.ui.model.PhotoModel
import tuanthanhnguyen.androidmvvmarchitecture.ui.model.mapper.PhotoModelMapper
import javax.inject.Inject

class PhotosViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val schedulerProvider: SchedulerProvider
) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _photoDataModelListState = MutableLiveData<Resource<List<PhotoModel>>>()

    val photoDataModelListState: LiveData<Resource<List<PhotoModel>>> =
        _photoDataModelListState

    private val _refresh = MutableLiveData<Boolean>()
    val refresh: LiveData<Boolean> = _refresh

    private var _photoDataModels: List<PhotoModel>? = null

    private var currentPage = 0

    fun getPhotosPageNumber(page: Int) {
        currentPage = page
        _photoDataModelListState.value = Resource.loading()

        photoRepository.getPhotosWithOfflineFallback(page)
            .map {
                return@map PhotoModelMapper.photoDataModelsToPhotoModels(it)
            }
            .map { newPageData ->
                if (page == 1) {
                    _photoDataModels = emptyList()
                }
                val currentList = _photoDataModels.orEmpty()
                val updatedList = currentList + newPageData
                _photoDataModels = updatedList
                return@map updatedList
            }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(object : SingleObserver<List<PhotoModel>> {
                override fun onSubscribe(disposable: Disposable) {
                    compositeDisposable.add(disposable)
                }

                override fun onSuccess(photoDataModelList: List<PhotoModel>) {
                    _photoDataModelListState.value = Resource.success(photoDataModelList)
                }

                override fun onError(e: Throwable) {
                    _photoDataModelListState.value = Resource.failure(e.message)
                }
            })
    }

    fun onRefresh() {
        _refresh.value = true
    }

    fun retry() {
        getPhotosPageNumber(currentPage)
    }

    @SuppressLint("EmptySuperCall")
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}