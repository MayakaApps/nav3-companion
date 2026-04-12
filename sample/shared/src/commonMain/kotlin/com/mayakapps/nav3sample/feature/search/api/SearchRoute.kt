package com.mayakapps.nav3sample.feature.search.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SearchRoute(
    val query: String = "",
) : NavKey
