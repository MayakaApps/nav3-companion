package com.mayakapps.nav3sample.feature.search.impl

import com.mayakapps.nav3companion.nav3cDeepLink
import com.mayakapps.nav3sample.feature.profile.api.ProfileRoute
import com.mayakapps.nav3sample.feature.search.api.SearchRoute
import com.mayakapps.nav3sample.navigation.Navigator
import com.mayakapps.nav3companion.nav3cEntry
import org.koin.dsl.module

val searchModule = module {
    nav3cDeepLink<SearchRoute>("nav3companion://search?query={query}") { route ->
        get<Navigator>().push(route)
        true
    }

    nav3cEntry<SearchRoute> { route ->
        val navigator = get<Navigator>()
        SearchScreen(
            query = route.query,
            onOpenProfile = { userId -> navigator.push(ProfileRoute(userId)) },
            onBack = { navigator.pop() },
        )
    }
}
