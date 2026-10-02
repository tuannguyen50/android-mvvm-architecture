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

package tuanthanhnguyen.androidmvvmarchitecture.ui.common.result

data class Resource<out T>(
    val data: T? = null,
    val status: Status = Status.LOADING,
    val errorMessage: String? = null
) {

    companion object {

        fun <T> success(data: T): Resource<T> = Resource(data, Status.SUCCESS, null)

        fun <T> failure(errorMessage: String?): Resource<T> =
            Resource(null, Status.ERROR, errorMessage)

        fun <T> loading(): Resource<T> = Resource(null, Status.LOADING, null)
    }
}