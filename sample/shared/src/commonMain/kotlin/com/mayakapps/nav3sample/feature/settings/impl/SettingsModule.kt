package com.mayakapps.nav3sample.feature.settings.impl

import com.mayakapps.nav3companion.nav3cDeepLink
import com.mayakapps.nav3companion.nav3cEntry
import com.mayakapps.nav3sample.feature.settings.api.SettingsRoute
import com.mayakapps.nav3sample.navigation.Navigator
import org.koin.dsl.module

val settingsModule = module {
    nav3cDeepLink<SettingsRoute>("nav3companion://settings/{section}") { route ->
        get<Navigator>().push(route)
        true
    }

    nav3cEntry<SettingsRoute> { route ->
        SettingsScreen(
            section = route.section,
            onBack = { get<Navigator>().pop() },
        )
    }
}
