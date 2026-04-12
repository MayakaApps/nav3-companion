package com.mayakapps.nav3companion

import com.mayakapps.nav3companion.internal.DeepLinkRequest
import com.mayakapps.nav3companion.internal.NavUri
import org.koin.core.scope.Scope

/**
 * Retrieves a [Nav3cDeepLinkHandler] from the Koin scope.
 *
 * It collects all registered deep links from the scope and constructs a handler that can match
 * incoming URI strings against these deep links.
 */
fun <T : Any> Scope.nav3cDeepLinkHandler(): Nav3cDeepLinkHandler<T> {
    val deepLinks = getAll<Nav3cDeepLink<T>>()
    return Nav3cDeepLinkHandler(deepLinks)
}

/**
 * A handler for processing deep links in a Nav3c-based navigation system. It maintains a list of
 * registered deep links and provides a method to handle incoming URI strings by matching them
 * against the registered deep links.
 *
 * @param T The type of the deep link keys that this handler will process.
 * @property deepLinks The list of registered deep links that this handler will use to match
 *    incoming URIs.
 */
class Nav3cDeepLinkHandler<T : Any>(
    private val deepLinks: List<Nav3cDeepLink<out T>>,
) {

    /**
     * Handles the given [uriString] by matching it against the registered deep links. If a match
     * is found, the corresponding handler is invoked. Returns `true` if the URI was successfully
     * handled, `false` otherwise.
     */
    fun handle(uriString: String): Boolean {
        val request = DeepLinkRequest(NavUri(uriString))
        deepLinks.forEach { deepLink ->
            if (deepLink.handle(request)) {
                return true
            }
        }

        return false
    }
}
