package com.mocoding.pokedex.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.mocoding.pokedex.ui.root.RootComponent
import com.mocoding.pokedex.ui.root.RootContent
import com.mocoding.pokedex.ui.theme.PokedexTheme
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.component.setupDefaultComponents
import com.seiko.imageloader.option.androidContext

@Composable
fun ContentView(component: RootComponent) {
    val context = LocalContext.current
    CompositionLocalProvider(
        LocalImageLoader provides remember(context) { generateImageLoader(context) }
    ) {
        PokedexTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                RootContent(component)
            }
        }
    }
}

private fun generateImageLoader(context: Context): ImageLoader {
    return ImageLoader {
        options {
            androidContext(context)
        }
        components {
            setupDefaultComponents()
        }
    }
}
