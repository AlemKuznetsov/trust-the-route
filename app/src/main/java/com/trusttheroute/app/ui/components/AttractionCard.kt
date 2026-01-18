package com.trusttheroute.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.trusttheroute.app.domain.model.Attraction
import com.trusttheroute.app.ui.theme.*

@Composable
fun AttractionCard(
    attraction: Attraction,
    isVisible: Boolean,
    isAudioPlaying: Boolean,
    onDismiss: () -> Unit,
    onPlayAudio: () -> Unit,
    onPauseAudio: () -> Unit,
    onRestartAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Заголовок с названием
                    Text(
                        text = attraction.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = LightOnSurface,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 16.dp, end = 56.dp, bottom = 16.dp)
                    )

                    // Прокручиваемая область с изображениями и описанием
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        // Галерея изображений достопримечательности
                        val imageUrls = buildList {
                            // Добавляем локальные изображения
                            attraction.localImagePaths.forEach { path ->
                                add("file:///android_asset/images/$path")
                            }
                            // Добавляем URL изображения
                            attraction.imageUrls.forEach { url ->
                                if (url !in this) add(url)
                            }
                        }
                        
                        if (imageUrls.isNotEmpty()) {
                            if (imageUrls.size == 1) {
                                // Одно изображение - показываем полноразмерным
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(LightSurfaceVariant)
                                ) {
                                    AsyncImage(
                                        model = imageUrls[0],
                                        contentDescription = attraction.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            } else {
                                // Несколько изображений - показываем галерею с горизонтальным скроллом
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    contentPadding = PaddingValues(horizontal = 0.dp)
                                ) {
                                    items(imageUrls) { imageUrl ->
                                        Box(
                                            modifier = Modifier
                                                .width(280.dp)
                                                .height(200.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(LightSurfaceVariant)
                                        ) {
                                            AsyncImage(
                                                model = imageUrl,
                                                contentDescription = "${attraction.name} - изображение",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Описание с прокруткой
                        Text(
                            text = attraction.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LightOnSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Зафиксированный аудиоплеер внизу
                    if (attraction.audioUrl.isNotEmpty() || attraction.localAudioPath != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = White,
                            shadowElevation = 4.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Кнопка Play/Pause
                                Button(
                                    onClick = {
                                        if (isAudioPlaying) {
                                            onPauseAudio()
                                        } else {
                                            onPlayAudio()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BluePrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isAudioPlaying) {
                                            Icons.Default.Pause
                                        } else {
                                            Icons.Default.PlayArrow
                                        },
                                        contentDescription = if (isAudioPlaying) "Пауза" else "Воспроизвести",
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isAudioPlaying) "Пауза" else "Воспроизвести",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                                
                                // Кнопка перезапуска
                                OutlinedButton(
                                    onClick = onRestartAudio,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.width(56.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Replay,
                                        contentDescription = "С начала",
                                        modifier = Modifier.size(20.dp),
                                        tint = BluePrimary
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Кнопка закрытия в правом верхнем углу
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = LightOnSurfaceVariant
                    )
                }
            }
        }
    }
}
