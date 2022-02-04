/*
 * Copyright 2021-Present Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.authfoundation.client

/**
 * Describes the result from the OidcClient.
 */
sealed class OidcClientResult<T> {
    /** An error result. */
    data class Error<T> internal constructor(val exception: Exception) : OidcClientResult<T>() {
        data class HttpResponseException internal constructor(
            val responseCode: Int,
            val error: String?,
            val errorDescription: String?,
        ) : Exception()
    }

    /** Success with the expected result. */
    data class Success<T> internal constructor(val result: T) : OidcClientResult<T>()
}
