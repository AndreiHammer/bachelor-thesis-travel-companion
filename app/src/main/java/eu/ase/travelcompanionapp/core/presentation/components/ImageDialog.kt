package eu.ase.travelcompanionapp.core.presentation.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import eu.ase.travelcompanionapp.R

@Composable
fun ImageDialog(
    imageCount: Int,
    currentImageIndex: Int,
    onDismiss: () -> Unit,
    onImageChange: (Int) -> Unit,
    imageContent: @Composable (Int) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.7f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                val pagerState = rememberPagerState(
                    initialPage = currentImageIndex,
                    pageCount = { imageCount }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            imageContent(page)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        NavigationButton(
                            onClick = {
                                if (pagerState.currentPage > 0) {
                                    onImageChange(pagerState.currentPage - 1)
                                }
                            },
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.previous_image),
                            enabled = pagerState.currentPage > 0
                        )

                        NavigationButton(
                            onClick = {
                                if (pagerState.currentPage < imageCount - 1) {
                                    onImageChange(pagerState.currentPage + 1)
                                }
                            },
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(R.string.next_image),
                            enabled = pagerState.currentPage < imageCount - 1
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .clip(RoundedCornerShape(20.dp)),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "${pagerState.currentPage + 1}/$imageCount",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                LaunchedEffect(currentImageIndex) {
                    pagerState.animateScrollToPage(currentImageIndex)
                }
            }
        }
    }
}

@Composable
fun BitmapImageDialog(
    photos: List<Bitmap>,
    currentImageIndex: Int,
    onDismiss: () -> Unit,
    onImageChange: (Int) -> Unit
) {
    ImageDialog(
        imageCount = photos.size,
        currentImageIndex = currentImageIndex,
        onDismiss = onDismiss,
        onImageChange = onImageChange
    ) { page ->
        Image(
            bitmap = photos[page].asImageBitmap(),
            contentDescription = stringResource(R.string.photo_number, page),
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun UrlImageDialog(
    photoUrls: List<String>,
    currentImageIndex: Int,
    onDismiss: () -> Unit,
    onImageChange: (Int) -> Unit
) {
    ImageDialog(
        imageCount = photoUrls.size,
        currentImageIndex = currentImageIndex,
        onDismiss = onDismiss,
        onImageChange = onImageChange
    ) { page ->
        AsyncImage(
            model = photoUrls[page],
            contentDescription = stringResource(R.string.photo_number, page),
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun NavigationButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    enabled: Boolean
) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(
                if (enabled) 
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                else 
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (enabled) 
                    MaterialTheme.colorScheme.onSurface 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
} 