package com.mayakapps.nav3sample.feature.home.impl

import com.mayakapps.nav3sample.feature.home.api.HomeRoute
import com.mayakapps.nav3sample.feature.profile.api.ProfileRoute
import com.mayakapps.nav3sample.feature.search.api.SearchRoute
import com.mayakapps.nav3sample.feature.settings.api.SettingsRoute
import com.mayakapps.nav3sample.navigation.Navigator
import com.mayakapps.nav3companion.nav3cEntry
import org.koin.dsl.module

val homeModule = module {
    nav3cEntry<HomeRoute> {
        val navigator = get<Navigator>()
        HomeScreen(
            onSearchClick = { navigator.push(SearchRoute(query = "compose")) },
            onProfileClick = { userId -> navigator.push(ProfileRoute(userId)) },
            onSettingsClick = { navigator.push(SettingsRoute()) },
        )
    }
}
