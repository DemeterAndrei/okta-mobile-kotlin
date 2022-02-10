/*
 * Copyright 2022-Present Okta, Inc.
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
package com.okta.authfoundation.jwt

import kotlinx.coroutines.withContext
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlin.coroutines.CoroutineContext

/**
 * Represents a Json Web Token.
 */
data class Jwt internal constructor(
    /** Identifies the digital signature algorithm used. */
    val algorithm: String,
    /** Identifies the public key used to verify the ID token. */
    val keyId: String,

    internal val payload: JsonElement,

    /**
     * The base64 encoded signature.
     */
    val signature: String,

    private val json: Json,

    private val computeDispatcher: CoroutineContext,
) {
    /**
     * Used to get access to the payload data in a type safe way.
     *
     * @param deserializationStrategy the [DeserializationStrategy] capable of deserializing the specified type.
     *
     * @throws SerializationException if the payload data can't be deserialized into the specified type.
     * @return the specified type, deserialized from the payload.
     */
    suspend fun <T> payload(deserializationStrategy: DeserializationStrategy<T>): T {
        return withContext(computeDispatcher) {
            json.decodeFromJsonElement(deserializationStrategy, payload)
        }
    }
}