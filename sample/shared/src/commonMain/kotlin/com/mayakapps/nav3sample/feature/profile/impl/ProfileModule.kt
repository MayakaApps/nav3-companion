package com.mayakapps.nav3sample.feature.profile.impl

import com.mayakapps.nav3companion.nav3cDeepLink
import com.mayakapps.nav3companion.nav3cEntry
import com.mayakapps.nav3sample.feature.home.api.HomeRoute
import com.mayakapps.nav3sample.feature.profile.api.ProfileRoute
import com.mayakapps.nav3sample.feature.search.api.SearchRoute
import com.mayakapps.nav3sample.navigation.Navigator
import org.koin.dsl.module

val profileModule = module {
    nav3cDeepLink<ProfileRoute>("nav3companion://profile/{userId}") { _ ->
        false
    }

    nav3cDeepLink<ProfileRoute>("nav3companion://profile/{userId}") { route ->
        get<Navigator>().setBackStack(
            listOf(
                HomeRoute,
                SearchRoute(query = route.userId),
                ProfileRoute(userId = route.userId),
            ),
        )

        true
    }

    nav3cEntry<ProfileRoute> { route ->
        ProfileScreen(
            userId = route.userId,
            onBack = { get<Navigator>().pop() },
        )
    }
}
