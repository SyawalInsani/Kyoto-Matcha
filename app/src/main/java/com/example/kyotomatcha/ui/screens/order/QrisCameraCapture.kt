package com.example.kyotomatcha.ui.screens.order

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.kyotomatcha.ui.theme.MatchaGreen
import com.example.kyotomatcha.ui.theme.White
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.min

// Bingkai panduan: pas-kan layar HP pelanggan di dalam kotak supaya hasil baca bagus.
// Bingkai berbentuk potret (layar HP). Nilai ini juga dipakai untuk memotong hasil foto.
private const val FRAME_WIDTH_FRACTION = 0.74f
// Sisakan ruang untuk teks petunjuk (atas) dan tombol foto (bawah) agar tidak menimpa bingkai.
private const val FRAME_TOP_FRACTION = 0.12f
private const val FRAME_AREA_HEIGHT_FRACTION = 0.70f
private const val FRAME_ASPECT = 1.9f // tinggi : lebar layar HP
private const val MAX_UPLOAD_WIDTH = 1800

/** Posisi bingkai di dalam tampilan kamera (piksel tampilan). */
private data class FrameRect(val left: Float, val top: Float, val width: Float, val height: Float)

private fun frameFor(view: IntSize): FrameRect {
    val w = view.width * FRAME_WIDTH_FRACTION
    val areaTop = view.height * FRAME_TOP_FRACTION
    val areaH = view.height * FRAME_AREA_HEIGHT_FRACTION
    val h = min(w * FRAME_ASPECT, areaH)
    val frameW = if (h < w * FRAME_ASPECT) h / FRAME_ASPECT else w
    return FrameRect((view.width - frameW) / 2f, areaTop + (areaH - h) / 2f, frameW, h)
}

/**
 * Kamera dengan bingkai panduan. onCaptured menerima JPEG yang sudah dipotong sesuai bingkai
 * (pratinjau diisi dengan scaleType FILL_CENTER, jadi bingkai bisa dipetakan ke gambar hasil foto).
 */
@Composable
fun QrisCameraCapture(
    onCaptured: (jpeg: ByteArray, preview: Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var granted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    var asked by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted = it }
    LaunchedEffect(Unit) {
        if (!granted && !asked) {
            asked = true
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (!granted) {
        Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Izin kamera diperlukan untuk memotret bukti bayar.", fontSize = 13.sp, textAlign = TextAlign.Center)
            Button(
                onClick = { launcher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.padding(top = 12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
            ) { Text("Izinkan kamera", color = White) }
        }
        return
    }

    val controller = remember {
        LifecycleCameraController(context).apply { setEnabledUseCases(CameraController.IMAGE_CAPTURE) }
    }
    LaunchedEffect(controller) { controller.bindToLifecycle(lifecycleOwner) }

    var viewSize by remember { mutableStateOf(IntSize.Zero) }
    var shooting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Box(modifier = modifier.onSizeChanged { viewSize = it }) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    this.controller = controller
                }
            }
        )

        // Gelapkan area di luar bingkai, lalu gambar sudut-sudut bingkai.
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)) {
            val f = frameFor(IntSize(size.width.toInt(), size.height.toInt()))
            drawRect(Color(0x99000000))
            drawRoundRect(
                color = Color.Transparent, topLeft = Offset(f.left, f.top), size = Size(f.width, f.height),
                cornerRadius = CornerRadius(24f), blendMode = BlendMode.Clear
            )
            drawRoundRect(
                color = Color.White, topLeft = Offset(f.left, f.top), size = Size(f.width, f.height),
                cornerRadius = CornerRadius(24f), style = Stroke(width = 5f)
            )
        }

        Text(
            text = "Pas-kan layar HP pelanggan di dalam kotak",
            color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(top = 10.dp, start = 16.dp, end = 16.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (error.isNotEmpty()) Text(error, color = Color(0xFFFFB4B4), fontSize = 12.sp)
            Button(
                enabled = !shooting && viewSize != IntSize.Zero,
                onClick = {
                    shooting = true
                    error = ""
                    controller.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                val result = try {
                                    cropToFrame(image, viewSize)
                                } catch (e: Exception) {
                                    null
                                } finally {
                                    image.close()
                                }
                                shooting = false
                                if (result == null) error = "Foto gagal diproses. Coba lagi." else onCaptured(result.first, result.second)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                shooting = false
                                error = "Kamera gagal memotret. Coba lagi."
                            }
                        }
                    )
                },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
            ) { Text(if (shooting) "Memotret…" else "Foto bukti bayar", color = White, fontWeight = FontWeight.Bold) }
        }
    }
}

/** Potong foto tepat pada bingkai panduan, perkecil bila perlu, simpan sebagai JPEG. */
private fun cropToFrame(image: ImageProxy, view: IntSize): Pair<ByteArray, Bitmap> {
    val rotation = image.imageInfo.rotationDegrees
    val raw = image.toBitmap()
    val upright = if (rotation == 0) raw else Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, Matrix().apply { postRotate(rotation.toFloat()) }, true)

    // FILL_CENTER: gambar diskalakan sampai menutupi tampilan, sisanya terpotong di tengah.
    val scale = max(view.width.toFloat() / upright.width, view.height.toFloat() / upright.height)
    val visibleLeft = (upright.width - view.width / scale) / 2f
    val visibleTop = (upright.height - view.height / scale) / 2f
    val f = frameFor(view)

    val x = (visibleLeft + f.left / scale).toInt().coerceIn(0, upright.width - 1)
    val y = (visibleTop + f.top / scale).toInt().coerceIn(0, upright.height - 1)
    val w = (f.width / scale).toInt().coerceAtMost(upright.width - x).coerceAtLeast(1)
    val h = (f.height / scale).toInt().coerceAtMost(upright.height - y).coerceAtLeast(1)
    var cropped = Bitmap.createBitmap(upright, x, y, w, h)

    if (cropped.width > MAX_UPLOAD_WIDTH) {
        val ratio = MAX_UPLOAD_WIDTH.toFloat() / cropped.width
        cropped = Bitmap.createScaledBitmap(cropped, MAX_UPLOAD_WIDTH, (cropped.height * ratio).toInt(), true)
    }
    val out = ByteArrayOutputStream()
    cropped.compress(Bitmap.CompressFormat.JPEG, 92, out)
    return out.toByteArray() to cropped
}
