package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ToastType {
    SUCCESS,
    INFO,
    WARNING,
    ERROR,
    DOWNLOAD
}

data class ToastData(
    val message: String,
    val type: ToastType = ToastType.INFO,
    val durationMillis: Long = 2600L,
    val id: Long = System.currentTimeMillis()
)

object AppToastManager {
    private val _currentToast = MutableStateFlow<ToastData?>(null)
    val currentToast = _currentToast.asStateFlow()

    private var dismissJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun show(message: String, type: ToastType = ToastType.INFO, durationMillis: Long = 2600L) {
        dismissJob?.cancel()
        _currentToast.value = ToastData(message, type, durationMillis, System.currentTimeMillis())
        dismissJob = scope.launch {
            delay(durationMillis)
            _currentToast.value = null
        }
    }

    fun dismiss() {
        dismissJob?.cancel()
        _currentToast.value = null
    }
}

@Composable
fun CustomToastHost(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = isSystemInDarkTheme()
) {
    val toastData by AppToastManager.currentToast.collectAsState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .zIndex(9999f),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = toastData != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            toastData?.let { data ->
                val (icon: ImageVector, accentColor: Color) = when (data.type) {
                    ToastType.SUCCESS -> Icons.Default.CheckCircle to Color(0xFF10B981)
                    ToastType.DOWNLOAD -> Icons.Default.Download to MaterialTheme.colorScheme.primary
                    ToastType.WARNING -> Icons.Default.WarningAmber to Color(0xFFF59E0B)
                    ToastType.ERROR -> Icons.Default.ErrorOutline to Color(0xFFEF4444)
                    ToastType.INFO -> Icons.Default.Info to MaterialTheme.colorScheme.primary
                }

                val bgColor = if (isDarkTheme) Color(0xFF0F172A).copy(alpha = 0.95f) else Color.White.copy(alpha = 0.96f)
                val borderColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0)
                val textColor = if (isDarkTheme) Color.White else Color(0xFF0F172A)

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = bgColor,
                    border = BorderStroke(1.dp, borderColor),
                    shadowElevation = 8.dp,
                    modifier = Modifier.shadow(8.dp, RoundedCornerShape(24.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(accentColor.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Text(
                            text = data.message,
                            color = textColor,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
