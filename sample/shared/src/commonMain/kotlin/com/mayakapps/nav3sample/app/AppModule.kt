package com.mayakapps.nav3sample.app

import com.mayakapps.nav3companion.nav3cDeepLinkHandler
import com.mayakapps.nav3sample.navigation.Navigator
import org.koin.dsl.module

val appModule = module {
    single { Navigator(deepLinkHandler = nav3cDeepLinkHandler()) }
}
