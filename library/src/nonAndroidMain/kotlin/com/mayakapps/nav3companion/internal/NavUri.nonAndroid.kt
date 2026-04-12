/*
 * Copyright 2024 The Android Open Source Project
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

package com.mayakapps.nav3companion.internal

internal actual abstract class NavUri {

    actual abstract fun getScheme(): String?

    actual abstract fun getAuthority(): String?

    actual abstract fun getQuery(): String?

    actual abstract fun getFragment(): String?

    actual abstract fun getPathSegments(): List<String>

    actual open fun getQueryParameterNames(): Set<String> = error("Abstract implementation")

    actual open fun getQueryParameters(key: String): List<String> =
        error("Abstract implementation")

    actual abstract override fun toString(): String
}

internal actual object NavUriUtils {
    actual fun encode(s: String, allow: String?): String = InternalUri.encode(s, allow)

    actual fun decode(s: String): String = InternalUri.decode(s)

    actual fun parse(uriString: String): NavUri = ActualUri(uriString)
}

private class ActualUri(private val uriString: String) : NavUri() {

    private companion object {
        private val SCHEME_PATTERN = Regex("^[a-zA-Z][a-zA-Z0-9+.-]*$")
        private val QUERY_PATTERN = Regex("^[^?#]+\\?([^#]*).*")
        private val FRAGMENT_PATTERN = Regex("#(.+)")
    }

    private val schemeSeparatorIndex by lazy { uriString.indexOf(':') }

    private val _scheme: String? by lazy {
        val ssi = schemeSeparatorIndex
        if (ssi <= 0) return@lazy null

        val scheme = uriString.substring(0, ssi)
        if (SCHEME_PATTERN.matches(scheme)) scheme else null
    }

    private val _authority: String? by lazy {
        val ssi = schemeSeparatorIndex
        val length = uriString.length

        val authorityStart = when {
            ssi > -1 && ssi + 2 < length && uriString[ssi + 1] == '/' && uriString[ssi + 2] == '/' -> {
                ssi + 3
            }

            ssi == -1 && length > 1 && uriString[0] == '/' && uriString[1] == '/' -> 2
            else -> return@lazy null
        }

        var authorityEnd = authorityStart
        while (authorityEnd < length) {
            when (uriString[authorityEnd]) {
                '/', '\\', '?', '#' -> break
            }
            authorityEnd++
        }

        if (authorityEnd == authorityStart) "" else uriString.substring(authorityStart, authorityEnd)
    }

    private val _query: String? by lazy { QUERY_PATTERN.find(uriString)?.groups?.get(1)?.value }

    private val _fragment: String? by lazy {
        FRAGMENT_PATTERN.find(uriString)?.groups?.get(1)?.value
    }

    private val _pathSegments: List<String> by lazy {
        val ssi = schemeSeparatorIndex
        if (ssi > -1) {
            if (ssi + 1 == uriString.length) return@lazy emptyList()
            if (uriString.getOrNull(ssi + 1) != '/') return@lazy emptyList()
        }

        val path = InternalUri.parsePath(uriString, ssi)

        path.split('/').map { InternalUri.decode(it) }
    }

    override fun getScheme(): String? = _scheme

    override fun getAuthority(): String? = _authority

    override fun getQuery(): String? = _query

    override fun getFragment(): String? = _fragment

    override fun getPathSegments(): List<String> = _pathSegments

    private fun isHierarchical(): Boolean {
        if (schemeSeparatorIndex == -1) return true // All relative URIs are hierarchical.
        if (uriString.length == schemeSeparatorIndex + 1) return false // No ssp.

        // If the ssp starts with a '/', this is hierarchical.
        return uriString[schemeSeparatorIndex + 1] == '/'
    }

    override fun getQueryParameters(key: String): List<String> {
        require(isHierarchical())
        val query = _query ?: return emptyList()
        val encodedKey = InternalUri.encode(key)

        return query.split('&').mapNotNull {
            val i = it.indexOf('=')
            when {
                i == -1 -> if (it == encodedKey) "" else null
                it.substring(0, i) == encodedKey -> {
                    InternalUri.decode(it.substring(i + 1))
                }

                else -> null
            }
        }
    }

    override fun getQueryParameterNames(): Set<String> {
        require(isHierarchical())
        val query = _query ?: return emptySet()

        return query
            .split('&')
            .map {
                val index = it.indexOf('=')
                if (index == -1) return@map it else InternalUri.decode(it.substring(0, index))
            }
            .toSet()
    }

    override fun toString(): String = uriString
}
