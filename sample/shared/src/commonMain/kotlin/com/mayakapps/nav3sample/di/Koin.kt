package com.mayakapps.nav3sample.di

import com.mayakapps.nav3sample.app.appModule
import com.mayakapps.nav3sample.feature.home.impl.homeModule
import com.mayakapps.nav3sample.feature.profile.impl.profileModule
import com.mayakapps.nav3sample.feature.search.impl.searchModule
import com.mayakapps.nav3sample.feature.settings.impl.settingsModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

@Suppress("Unused") // Used in `iosApp`
fun initKoin() = initKoin {}

fun initKoin(appDeclaration: KoinAppDeclaration) = startKoin {
    appDeclaration()

    modules(
        appModule,
        homeModule,
        searchModule,
        profileModule,
        settingsModule,
    )
}
