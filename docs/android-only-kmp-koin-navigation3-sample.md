# Android+iOS KMP sample: `nav3-companion` + Koin + Navigation 3

This sample shows how to use `nav3-companion` in a Kotlin Multiplatform app that targets **Android and iOS**.

For readability, the code blocks below omit import lists and focus on the module boundaries, route types, and Koin/Navigation 3 wiring.

It demonstrates:

- 4 screens: `Home`, `Search`, `Profile`, and `Settings`
- feature-local Koin modules, as if each feature lived in its own Gradle module
- Navigation 3 with `NavDisplay`
- deep-link declarations on a few destinations
- a small app-level Koin module that wires everything together
- a shared `initKoin()` bootstrap that both Android and iOS call

## Suggested module layout

```text
:shared
  └─ commonMain
     ├─ navigation/Routes.kt
     ├─ navigation/Navigator.kt
     ├─ koin/Koin.kt
     └─ app/App.kt

:feature-home
  └─ commonMain
     └─ HomeModule.kt

:feature-search
  └─ commonMain
     └─ SearchModule.kt

:feature-profile
  └─ commonMain
     └─ ProfileModule.kt

:feature-settings
  └─ commonMain
     └─ SettingsModule.kt

:androidApp
  ├─ Application.kt
  ├─ MainActivity.kt
  └─ AndroidManifest.xml

:iosApp
  └─ App.swift
```

## Shared navigation contract

`Routes.kt`

```kotlin
@Serializable
data object HomeRoute : NavKey

@Serializable
data class SearchRoute(
    val query: String = "",
) : NavKey

@Serializable
data class ProfileRoute(
    val userId: String,
) : NavKey

@Serializable
data class SettingsRoute(
    val section: String = "general",
) : NavKey
```

`Navigator.kt`

```kotlin
class Navigator {

    private lateinit var backStack: SnapshotStateList<NavKey>

    fun setBackStack(backStack: SnapshotStateList<NavKey>) {
        this.backStack = backStack
    }

    fun goTo(route: NavKey) {
        requireBackStack()
        backStack.add(route)
    }

    fun goBack() {
        requireBackStack()
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }

    private fun requireBackStack() {
        check(::backStack.isInitialized) { "Navigator backStack not initialized" }
    }
}
```

## Feature modules

Each feature owns its own Koin module. In a real app, these files could live in separate Gradle modules.

### Home feature

`HomeModule.kt`

```kotlin
val homeModule = module {
    navigation<HomeRoute> {
        val navigator = get<Navigator>()

        HomeScreen(
            onSearchClick = { navigator.goTo(SearchRoute(query = "compose")) },
            onProfileClick = { userId -> navigator.goTo(ProfileRoute(userId)) },
            onSettingsClick = { navigator.goTo(SettingsRoute()) },
        )
    }
}
```

### Search feature

`SearchModule.kt`

```kotlin

val searchModule = module {
    navDeepLink<SearchRoute>("nav3companion://search?query={query}") { route ->
        get<Navigator>().goTo(route)
        true
    }

    navigation<SearchRoute> { route ->
        val navigator = get<Navigator>()

        SearchScreen(
            initialQuery = route.query,
            onOpenProfile = { userId -> navigator.goTo(ProfileRoute(userId)) },
            onBack = { navigator.goBack() },
        )
    }
}
```

### Profile feature

`ProfileModule.kt`

```kotlin
val profileModule = module {
    navDeepLink<ProfileRoute>("nav3companion://profile/{userId}") { route ->
        // This block didn't handle the deep-link, so return false to let other navDeepLink handlers
        // try to handle it.
        false
    }

    navDeepLink<ProfileRoute>("nav3companion://profile/{userId}") { route ->
        get<Navigator>().goTo(route)
        true
    }

    navigation<ProfileRoute> { route ->
        ProfileScreen(
            userId = route.userId,
            onBack = { get<Navigator>().goBack() },
        )
    }
}
```

### Settings feature

`SettingsModule.kt`

```kotlin
val settingsModule = module {
    navDeepLink<SettingsRoute>("nav3companion://settings/{section}") { route ->
        get<Navigator>().goTo(route)
        true
    }

    navigation<SettingsRoute> { route ->
        SettingsScreen(
            section = route.section,
            onBack = { get<Navigator>().goBack() },
        )
    }
}
```

## App-level module

`AppModule.kt`

```kotlin
val appModule = module {
    singleOf<Navigator>()
}
```

## Shared Koin bootstrap

`Koin.kt`

```kotlin
fun initKoin() = initKoin {}

fun initKoin(appDeclaration: KoinAppDeclaration): KoinApplication {
    return startKoin {
        appDeclaration()
        modules(
            appModule,
            homeModule,
            searchModule,
            profileModule,
            settingsModule,
        )
    }
}
```

## Shared Compose host

`App.kt`

```kotlin
@Composable
fun App() {
    val navigator = koinInject<Navigator>()
    val nav3SerializersModule = koinNav3SerializersModule()
    navigator.setBackStack(
        rememberNavBackStack(
            config = SavedStateConfiguration {
                serializersModule = nav3SerializersModule
            },
            HomeRoute,
        ),
    )

    NavDisplay(
        backStack = backStack,
        onBack = { navigator.goBack() },
        entryProvider = koinEntryProvider(),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
    )
}
```

## Platform entry points

`Application.kt`

```kotlin
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@SampleApplication)
        }
    }
}
```

`MainActivity.kt`

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}
```

`App.swift`

```swift
@main
struct SampleApp: App {
    init() {
        KoinKt.initKoin()
    }

    var body: some Scene {
        WindowGroup {
            ComposeView()
        }
    }
}
```

## Deep-link manifest entries

Declare intent filters for the screens that should be reachable from outside the app on Android. iOS does not use this manifest-based deep-link setup, but it reuses the same shared navigation and Koin graph.

`AndroidManifest.xml`

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="nav3companion" android:host="search" />
</intent-filter>

<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="nav3companion" android:host="profile" />
</intent-filter>

<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="nav3companion" android:host="settings" />
</intent-filter>
```

Example URIs:

- `nav3companion://search?query=compose`
- `nav3companion://profile/123`
- `nav3companion://settings/privacy`

## Why this shape works well

- Each feature owns its own Koin module, so the dependency graph stays modular.
- Navigation 3 routes are plain `@Serializable` `NavKey` types, so they are easy to save and inspect.
- `nav3-companion` keeps the `navigation<T>` declarations next to the screen they render.
- Deep-link patterns stay close to the feature that owns the screen.
- The app-level module stays small: it only wires shared state and starts Koin.

If you want, I can turn this into a fully scaffolded example project layout next.
