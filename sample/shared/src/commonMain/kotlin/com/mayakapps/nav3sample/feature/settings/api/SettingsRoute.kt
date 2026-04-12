package com.mayakapps.nav3sample.feature.settings.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SettingsRoute(
    val section: String = "general",
) : NavKey
