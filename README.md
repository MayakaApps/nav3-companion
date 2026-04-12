# Navigation3 Companion

Navigation3 Companion is a Kotlin Multiplatform library that makes it easier to build navigation
flows with AndroidX Navigation 3 and Koin.

## Features

- Register navigation destinations in Koin, similar to Koin's navigation3 integration
- Register deep links in a flexible way in Koin
- Automatically generated serializers module for declaring `rememberNavBackStack`

## Usage

Add the dependency to your project:

```kotlin
dependencies {
    implementation("com.mayakapps:nav3-companion:0.1.0")
}
```

Then, in your Koin module, register your navigation destinations and deep links:

```kotlin
val appModule = module {
    // `Navigator` is app-specific and should be implemented by the app.
    // `nav3cDeepLinkHandler` is a factory function that provides a `DeepLinkHandler` with all registered deep links.
    // Check out the sample app for an example of a simple `Navigator` implementation.
    single { Navigator(deepLinkHandler = nav3cDeepLinkHandler()) }
}

val featureModule = module {
    nav3cDeepLink<ProfileRoute>("nav3companion://profile/{userId}") { _ ->
        // Return `false` if the deep link was not handled to allow other deep links to be tried.
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
        val navigator = get<Navigator>()
        ProfileScreen(
            userId = route.userId,
            onBack = { navigator.pop() },
        )
    }
}
```

Delegate deep link handling to your `Navigator`, which can delegate to the `DeepLinkHandler`
provided by `nav3cDeepLinkHandler()`:

```kotlin
deepLinkHandler.handle(uriString)
```

Finally, set up your `NavDisplay` and `NavBackStack`:

```kotlin
@Composable
fun App() {
    val navigator = koinInject<Navigator>()
    val backStack = rememberNavBackStack(
        configuration = Nav3cSavedStateConfiguration(),
        HomeRoute,
    )

    // `remember` is used instead of `LaunchedEffect` to apply queued deep links before the first composition,
    remember(navigator, backStack) {
        navigator.initializeBackStack(backStack)
    }

    NavDisplay(
        backStack = backStack,
        onBack = { navigator.pop() },
        entryProvider = nav3cEntryProvider(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
    )
}
```

## License

Navigation3 Companion is licensed under Apache License 2.0. See [LICENSE](LICENSE) for details.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request with any improvements or
bug fixes. Make sure to follow the existing code style and include tests for any new functionality.
