package com.mayakapps.nav3sample.feature.profile.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class ProfileRoute(
    val userId: String,
) : NavKey
