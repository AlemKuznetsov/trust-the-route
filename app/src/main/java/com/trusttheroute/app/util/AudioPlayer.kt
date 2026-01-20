package com.trusttheroute.app.util

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    
    // Используем DefaultDataSource.Factory, который автоматически обрабатывает asset:// URI
    private val dataSourceFactory: DefaultDataSource.Factory = DefaultDataSource.Factory(context)

    val isPlaying: Boolean
        get() = player.isPlaying

    val currentPosition: Long
        get() = player.currentPosition

    val duration: Long
        get() = player.duration

    fun play(url: String, onCompletion: (() -> Unit)? = null) {
        try {
            android.util.Log.d("AudioPlayer", "Playing audio: $url")
            
            // Останавливаем текущее воспроизведение перед загрузкой нового
            if (player.isPlaying) {
                player.stop()
            }
            player.clearMediaItems()
            
            // Формируем правильный URI для файлов из assets
            val uri = when {
                url.startsWith("file:///android_asset/") -> {
                    // Преобразуем file:///android_asset/path в asset:///path
                    val assetPath = url.removePrefix("file:///android_asset/")
                    android.util.Log.d("AudioPlayer", "Asset path: $assetPath")
                    Uri.parse("asset:///$assetPath")
                }
                !url.contains("://") && url.contains(".") -> {
                    // Если это просто имя файла (например, "attraction_1.mp3")
                    android.util.Log.d("AudioPlayer", "Treating as asset filename: $url")
                    Uri.parse("asset:///audio/$url")
                }
                else -> {
                    // Обычный URL (http/https) или другой формат
                    Uri.parse(url)
                }
            }
            
            android.util.Log.d("AudioPlayer", "Final URI: $uri")
            
            val mediaSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
            
            android.util.Log.d("AudioPlayer", "MediaSource created, preparing...")
            
            // Создаем listener для отслеживания готовности плеера
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    android.util.Log.d("AudioPlayer", "Playback state: $playbackState")
                    when (playbackState) {
                        Player.STATE_READY -> {
                            android.util.Log.d("AudioPlayer", "Player ready, duration: ${player.duration}ms")
                            // Убеждаемся, что воспроизведение началось
                            if (!player.isPlaying) {
                                android.util.Log.d("AudioPlayer", "Starting playback after ready state")
                                player.play()
                            }
                        }
                        Player.STATE_ENDED -> {
                            android.util.Log.d("AudioPlayer", "Playback ended")
                            onCompletion?.invoke()
                            player.removeListener(this)
                        }
                        Player.STATE_BUFFERING -> {
                            android.util.Log.d("AudioPlayer", "Buffering...")
                        }
                        Player.STATE_IDLE -> {
                            android.util.Log.d("AudioPlayer", "Player idle")
                        }
                    }
                }
                
                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.e("AudioPlayer", "Playback error: ${error.message}", error)
                    android.util.Log.e("AudioPlayer", "Error type: ${error.errorCode}", error)
                    player.removeListener(this)
                }
            }
            
            // Добавляем listener перед установкой источника
            player.addListener(listener)
            
            // Устанавливаем источник и готовим плеер
            player.setMediaSource(mediaSource)
            player.prepare()
            
            // Пытаемся начать воспроизведение сразу
            // Если плеер еще не готов, он начнет воспроизведение автоматически в STATE_READY
            player.play()
            
        } catch (e: Exception) {
            android.util.Log.e("AudioPlayer", "Error playing audio: $url", e)
            e.printStackTrace()
        }
    }

    fun pause() {
        if (player.isPlaying) {
            player.pause()
        }
    }
    
    fun resume() {
        // Возобновляем воспроизведение, если плеер готов (STATE_READY означает готовность к воспроизведению)
        if (!player.isPlaying && player.playbackState == Player.STATE_READY) {
            android.util.Log.d("AudioPlayer", "Resuming playback")
            player.play()
        } else {
            android.util.Log.d("AudioPlayer", "Cannot resume: isPlaying=${player.isPlaying}, state=${player.playbackState}")
        }
    }

    fun stop() {
        player.stop()
        player.clearMediaItems()
    }

    fun release() {
        player.release()
    }
}
