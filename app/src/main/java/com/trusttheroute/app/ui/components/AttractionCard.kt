package com.trusttheroute.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.trusttheroute.app.domain.model.Attraction
import com.trusttheroute.app.ui.theme.*
import com.trusttheroute.app.util.StorageUrlHelper

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
    val isDarkTheme = isDarkTheme()
    val cardColor = if (isDarkTheme) DarkSurface else White
    val borderColor = if (isDarkTheme) DarkBorder else Color.Transparent
    
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(),
        exit = shrinkVertically(),
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .then(
                    if (isDarkTheme) {
                        Modifier
                            .border(
                                width = 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .shadow(16.dp, RoundedCornerShape(24.dp))
                    } else {
                        Modifier
                    }
                ),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            ),
            elevation = if (!isDarkTheme) CardDefaults.cardElevation(defaultElevation = 8.dp) else CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                        color = if (isDarkTheme) Blue400 else LightOnSurface,
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
                        // Используем StorageUrlHelper для получения приоритетных URL
                        val imageUrls = StorageUrlHelper.getPrioritizedImageUrls(
                            cloudUrls = attraction.imageUrls,
                            localPaths = attraction.localImagePaths,
                            routeId = attraction.routeId
                        )
                        
                        if (imageUrls.isNotEmpty()) {
                            if (imageUrls.size == 1) {
                                // Одно изображение - показываем полноразмерным
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDarkTheme) DarkSurfaceVariant else LightSurfaceVariant)
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
                                                .background(if (isDarkTheme) DarkSurfaceVariant else LightSurfaceVariant)
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
                            color = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Зафиксированный аудиоплеер внизу
                    // Используем приоритетный URL: сначала облачный, потом локальный
                    val audioUrl = StorageUrlHelper.getPrioritizedAudioUrl(
                        cloudUrl = attraction.audioUrl.takeIf { it.isNotEmpty() },
                        localPath = attraction.localAudioPath,
                        routeId = attraction.routeId
                    )
                    
                    if (audioUrl.isNotEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(
                                    if (isDarkTheme) {
                                        Modifier
                                            .border(
                                                width = 1.dp,
                                                color = DarkBorderHover,
                                                shape = RoundedCornerShape(16.dp)
                                            )
                                    } else {
                                        Modifier
                                    }
                                ),
                            color = if (isDarkTheme) {
                                // Градиент для темной темы
                                Color.Transparent
                            } else {
                                White
                            },
                            shadowElevation = if (!isDarkTheme) 4.dp else 0.dp
                        ) {
                            Box(
                                modifier = if (isDarkTheme) {
                                    Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.horizontalGradient(
                                                colors = listOf(DarkSurfaceVariant, DarkSurfaceHover)
                                            )
                                        )
                                } else {
                                    Modifier.fillMaxWidth()
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Метка "Аудиогид"
                                    Text(
                                        text = "Аудиогид",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isDarkTheme) DarkOnSurfacePlaceholder else Color.Unspecified,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                    
                                    // Кнопка Play/Pause
                                    if (isDarkTheme) {
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    Brush.horizontalGradient(
                                                        colors = listOf(Blue600, Blue700)
                                                    )
                                                )
                                                .clickable {
                                                    if (isAudioPlaying) {
                                                        onPauseAudio()
                                                    } else {
                                                        onPlayAudio()
                                                    }
                                                }
                                                .padding(horizontal = 12.dp, vertical = 12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (isAudioPlaying) {
                                                        Icons.Default.Pause
                                                    } else {
                                                        Icons.Default.PlayArrow
                                                    },
                                                    contentDescription = if (isAudioPlaying) "Пауза" else "Воспроизвести",
                                                    modifier = Modifier.size(18.dp),
                                                    tint = White
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isAudioPlaying) "Пауза" else "Воспроизвести",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = White,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    } else {
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
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
                                        ) {
                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (isAudioPlaying) {
                                                        Icons.Default.Pause
                                                    } else {
                                                        Icons.Default.PlayArrow
                                                    },
                                                    contentDescription = if (isAudioPlaying) "Пауза" else "Воспроизвести",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isAudioPlaying) "Пауза" else "Воспроизвести",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    maxLines = 1,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    }
                                    
                                    // Кнопка перезапуска
                                    OutlinedButton(
                                        onClick = onRestartAudio,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.width(56.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = if (isDarkTheme) Blue400 else BluePrimary
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isDarkTheme) DarkBorderHover else BluePrimary
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Replay,
                                            contentDescription = "С начала",
                                            modifier = Modifier.size(20.dp),
                                            tint = if (isDarkTheme) Blue400 else BluePrimary
                                        )
                                    }
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
                        .then(
                            if (isDarkTheme) {
                                Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface.copy(alpha = 0.9f))
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Закрыть",
                        tint = if (isDarkTheme) DarkOnSurfaceSecondary else LightOnSurfaceVariant
                    )
                }
            }
        }
    }
}
