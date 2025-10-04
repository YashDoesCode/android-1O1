package com.YashDoesCode.gallery.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.YashDoesCode.gallery.domain.model.GalleryPermissionState
import com.YashDoesCode.gallery.domain.model.MediaImage
import com.YashDoesCode.gallery.ui.components.AlbumsGridScreen
import com.YashDoesCode.gallery.ui.components.ImageViewerScreen
import com.YashDoesCode.gallery.ui.components.PermissionView
import com.YashDoesCode.gallery.ui.components.PhotoGridScreen

@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val selectedImage by viewModel.selectedImage.collectAsState()
    val isDetailsOpen by viewModel.isDetailsOpen.collectAsState()

    val requiredPermissions = remember {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
            else -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results.values.any { it }
        val partial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            results[Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED] == true &&
                    results[Manifest.permission.READ_MEDIA_IMAGES] != true
        } else {
            false
        }

        val state = when {
            partial -> GalleryPermissionState.Partial
            granted -> GalleryPermissionState.Granted
            else -> GalleryPermissionState.Denied
        }
        viewModel.onAction(GalleryAction.UpdatePermission(state))
    }

    LaunchedEffect(Unit) {
        val hasPermission = requiredPermissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (hasPermission) {
            viewModel.onAction(GalleryAction.UpdatePermission(GalleryPermissionState.Granted))
        } else {
            viewModel.onAction(GalleryAction.UpdatePermission(GalleryPermissionState.Denied))
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Fullscreen photo viewer overlay
        if (selectedImage != null && uiState is GalleryUiState.PhotosContent) {
            val photosState = uiState as GalleryUiState.PhotosContent
            val initialIndex = remember(selectedImage) {
                photosState.images.indexOfFirst { it.id == selectedImage?.id }.coerceAtLeast(0)
            }
            ImageViewerScreen(
                images = photosState.images,
                initialIndex = initialIndex,
                isDetailsOpen = isDetailsOpen,
                onBackClick = { viewModel.onAction(GalleryAction.NavigateBack) },
                onOpenDetails = { viewModel.onAction(GalleryAction.OpenDetails(it)) },
                onCloseDetails = { viewModel.onAction(GalleryAction.CloseDetails) }
            )
        } else {
            AnimatedContent(
                targetState = uiState,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "GalleryUiStateTransition"
            ) { state ->
                when (state) {
                    GalleryUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    is GalleryUiState.PermissionRequired -> {
                        PermissionView(
                            state = state.state,
                            onRequestPermission = {
                                permissionLauncher.launch(requiredPermissions)
                            }
                        )
                    }

                    is GalleryUiState.Empty -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    is GalleryUiState.AlbumsContent -> {
                        AlbumsGridScreen(
                            albums = state.albums,
                            onSelectAlbum = { album ->
                                viewModel.onAction(GalleryAction.SelectAlbum(album))
                            }
                        )
                    }

                    is GalleryUiState.PhotosContent -> {
                        PhotoGridScreen(
                            albumName = state.albumName,
                            images = state.images,
                            onBackClick = { viewModel.onAction(GalleryAction.NavigateBack) },
                            onSelectImage = { image, index ->
                                viewModel.onAction(GalleryAction.SelectImage(image, index))
                            }
                        )
                    }

                    is GalleryUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = { viewModel.onAction(GalleryAction.Refresh) }) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
