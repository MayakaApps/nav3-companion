package com.mayakapps.nav3sample.feature.home.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onProfileClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Home")
        Button(onClick = onSearchClick) { Text("Search") }
        Button(onClick = { onProfileClick("123") }) { Text("Open Profile") }
        Button(onClick = onSettingsClick) { Text("Settings") }
    }
}
