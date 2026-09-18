package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Memory
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import java.io.File
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SortOption
import com.example.data.model.ThemeMode
import com.example.ui.theme.AppThemeColor
import com.example.ui.theme.contrastingTextColor
import com.example.ui.theme.ensureReadableAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {},
    themeColor: AppThemeColor,
    onThemeColorChange: (AppThemeColor) -> Unit,
    defaultFilterType: String,
    onDefaultFilterTypeChange: (String) -> Unit,
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    onClearCache: () -> Unit,
    maxConcurrentDownloads: Int = 3,
    onMaxConcurrentDownloadsChange: (Int) -> Unit = {},
    catalogLayoutMode: String = "GRID_2",
    onCatalogLayoutModeChange: (String) -> Unit = {},
    downloadFolderName: String = "Películas (Por defecto)",
    downloadFolderPath: String = "",
    onDownloadFolderChange: (name: String, path: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val openFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
            } catch (_: Exception) {}

            val folderName = uri.lastPathSegment?.substringAfterLast(":") ?: "Carpeta personalizada"
            onDownloadFolderChange(folderName, uri.toString())
            Toast.makeText(context, "Carpeta seleccionada: $folderName", Toast.LENGTH_SHORT).show()
        }
    }

    var showColorPickerDialog by remember { mutableStateOf(false) }
    var showFolderDialog by remember { mutableStateOf(false) }

    var hardwareAcceleration by remember { mutableStateOf(true) }
    var screenGestures by remember { mutableStateOf(true) }
    var autoResume by remember { mutableStateOf(true) }
    var wifiOnlyDownloads by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }

    val screenBg = if (isDarkTheme) Color(0xFF070B18) else Color(0xFFF1F5F9)
    val cardBg = if (isDarkTheme) Color(0xFF0D1424) else Color.White
    val cardBorder = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFA0AEC0)
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF334155)
    val itemBg = if (isDarkTheme) Color(0xFF131C30) else Color(0xFFF1F5F9)
    val dividerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFCBD5E1)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = screenBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.2f else 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Ajustes",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif,
                            fontSize = 20.sp,
                            color = textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = screenBg
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Ordenación del Catálogo
            SettingsCategoryHeader(title = "ORDENACIÓN POR DEFECTO", icon = Icons.Default.Sort)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Selecciona el orden inicial de los títulos",
                        fontSize = 13.sp,
                        color = textSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    SortOption.entries.forEach { option ->
                        val isSelected = sortOption == option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSortOptionChange(option) }
                                .padding(vertical = 6.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSortOptionChange(option) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = textSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = option.displayName + if (option == SortOption.NAME_AZ) " (Por defecto)" else "",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) textPrimary else textSecondary
                            )
                        }
                    }
                }
            }

            // Section: Diseño y Vista del Catálogo
            SettingsCategoryHeader(title = "DISEÑO DEL CATÁLOGO", icon = Icons.Default.GridView)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Elige cómo deseas visualizar los contenidos en la pantalla principal",
                        fontSize = 13.sp,
                        color = textSecondary
                    )

                    val layoutOptions = listOf(
                        Triple("GRID_2", "Cuadrícula estándar (2 columnas)", Icons.Default.GridView),
                        Triple("GRID_3", "Cuadrícula compacta (3 columnas)", Icons.Default.ViewModule),
                        Triple("LIST", "Lista detallada (1 columna)", Icons.Default.ViewAgenda)
                    )

                    layoutOptions.forEach { (mode, label, icon) ->
                        val isSelected = catalogLayoutMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.18f else 0.12f)
                                    else itemBg
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { onCatalogLayoutModeChange(mode) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = label,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) textPrimary else textSecondary
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = { onCatalogLayoutModeChange(mode) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = textSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Section 2: Modo Visual (Sistema / Oscuro / Claro)
            SettingsCategoryHeader(title = "MODO DE APARIENCIA", icon = Icons.Default.BrightnessAuto)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Selecciona el modo visual preferido para la aplicación",
                        fontSize = 13.sp,
                        color = textSecondary
                    )

                    val modes = listOf(
                        Triple(ThemeMode.SYSTEM, "Del Sistema (Automático)", Icons.Default.BrightnessAuto),
                        Triple(ThemeMode.DARK, "Modo Oscuro", Icons.Default.DarkMode),
                        Triple(ThemeMode.LIGHT, "Modo Claro", Icons.Default.LightMode)
                    )

                    modes.forEach { (mode, label, icon) ->
                        val isSelected = themeMode == mode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.18f else 0.12f)
                                    else itemBg
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    onThemeModeChange(mode)
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = label,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) textPrimary else textSecondary
                                )
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onThemeModeChange(mode)
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = textSecondary
                                )
                            )
                        }
                    }
                }
            }

            // Section 3: Personalización de Colores y Temas de la App
            SettingsCategoryHeader(title = "TEMAS Y PALETA DE COLORES", icon = Icons.Default.Palette)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Elige el color distintivo de Download Free",
                        fontSize = 13.sp,
                        color = textSecondary
                    )

                    val currentHex = remember(themeColor.primary) {
                        val r = (themeColor.primary.red * 255).toInt().coerceIn(0, 255)
                        val g = (themeColor.primary.green * 255).toInt().coerceIn(0, 255)
                        val b = (themeColor.primary.blue * 255).toInt().coerceIn(0, 255)
                        String.format("#%02X%02X%02X", r, g, b)
                    }
                    val onThemePrimary = remember(themeColor.primary) { themeColor.primary.contrastingTextColor() }
                    val readableThemeAccent = remember(themeColor.primary, isDarkTheme) {
                        themeColor.primary.ensureReadableAccent(isDarkTheme)
                    }

                    // Card estilizada con efecto de iluminación ambiental y profundidad
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        themeColor.primary.copy(alpha = if (isDarkTheme) 0.18f else 0.12f),
                                        if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.6f) else Color(0xFFF1F5F9)
                                    )
                                )
                            )
                            .border(
                                1.dp,
                                Brush.horizontalGradient(
                                    listOf(
                                        themeColor.primary.copy(alpha = 0.45f),
                                        cardBorder.copy(alpha = 0.6f)
                                    )
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { showColorPickerDialog = true }
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                // Gema / Burbuja de color en relieve 3D
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    themeColor.primary,
                                                    themeColor.primaryVariant
                                                )
                                            )
                                        )
                                        .border(
                                            2.dp,
                                            Brush.verticalGradient(
                                                listOf(
                                                    Color.White.copy(alpha = 0.7f),
                                                    Color.Black.copy(alpha = 0.4f)
                                                )
                                            ),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = onThemePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Color de la aplicación",
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    // Código HEX dentro de una pastilla sutil
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isDarkTheme) Color(0xFF0F172A) else Color.White)
                                                .border(1.dp, themeColor.primary.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = currentHex,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                color = readableThemeAccent
                                            )
                                        }
                                        Text(
                                            text = themeColor.displayName,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { showColorPickerDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = themeColor.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Colorize,
                                    contentDescription = null,
                                    tint = onThemePrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Elegir",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = onThemePrimary
                                )
                            }
                        }
                    }

                    Text(
                        text = "Elige cualquier color del espectro completo o escribe tu código HEX personalizado.",
                        fontSize = 11.5.sp,
                        color = textSecondary
                    )
                }
            }

            // Section 4: Gestor de Descargas Avanzado
            SettingsCategoryHeader(title = "GESTOR DE DESCARGAS", icon = Icons.Default.Download)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Wi-Fi Only
                    SettingsSwitchRow(
                        icon = Icons.Default.Wifi,
                        title = "Descargar solo con Wi-Fi",
                        subtitle = "Evita el consumo de datos móviles en redes móviles.",
                        checked = wifiOnlyDownloads,
                        isDarkTheme = isDarkTheme,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onCheckedChange = {
                            wifiOnlyDownloads = it
                            Toast.makeText(context, if (it) "Descargas limitadas a Wi-Fi" else "Descargas permitidas en cualquier red", Toast.LENGTH_SHORT).show()
                        }
                    )

                    HorizontalDivider(color = dividerColor)

                    // Concurrent downloads limit (1 to 5)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Límite de descargas simultáneas",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = textPrimary
                            )
                            Text(
                                text = "Máximo 5 descargas concurrentes. El resto espera en cola.",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    if (maxConcurrentDownloads > 1) {
                                        onMaxConcurrentDownloadsChange(maxConcurrentDownloads - 1)
                                    }
                                },
                                enabled = maxConcurrentDownloads > 1,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Menos",
                                    tint = if (maxConcurrentDownloads > 1) textPrimary else textSecondary.copy(alpha = 0.4f)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.18f else 0.12f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "$maxConcurrentDownloads",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            IconButton(
                                onClick = {
                                    if (maxConcurrentDownloads < 5) {
                                        onMaxConcurrentDownloadsChange(maxConcurrentDownloads + 1)
                                    }
                                },
                                enabled = maxConcurrentDownloads < 5,
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Más",
                                    tint = if (maxConcurrentDownloads < 5) textPrimary else textSecondary.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = dividerColor)

                    // Carpeta de descargas
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showFolderDialog = true }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.2f else 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "Carpeta de descargas",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = textPrimary
                                )
                                Text(
                                    text = downloadFolderName,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                if (downloadFolderPath.isNotBlank()) {
                                    Text(
                                        text = downloadFolderPath,
                                        fontSize = 10.5.sp,
                                        color = textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        Button(
                            onClick = { showFolderDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.25f else 0.15f),
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Cambiar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Section 5: Motor de Reproducción Pro
            SettingsCategoryHeader(title = "REPRODUCTOR PRO", icon = Icons.Default.PlayCircleOutline)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    SettingsSwitchRow(
                        icon = Icons.Default.Memory,
                        title = "Decodificación por Hardware (HW+)",
                        subtitle = "Acelera la renderización de video y optimiza el consumo de batería.",
                        checked = hardwareAcceleration,
                        isDarkTheme = isDarkTheme,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onCheckedChange = { hardwareAcceleration = it }
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.TouchApp,
                        title = "Gestos Táctiles Avanzados",
                        subtitle = "Doble toque ±10s, deslizar para brillo y volumen, mantener presionado para 2x.",
                        checked = screenGestures,
                        isDarkTheme = isDarkTheme,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onCheckedChange = { screenGestures = it }
                    )
                    SettingsSwitchRow(
                        icon = Icons.Default.PlayCircleOutline,
                        title = "Reanudar Reproducción Automática",
                        subtitle = "Guarda la posición exacta en milisegundos para continuar donde lo dejaste.",
                        checked = autoResume,
                        isDarkTheme = isDarkTheme,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        onCheckedChange = { autoResume = it }
                    )
                }
            }

            // Section 6: Almacenamiento y Caché
            SettingsCategoryHeader(title = "ALMACENAMIENTO Y CACHÉ", icon = Icons.Default.CleaningServices)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Limpiar memoria caché",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = textPrimary
                            )
                            Text(
                                text = "Elimina imágenes temporales y restablece la caché local.",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showClearCacheDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Vaciar Caché Ahora", color = textPrimary, fontSize = 13.sp)
                    }
                }
            }

            // Section 7: Enlace Oficial
            SettingsCategoryHeader(title = "CANAL OFICIAL", icon = Icons.Default.Info)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, cardBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Telegram Canal Oficial
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(itemBg)
                            .border(1.dp, cardBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/downloadfreeelielet"))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Telegram: @downloadfreeelielet", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_telegram_logo),
                            contentDescription = "Telegram Oficial",
                            modifier = Modifier.size(36.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Canal de Telegram",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = textPrimary
                            )
                            Text(
                                text = "@downloadfreeelielet",
                                fontSize = 12.sp,
                                color = Color(0xFF2AABEE)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        // Dialog: Clear Cache Confirmation
        if (showClearCacheDialog) {
            AlertDialog(
                onDismissRequest = { showClearCacheDialog = false },
                containerColor = if (isDarkTheme) Color(0xFF0D1424) else Color.White,
                title = { Text("¿Vaciar memoria caché?", color = textPrimary) },
                text = {
                    Text(
                        "Se eliminarán las imágenes temporales almacenadas. Los videos ya descargados no se verán afectados.",
                        color = textSecondary
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onClearCache()
                            showClearCacheDialog = false
                            Toast.makeText(context, "Caché limpiada con éxito", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Confirmar", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearCacheDialog = false }) {
                        Text("Cancelar", color = textSecondary)
                    }
                }
            )
        }

        if (showColorPickerDialog) {
            ColorPickerDialog(
                currentColor = themeColor.primary,
                isDarkTheme = isDarkTheme,
                onDismiss = { showColorPickerDialog = false },
                onColorSelected = { selectedColor ->
                    onThemeColorChange(AppThemeColor.fromCustomColor(selectedColor))
                }
            )
        }

        if (showFolderDialog) {
            DownloadFolderDialog(
                currentName = downloadFolderName,
                currentPath = downloadFolderPath,
                isDarkTheme = isDarkTheme,
                onDismiss = { showFolderDialog = false },
                onSelectFolder = { name, path ->
                    onDownloadFolderChange(name, path)
                },
                onOpenSystemPicker = {
                    openFolderLauncher.launch(null)
                }
            )
        }
    }
}

@Composable
private fun SettingsCategoryHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp, start = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    isDarkTheme: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = textPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = textSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                uncheckedTrackColor = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0)
            )
        )
    }
}

@Composable
private fun ColorPickerDialog(
    currentColor: Color,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit
) {
    val hsvInit = remember(currentColor) {
        val hsv = FloatArray(3)
        val r = (currentColor.red * 255).toInt().coerceIn(0, 255)
        val g = (currentColor.green * 255).toInt().coerceIn(0, 255)
        val b = (currentColor.blue * 255).toInt().coerceIn(0, 255)
        android.graphics.Color.RGBToHSV(r, g, b, hsv)
        hsv
    }

    var hue by remember { mutableFloatStateOf(hsvInit[0]) }
    var saturation by remember { mutableFloatStateOf(hsvInit[1].coerceIn(0f, 1f)) }
    var value by remember { mutableFloatStateOf(hsvInit[2].coerceIn(0f, 1f)) }

    val activeColor = remember(hue, saturation, value) {
        val argb = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
        Color(argb)
    }

    val pureHueColor = remember(hue) {
        val argb = android.graphics.Color.HSVToColor(floatArrayOf(hue, 1f, 1f))
        Color(argb)
    }

    var hexInput by remember {
        val r = (currentColor.red * 255).toInt().coerceIn(0, 255)
        val g = (currentColor.green * 255).toInt().coerceIn(0, 255)
        val b = (currentColor.blue * 255).toInt().coerceIn(0, 255)
        mutableStateOf(String.format("#%02X%02X%02X", r, g, b))
    }
    var isHexValid by remember { mutableStateOf(true) }

    fun syncHex(c: Color) {
        val r = (c.red * 255).toInt().coerceIn(0, 255)
        val g = (c.green * 255).toInt().coerceIn(0, 255)
        val b = (c.blue * 255).toInt().coerceIn(0, 255)
        hexInput = String.format("#%02X%02X%02X", r, g, b)
        isHexValid = true
    }

    val rainbowColors = remember {
        listOf(
            Color(0xFFFF0000), // Red
            Color(0xFFFF00FF), // Magenta
            Color(0xFF0000FF), // Blue
            Color(0xFF00FFFF), // Cyan
            Color(0xFF00FF00), // Green
            Color(0xFFFFFF00), // Yellow
            Color(0xFFFF0000)  // Red
        )
    }

    var canvasBoxSize by remember { mutableStateOf(IntSize.Zero) }
    var hueBarBoxSize by remember { mutableStateOf(IntSize.Zero) }

    fun updateSatVal(x: Float, y: Float) {
        if (canvasBoxSize.width <= 0 || canvasBoxSize.height <= 0) return
        val s = (x / canvasBoxSize.width).coerceIn(0f, 1f)
        val v = (1f - (y / canvasBoxSize.height)).coerceIn(0f, 1f)
        saturation = s
        value = v
        val argb = android.graphics.Color.HSVToColor(floatArrayOf(hue, s, v))
        syncHex(Color(argb))
    }

    fun updateHue(y: Float) {
        if (hueBarBoxSize.height <= 0) return
        val ratio = (y / hueBarBoxSize.height).coerceIn(0f, 1f)
        val h = (1f - ratio) * 360f
        hue = h.coerceIn(0f, 360f)
        val argb = android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value))
        syncHex(Color(argb))
    }

    val cardBg = if (isDarkTheme) Color(0xFF0F172A) else Color.White
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF475569)
    val borderColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1)
    val onActiveColor = remember(activeColor) { activeColor.contrastingTextColor() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(activeColor, activeColor.copy(alpha = 0.6f))
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = onActiveColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "Elegir Color",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = textPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Main visual pickers row: 3D Depth Canvas + 3D Cylindrical Hue Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 1. Saturation / Value Canvas con profundidad 3D
                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .height(220.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                1.5.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = if (isDarkTheme) 0.35f else 0.8f),
                                        Color.Black.copy(alpha = if (isDarkTheme) 0.7f else 0.25f)
                                    )
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .onSizeChanged { canvasBoxSize = it }
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    updateSatVal(offset.x, offset.y)
                                }
                            }
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    change.consume()
                                    updateSatVal(change.position.x, change.position.y)
                                }
                            }
                    ) {
                        // Base: Horizontal gradient White -> pureHueColor
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.White, pureHueColor),
                                startX = 0f,
                                endX = size.width
                            )
                        )
                        // Overlay: Vertical gradient Transparent -> Black
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startY = 0f,
                                endY = size.height
                            )
                        )

                        // 3D Inner Bevel / Shadow effect (gives physical inset depth to canvas)
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent),
                                startY = 0f,
                                endY = 16.dp.toPx()
                            )
                        )
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent),
                                startX = 0f,
                                endX = 14.dp.toPx()
                            )
                        )

                        // Selector indicator thumb en relieve 3D
                        val selX = (saturation * size.width).coerceIn(0f, size.width)
                        val selY = ((1f - value) * size.height).coerceIn(0f, size.height)
                        val center = Offset(selX, selY)

                        // Sombra exterior proyectada (3D drop shadow)
                        drawCircle(
                            color = Color(0x77000000),
                            radius = 15.dp.toPx(),
                            center = center.copy(y = center.y + 2.dp.toPx())
                        )
                        // Anillo exterior oscuro
                        drawCircle(
                            color = Color(0xDD000000),
                            radius = 13.dp.toPx(),
                            center = center,
                            style = Stroke(width = 3.5.dp.toPx())
                        )
                        // Anillo bisel metálico blanco
                        drawCircle(
                            color = Color.White,
                            radius = 10.dp.toPx(),
                            center = center,
                            style = Stroke(width = 3.dp.toPx())
                        )
                        // Núcleo del color activo
                        drawCircle(
                            color = activeColor,
                            radius = 7.5.dp.toPx(),
                            center = center
                        )
                        // Reflejo de luz 3D en la parte superior del visor
                        drawCircle(
                            color = Color.White.copy(alpha = 0.65f),
                            radius = 2.5.dp.toPx(),
                            center = Offset(center.x - 2.dp.toPx(), center.y - 2.5.dp.toPx())
                        )
                    }

                    // 2. Barra cilíndrica de arcoíris vertical con efecto 3D
                    Canvas(
                        modifier = Modifier
                            .width(36.dp)
                            .height(220.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                1.5.dp,
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = if (isDarkTheme) 0.35f else 0.8f),
                                        Color.Black.copy(alpha = if (isDarkTheme) 0.7f else 0.25f)
                                    )
                                ),
                                RoundedCornerShape(14.dp)
                            )
                            .onSizeChanged { hueBarBoxSize = it }
                            .pointerInput(Unit) {
                                detectTapGestures { offset ->
                                    updateHue(offset.y)
                                }
                            }
                            .pointerInput(Unit) {
                                detectDragGestures { change, _ ->
                                    change.consume()
                                    updateHue(change.position.y)
                                }
                            }
                    ) {
                        // Fondo de arcoíris completo
                        drawRect(brush = Brush.verticalGradient(rainbowColors))

                        // Efecto 3D cilíndrico (tubo de cristal): sombra en bordes y brillo central
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.25f),
                                    Color.Black.copy(alpha = 0.35f)
                                )
                            )
                        )

                        // Sombra superior e inferior para efecto tubo
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.3f), Color.Transparent),
                                startY = 0f,
                                endY = 12.dp.toPx()
                            )
                        )

                        // Indicador 3D deslizante tipo abrazadera metálica
                        val hueRatio = (1f - (hue / 360f)).coerceIn(0f, 1f)
                        val thumbY = (hueRatio * size.height).coerceIn(7.dp.toPx(), size.height - 7.dp.toPx())

                        // Sombra proyectada del cursor
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.6f),
                            topLeft = Offset(1.dp.toPx(), thumbY - 5.dp.toPx() + 2.dp.toPx()),
                            size = Size(size.width - 2.dp.toPx(), 11.dp.toPx()),
                            cornerRadius = CornerRadius(5.dp.toPx())
                        )
                        // Borde exterior negro
                        drawRoundRect(
                            color = Color.Black.copy(alpha = 0.9f),
                            topLeft = Offset(1.5.dp.toPx(), thumbY - 5.5.dp.toPx()),
                            size = Size(size.width - 3.dp.toPx(), 11.dp.toPx()),
                            cornerRadius = CornerRadius(5.dp.toPx()),
                            style = Stroke(width = 2.5.dp.toPx())
                        )
                        // Pastilla blanca interior con reflejo
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(2.5.dp.toPx(), thumbY - 4.5.dp.toPx()),
                            size = Size(size.width - 5.dp.toPx(), 9.dp.toPx()),
                            cornerRadius = CornerRadius(4.dp.toPx())
                        )
                        // Brillo metálico en el centro del cursor
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.9f),
                            topLeft = Offset(5.dp.toPx(), thumbY - 3.dp.toPx()),
                            size = Size(size.width - 10.dp.toPx(), 3.dp.toPx()),
                            cornerRadius = CornerRadius(2.dp.toPx())
                        )
                    }
                }

                // Row with hint and HEX text input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Toca o desliza en el lienzo",
                        fontSize = 12.sp,
                        color = textSecondary,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = hexInput,
                        onValueChange = { input ->
                            hexInput = input
                            val clean = input.trim().removePrefix("#")
                            if (clean.length == 6 || clean.length == 8) {
                                try {
                                    val parsed = android.graphics.Color.parseColor("#$clean")
                                    val c = Color(parsed)
                                    val hsv = FloatArray(3)
                                    android.graphics.Color.RGBToHSV(
                                        (c.red * 255).toInt(),
                                        (c.green * 255).toInt(),
                                        (c.blue * 255).toInt(),
                                        hsv
                                    )
                                    hue = hsv[0]
                                    saturation = hsv[1].coerceIn(0f, 1f)
                                    value = hsv[2].coerceIn(0f, 1f)
                                    isHexValid = true
                                } catch (_: Exception) {
                                    isHexValid = false
                                }
                            } else {
                                isHexValid = clean.isEmpty()
                            }
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = textPrimary
                        ),
                        singleLine = true,
                        isError = !isHexValid,
                        modifier = Modifier.width(135.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = activeColor,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            cursorColor = activeColor
                        )
                    )
                }

                // Selected color preview pill
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = activeColor,
                    shadowElevation = 4.dp,
                    border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hexInput,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = onActiveColor
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onColorSelected(activeColor)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = activeColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Aplicar", color = onActiveColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = textSecondary)
            }
        },
        containerColor = cardBg
    )
}

@Composable
private fun DownloadFolderDialog(
    currentName: String,
    currentPath: String,
    isDarkTheme: Boolean,
    onDismiss: () -> Unit,
    onSelectFolder: (name: String, path: String) -> Unit,
    onOpenSystemPicker: () -> Unit
) {
    val context = LocalContext.current
    val cardBg = if (isDarkTheme) Color(0xFF0F172A) else Color.White
    val textPrimary = if (isDarkTheme) Color.White else Color(0xFF0F172A)
    val textSecondary = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF475569)

    val options = remember {
        listOf(
            Triple(
                "Películas (Por defecto)",
                context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath ?: "",
                "Almacenamiento privado de la app (Movies)"
            ),
            Triple(
                "Descargas de la app",
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath ?: "",
                "Almacenamiento privado de la app (Downloads)"
            ),
            Triple(
                "Videos y Galería (DCIM)",
                context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath ?: "",
                "Almacenamiento privado de la app (DCIM)"
            ),
            Triple(
                "Almacenamiento Público (DownloadFree)",
                try {
                    File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "DownloadFree").absolutePath
                } catch (_: Exception) { "" },
                "Carpeta visible en el explorador de archivos"
            )
        )
    }

    var selectedOptionName by remember { mutableStateOf(currentName) }
    var selectedOptionPath by remember { mutableStateOf(currentPath) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Carpeta de Descargas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = textPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Selecciona la carpeta donde se guardarán las películas y videos descargados:",
                    fontSize = 12.5.sp,
                    color = textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))

                options.forEach { (name, path, desc) ->
                    val isSelected = selectedOptionName == name
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.2f else 0.12f) else Color.Transparent,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else textSecondary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedOptionName = name
                                selectedOptionPath = path
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedOptionName = name
                                    selectedOptionPath = path
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    unselectedColor = textSecondary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = textPrimary
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Opción para seleccionar cualquier carpeta del sistema
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onOpenSystemPicker()
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Elegir otra carpeta con el explorador...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Abre el selector de carpetas del sistema",
                                fontSize = 11.sp,
                                color = textSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSelectFolder(selectedOptionName, selectedOptionPath)
                    onDismiss()
                }
            ) {
                Text("Guardar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = textSecondary)
            }
        },
        containerColor = cardBg
    )
}
