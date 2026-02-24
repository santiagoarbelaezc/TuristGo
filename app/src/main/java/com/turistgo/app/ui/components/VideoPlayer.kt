package com.turistgo.app.ui.components

import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    repeatMode: Int = Player.REPEAT_MODE_ALL
) {
    val context = LocalContext.current
    
    // Usamos remember con la URL como llave para reiniciarlo si cambia
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            this.repeatMode = repeatMode
            this.playWhenReady = autoPlay
            prepare()
            play() // Forzamos el inicio de la reproducción
        }
    }

    DisposableEffect(videoUrl) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                // Aseguramos que el video llene el círculo
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                // Fondo transparente para evitar parpadeos negros
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
            }
        },
        update = { view ->
            view.player = exoPlayer
        },
        modifier = modifier
    )
}
