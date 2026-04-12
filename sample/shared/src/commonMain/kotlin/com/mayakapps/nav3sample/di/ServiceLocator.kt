package com.mayakapps.nav3sample.di

import com.mayakapps.nav3sample.navigation.Navigator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("unused")
class ServiceLocator : KoinComponent {
    val navigator: Navigator by inject()
}
