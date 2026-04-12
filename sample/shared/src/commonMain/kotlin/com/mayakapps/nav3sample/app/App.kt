package com.mayakapps.nav3sample.app

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.mayakapps.nav3companion.Nav3cSavedStateConfiguration
import com.mayakapps.nav3companion.nav3cEntryProvider
import com.mayakapps.nav3sample.feature.home.api.HomeRoute
import com.mayakapps.nav3sample.navigation.Navigator
import org.koin.compose.koinInject

@Composable
fun App() {
    val navigator = koinInject<Navigator>()
    val backStack = rememberNavBackStack(
        configuration = Nav3cSavedStateConfiguration(),
        HomeRoute,
    )

    // `remember` is used instead of `LaunchedEffect` to apply queued deep links before the first
    // composition,
    remember(navigator, backStack) {
        navigator.initializeBackStack(backStack)
    }

    NavDisplay(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeContent),
        backStack = backStack,
        onBack = { navigator.pop() },
        entryProvider = nav3cEntryProvider(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
    )
}
