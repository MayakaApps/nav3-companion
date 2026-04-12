package com.mayakapps.nav3companion.internal

/**
 * Parse the requested Uri and store it in a easily readable format
 *
 * @param uri the target deeplink uri to link to
 */
internal class DeepLinkRequest(
    val uri: NavUri
) {
    /**
     * A list of path segments
     */
    val pathSegments: List<String> = uri.getPathSegments()

    /**
     * A map of query name to query value
     */
    val queries = buildMap {
        uri.getQueryParameterNames().forEach { argName ->
            this[argName] = uri.getQueryParameters(argName).first()
        }
    }
}
