package com.mayakapps.nav3companion

import com.mayakapps.nav3companion.internal.DeepLinkMatcher
import com.mayakapps.nav3companion.internal.DeepLinkPattern
import com.mayakapps.nav3companion.internal.DeepLinkRequest
import com.mayakapps.nav3companion.internal.KeyDecoder
import com.mayakapps.nav3companion.internal.NavUri
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

/**
 * Registers a deep link handler for the specified [uriPattern].
 *
 * The [handler] will be included in [Nav3cDeepLinkHandler] when [nav3cDeepLinkHandler] is called on
 * the Koin scope, allowing it to handle incoming URI strings. [handler] should return `true` if the
 * deep link was handled successfully, or `false` otherwise.
 */
@KoinDslMarker
inline fun <reified T : Any> ScopeDSL.nav3cDeepLink(
    uriPattern: String,
    noinline handler: Scope.(T) -> Boolean,
): KoinDefinition<Nav3cDeepLink<T>> = scoped(qualifier = named<T>()) {
    @OptIn(InternalSerializationApi::class)
    Nav3cDeepLink(
        serializer = T::class.serializer(),
        uriPattern = uriPattern,
        handler = { key -> handler(key) },
    )
}

/**
 * Registers a deep link handler for the specified [uriPattern].
 *
 * The [handler] will be included in [Nav3cDeepLinkHandler] when [nav3cDeepLinkHandler] is called on
 * the Koin scope, allowing it to handle incoming URI strings. [handler] should return `true` if the
 * deep link was handled successfully, or `false` otherwise.
 */
@KoinDslMarker
inline fun <reified T : Any> Module.nav3cDeepLink(
    uriPattern: String,
    noinline handler: Scope.(T) -> Boolean,
): KoinDefinition<Nav3cDeepLink<T>> = single(qualifier = named<T>()) {
    @OptIn(InternalSerializationApi::class)
    Nav3cDeepLink(
        serializer = T::class.serializer(),
        uriPattern = uriPattern,
        handler = { key -> handler(key) },
    )
}

/**
 * Represents a deep link in a Nav3c-based navigation system.
 *
 * It contains the necessary information to match incoming URI strings against a specified pattern
 * and extract the corresponding key of type [T] using the provided [serializer]. The [handler] is
 * invoked when a matching deep link is found, allowing you to perform the appropriate navigation
 * actions based on the extracted key.
 */
data class Nav3cDeepLink<T : Any>(
    val serializer: KSerializer<T>,
    val uriPattern: String,
    val handler: (T) -> Boolean,
) {

    private val pattern: DeepLinkPattern<T> = DeepLinkPattern(serializer, NavUri(uriPattern))

    internal fun handle(request: DeepLinkRequest): Boolean {
        val args = DeepLinkMatcher(request, pattern).match() ?: return false
        val key = KeyDecoder(args).decodeSerializableValue(serializer)
        return handler(key)
    }
}
