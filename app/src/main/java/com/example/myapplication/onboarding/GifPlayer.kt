package com.example.myapplication.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.compose.AsyncImage
import android.os.Build

@Composable
fun GifPlayer(
    modifier: Modifier = Modifier,
    resId: Int
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()

    val request = ImageRequest.Builder(context)
        .data(resId)
        .crossfade(true)
        .build()

    AsyncImage(
        model = request,
        contentDescription = null,
        modifier = modifier,
        imageLoader = imageLoader
    )
}
