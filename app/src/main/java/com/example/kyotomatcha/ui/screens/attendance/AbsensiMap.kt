package com.example.kyotomatcha.ui.screens.attendance

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.White
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

// Tile source publik tanpa kunci API
private val OsmHotTileSource = object : XYTileSource(
    "OpenStreetMapHOT",
    1, 19, 256, ".png",
    arrayOf(
        "https://a.tile.openstreetmap.fr/hot/",
        "https://b.tile.openstreetmap.fr/hot/"
    )
) {}

// Membuat ikon titik biru (Blue Dot) khas Google Maps untuk lokasi pengguna
private fun createBlueUserDotDrawable(context: Context): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val sizePx = (36 * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(sizePx, sizePx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    val center = sizePx / 2f
    val radiusOuter = sizePx * 0.46f
    val radiusMiddle = sizePx * 0.32f
    val radiusInner = sizePx * 0.22f

    val haloPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x3D1A73E8.toInt()
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(center, center, radiusOuter, haloPaint)

    val whiteBorderPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.FILL
        setShadowLayer(4f * density, 0f, 2f * density, 0x33000000.toInt())
    }
    canvas.drawCircle(center, center, radiusMiddle, whiteBorderPaint)

    val blueDotPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF1A73E8.toInt()
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(center, center, radiusInner, blueDotPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

// Membuat penanda pin lokasi warna merah khas (Red Pin) untuk kedai
private fun createRedLocationPinDrawable(context: Context): android.graphics.drawable.Drawable {
    val density = context.resources.displayMetrics.density
    val widthPx = (32 * density).toInt()
    val heightPx = (44 * density).toInt()
    val bitmap = android.graphics.Bitmap.createBitmap(widthPx, heightPx, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    val centerX = widthPx / 2f
    val headRadius = widthPx * 0.44f
    val headCenterY = headRadius + (2f * density)

    val pinPath = android.graphics.Path().apply {
        arcTo(
            centerX - headRadius,
            headCenterY - headRadius,
            centerX + headRadius,
            headCenterY + headRadius,
            -210f, 240f, false
        )
        lineTo(centerX, heightPx.toFloat() - (2f * density))
        close()
    }

    val shadowPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x33000000.toInt()
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawOval(
        centerX - (8f * density),
        heightPx.toFloat() - (4f * density),
        centerX + (8f * density),
        heightPx.toFloat(),
        shadowPaint
    )

    val redPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFEA4335.toInt() // Merah khas penanda lokasi (#EA4335)
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawPath(pinPath, redPaint)

    val whiteInnerPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = android.graphics.Paint.Style.FILL
    }
    canvas.drawCircle(centerX, headCenterY, headRadius * 0.48f, whiteInnerPaint)

    return android.graphics.drawable.BitmapDrawable(context.resources, bitmap)
}

@Composable
fun AbsensiMap(
    cabangLat: Double,
    cabangLng: Double,
    radiusMeter: Double,
    onJarakChange: (jarak: Float, lokasiPalsu: Boolean) -> Unit,
    recenterTrigger: Int = 0,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val onJarak by rememberUpdatedState(onJarakChange)
    val cabang = remember { GeoPoint(cabangLat, cabangLng) }
    val redPinDrawable = remember { createRedLocationPinDrawable(context) }
    var lastKnownUserGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }

    SideEffect {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = "Mozilla/5.0 (Android; Mobile; rv:120.0) Gecko/120.0 Firefox/120.0 KyotoMatchaApp/1.0"
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(OsmHotTileSource)
            setMultiTouchControls(true)
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
            controller.setZoom(17.5)
            controller.setCenter(cabang)

            // Lingkaran radius izin absen
            overlays.add(Polygon(this).apply {
                points = Polygon.pointsAsCircle(cabang, radiusMeter)
                fillPaint.color = 0x3378955A
                outlinePaint.color = 0xFF78955A.toInt()
                outlinePaint.strokeWidth = 3f
            })

            // Penanda cabang (Pin Merah)
            overlays.add(Marker(this).apply {
                position = cabang
                title = "Kyoto-Matcha"
                icon = redPinDrawable
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })
        }
    }

    LaunchedEffect(recenterTrigger) {
        if (recenterTrigger > 0) {
            lastKnownUserGeoPoint?.let { userPos ->
                mapView.controller.setZoom(17.5)
                mapView.controller.animateTo(userPos)
            }
        }
    }

    // Siklus hidup MapView
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // Izin lokasi
    var izin by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { izin = it }
    LaunchedEffect(Unit) {
        if (!izin) launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // Lokasi pengguna, diperbarui tiap 5 detik
    var hasCenteredUserLocation by remember { mutableStateOf(false) }

    DisposableEffect(izin) {
        if (!izin) return@DisposableEffect onDispose {}

        val client = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

        val blueDotDrawable = createBlueUserDotDrawable(context)

        val userMarker = Marker(mapView).apply {
            title = "Posisi Anda"
            icon = blueDotDrawable
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        }
        mapView.overlays.add(userMarker)

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                val userGeoPoint = GeoPoint(loc.latitude, loc.longitude)

                lastKnownUserGeoPoint = userGeoPoint
                userMarker.position = userGeoPoint

                // Fokus kamera ke koordinat titik pengguna
                if (!hasCenteredUserLocation) {
                    hasCenteredUserLocation = true
                    mapView.controller.setZoom(17.5)
                    mapView.controller.animateTo(userGeoPoint)
                }

                mapView.invalidate()

                val hasil = FloatArray(1)
                Location.distanceBetween(
                    loc.latitude, loc.longitude, cabangLat, cabangLng, hasil
                )
                val palsu = if (Build.VERSION.SDK_INT >= 31) loc.isMock
                else @Suppress("DEPRECATION") loc.isFromMockProvider
                onJarak(hasil[0], palsu)
            }
        }

        try {
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (_: SecurityException) { }

        onDispose {
            client.removeLocationUpdates(callback)
            mapView.overlays.remove(userMarker)
        }
    }

    Box(modifier = modifier) {
        // Fallback Vector Map Canvas (Background)
        CustomVectorMapFallback(modifier = Modifier.matchParentSize())

        // OSM Map View (Foreground)
        AndroidView(factory = { mapView }, modifier = Modifier.matchParentSize())

        // Custom Floating Zoom Controls (+ and -)
        Column(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.TopEnd),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .clickable { mapView.controller.zoomIn() },
                shape = CircleShape,
                color = White,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "+",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .clickable { mapView.controller.zoomOut() },
                shape = CircleShape,
                color = White,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "−",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryDarkGreen
                    )
                }
            }
        }
    }
}

@Composable
fun CustomVectorMapFallback(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Background terrain
        drawRect(color = Color(0xFFEBE7DD))

        // Park Area (light green)
        val parkPath = Path().apply {
            moveTo(width * 0.1f, height * 0.3f)
            lineTo(width * 0.45f, height * 0.2f)
            lineTo(width * 0.5f, height * 0.65f)
            lineTo(width * 0.15f, height * 0.7f)
            close()
        }
        drawPath(path = parkPath, color = Color(0xFFD4E5C9))

        // River (light blue curve)
        val riverPath = Path().apply {
            moveTo(0f, height * 0.4f)
            cubicTo(
                width * 0.3f, height * 0.35f,
                width * 0.6f, height * 0.6f,
                width, height * 0.5f
            )
        }
        drawPath(
            path = riverPath,
            color = Color(0xFFB5D0E8),
            style = Stroke(width = 36f)
        )

        // Road network (white lines)
        val roadColor = Color.White
        // Horizontal main road
        drawLine(
            color = roadColor,
            start = Offset(0f, height * 0.3f),
            end = Offset(width, height * 0.35f),
            strokeWidth = 24f
        )
        // Vertical main road
        drawLine(
            color = roadColor,
            start = Offset(width * 0.55f, 0f),
            end = Offset(width * 0.55f, height),
            strokeWidth = 22f
        )
        // Secondary roads
        drawLine(
            color = roadColor,
            start = Offset(width * 0.2f, 0f),
            end = Offset(width * 0.2f, height),
            strokeWidth = 14f
        )
        drawLine(
            color = roadColor,
            start = Offset(0f, height * 0.75f),
            end = Offset(width, height * 0.75f),
            strokeWidth = 16f
        )

        // Center Location Dot & Radius
        val center = Offset(width * 0.55f, height * 0.45f)
        // Radius ring
        drawCircle(color = Color(0x3378955A), radius = 100f, center = center)
        drawCircle(color = Color(0xFF78955A), radius = 100f, center = center, style = Stroke(width = 4f))

        // User blue location dot
        drawCircle(color = Color(0x442B6CB0), radius = 28f, center = center)
        drawCircle(color = Color.White, radius = 16f, center = center)
        drawCircle(color = Color(0xFF2B6CB0), radius = 12f, center = center)
    }
}
