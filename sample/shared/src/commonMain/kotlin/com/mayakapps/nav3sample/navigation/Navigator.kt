package com.mayakapps.nav3sample.navigation

import androidx.navigation3.runtime.NavKey
import com.mayakapps.nav3companion.Nav3cDeepLinkHandler

class Navigator internal constructor(
    private val deepLinkHandler: Nav3cDeepLinkHandler<Any>,
) {

    private val queuedCommands = mutableListOf<NavigatorCommand>()

    private var _backStack: MutableList<NavKey>? = null

    /**
     * Initializes the navigation stack for this navigator.
     *
     * All queued commands will be executed in the order they were added.
     */
    fun initializeBackStack(backStack: MutableList<NavKey>) {
        while (queuedCommands.isNotEmpty()) {
            when (val command = queuedCommands.removeFirst()) {
                is NavigatorCommand.Push -> backStack.push(command.screen)
                is NavigatorCommand.Pop -> backStack.pop()
            }
        }

        this._backStack = backStack
    }

    /**
     * Handles a deep link URI string via the provided [deepLinkHandler].
     *
     * Returns true if the URI was successfully handled, false otherwise.
     */
    fun handleDeepLink(uriString: String): Boolean {
        return deepLinkHandler.handle(uriString)
    }

    /** Adds a screen to the top of the stack. */
    fun push(screen: NavKey) {
        _backStack?.push(screen)
            ?: queuedCommands.add(NavigatorCommand.Push(screen))
    }

    private fun MutableList<NavKey>.push(screen: NavKey) {
        add(screen)
    }

    /**
     * Removes the top screen from the stack.
     *
     * @return true if a screen was removed, false if the stack would become empty, and `null` if
     *    the stack is not set yet.
     */
    fun pop(): Boolean? {
        _backStack?.let { return it.pop() }
        queuedCommands.add(NavigatorCommand.Pop)
        return null
    }

    private fun MutableList<NavKey>.pop(): Boolean {
        return removeLastOrNull() != null
    }

    fun setBackStack(backStack: List<NavKey>) {
        _backStack?.setBackStack(backStack)
            ?: queuedCommands.add(NavigatorCommand.SetBackStack(backStack))
    }

    private fun MutableList<NavKey>.setBackStack(backStack: List<NavKey>) {
        addAll(backStack)
        removeRange(0, size - backStack.size)
    }

    /**
     * Removes the portion of the list between the specified [fromIndex] (inclusive) and [toIndex]
     * (exclusive).
     */
    private fun MutableList<*>.removeRange(fromIndex: Int, toIndex: Int) {
        subList(fromIndex, toIndex).clear()
    }

    private interface NavigatorCommand {
        data class Push(val screen: NavKey) : NavigatorCommand
        data object Pop : NavigatorCommand
        data class SetBackStack(val backStack: List<NavKey>) : NavigatorCommand
    }
}
