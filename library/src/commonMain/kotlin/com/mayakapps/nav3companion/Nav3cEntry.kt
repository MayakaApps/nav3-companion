package com.mayakapps.nav3companion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.savedstate.serialization.SavedStateConfiguration
import androidx.savedstate.serialization.SavedStateConfiguration.Builder
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import org.koin.compose.currentKoinScope
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL

/**
 * Composable function that retrieves an entry provider from the current or specified Koin scope.
 *
 * This function collects all registered [Nav3cEntry] instances from the Koin scope and aggregates
 * them into a single entry provider that can be used with Navigation 3. By default, it uses the
 * scope from [currentKoinScope], but a custom scope can be provided.
 *
 * Example usage:
 * ```kotlin
 * @Composable
 * fun MyApp() {
 *     val entryProvider = koinEntryProvider()
 *     NavDisplay(
 *         backStack = navBackStack,
 *         entryProvider = koinEntryProvider(),
 *     )
 * }
 * ```
 *
 * @return An entry provider that combines all registered navigation entries from the scope.
 */
@Composable
fun <T : Any> nav3cEntryProvider(scope: Scope = currentKoinScope()): (T) -> NavEntry<T> {
    return remember(scope) {
        entryProvider {
            scope.getAll<Nav3cEntry<T>>().forEach { it.entryInstaller(this) }
        }
    }
}

/**
 * Creates a [SavedStateConfiguration] that includes serializers for all navigation entries
 * registered by [nav3cEntry] to be used with Navigation 3's `rememberNavBackStack` function.
 */
@Composable
fun nav3cSavedStateConfiguration(
    from: SavedStateConfiguration = SavedStateConfiguration.DEFAULT,
    builderAction: Builder.() -> Unit = {},
): SavedStateConfiguration {
    val serializersModule = nav3cSerializersModule<NavKey>()
    return SavedStateConfiguration(from) {
        this.serializersModule = serializersModule
        builderAction()
    }
}

/**
 * Creates a [SerializersModule] that includes polymorphic serializers for all navigation entries
 * registered by [nav3cEntry].
 */
@Composable
inline fun <reified T : Any> nav3cSerializersModule(scope: Scope = currentKoinScope()): SerializersModule {
    return remember(scope) {
        SerializersModule {
            polymorphic(T::class) {
                scope.getAll<Nav3cEntry<T>>().forEach { it.serializerInstaller(this) }
            }
        }
    }
}

/**
 * Declares a scoped navigation entry within a Koin scope DSL.
 *
 * This function registers a composable navigation destination that is scoped to a specific Koin
 * scope, allowing access to scoped dependencies within the composable. The route type [KeyT] is
 * used as both the navigation destination identifier and a qualifier for the entry provider.
 *
 * Example usage:
 * ```kotlin
 * activityScope {
 *     viewModel { MyViewModel() }
 *     navigation<MyRoute> { route ->
 *         MyScreen(viewModel = koinViewModel())
 *     }
 * }
 * ```
 *
 * @param KeyT The type representing the navigation route/destination.
 * @param metadata Optional metadata map to associate with the navigation entry (default is empty).
 * @param definition A composable function that receives the [Scope] and route instance [KeyT] to
 *    render the destination.
 * @return A [KoinDefinition] for the created [Nav3cEntry].
 * @see Module.nav3cEntry for module-level navigation entries.
 */
@KoinDslMarker
inline fun <reified KeyT : Any> ScopeDSL.nav3cEntry(
    metadata: Map<String, Any> = emptyMap(),
    noinline definition: @Composable Scope.(key: KeyT) -> Unit,
): KoinDefinition<Nav3cEntry<KeyT>> = scoped(qualifier = named<KeyT>()) {
    Nav3cEntry(
        entryInstaller = {
            entry<KeyT>(content = { key -> definition(key) }, metadata = metadata)
        },
        serializerInstaller = {
            @OptIn(InternalSerializationApi::class)
            subclass(KeyT::class, KeyT::class.serializer())
        },
    )
}

/**
 * Declares a singleton navigation entry within a Koin module.
 *
 * This function registers a composable navigation destination as a singleton in the Koin module,
 * allowing access to module-level dependencies within the composable. The route type [KeyT] is
 * used as both the navigation destination identifier and a qualifier for the entry provider.
 *
 * Example usage:
 * ```kotlin
 * module {
 *     viewModel { MyViewModel() }
 *     navigation<HomeRoute> { route ->
 *         HomeScreen(myViewModel = koinViewModel())
 *     }
 * }
 * ```
 *
 * @param KeyT The type representing the navigation route/destination.
 * @param metadata Optional metadata map to associate with the navigation entry (default is empty).
 * @param definition A composable function that receives the [Scope] and route instance [KeyT] to
 *    render the destination.
 * @return A [KoinDefinition] for the created [Nav3cEntry].
 * @see ScopeDSL.nav3cEntry for scope-level navigation entries.
 */
@KoinDslMarker
inline fun <reified KeyT : Any> Module.nav3cEntry(
    metadata: Map<String, Any> = emptyMap(),
    noinline definition: @Composable Scope.(key: KeyT) -> Unit,
): KoinDefinition<Nav3cEntry<KeyT>> = single(qualifier = named<KeyT>()) {
    Nav3cEntry(
        entryInstaller = {
            entry<KeyT>(content = { key -> definition(key) }, metadata = metadata)
        },
        serializerInstaller = {
            @OptIn(InternalSerializationApi::class)
            subclass(KeyT::class, KeyT::class.serializer())
        },
    )
}

data class Nav3cEntry<T : Any>(
    val entryInstaller: EntryProviderScope<T>.() -> Unit,
    val serializerInstaller: PolymorphicModuleBuilder<T>.() -> Unit,
)
