package com.mayakapps.nav3companion.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.core.util.Consumer
import com.mayakapps.nav3sample.app.App
import com.mayakapps.nav3sample.navigation.Navigator
import org.koin.android.ext.android.inject
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val navigator: Navigator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        handleDeepLink(intent)

        setContent {
            DisposableEffect(Unit) {
                val consumer = Consumer<Intent> { handleDeepLink(it) }
                this@MainActivity.addOnNewIntentListener(consumer)

                onDispose {
                    this@MainActivity.removeOnNewIntentListener(consumer)
                }
            }

            App()
        }
    }

    private fun handleDeepLink(intent: Intent): Boolean {
        val uri = intent.data?.toString() ?: return false

        return navigator.handleDeepLink(uri)
    }
}
