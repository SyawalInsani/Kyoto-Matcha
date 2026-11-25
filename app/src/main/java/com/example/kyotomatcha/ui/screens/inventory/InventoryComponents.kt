package com.example.kyotomatcha.ui.screens.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.kyotomatcha.data.remote.StockItemDto
import com.example.kyotomatcha.ui.theme.*
import com.example.kyotomatcha.util.QuantityUtil

private val fieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White)

@Composable
fun LabeledField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    numeric: Boolean = false,
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 12.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            visualTransformation = visualTransformation,
            placeholder = if (placeholder.isEmpty()) null else ({ Text(placeholder, color = TextMuted, fontSize = 13.sp) }),
            keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text),
            shape = RoundedCornerShape(16.dp),
            colors = fieldColors,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Pilih item stok dari daftar. */
@Composable
fun ItemPicker(items: List<StockItemDto>, selected: StockItemDto?, onSelect: (StockItemDto) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text(text = "Item", fontSize = 12.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(4.dp))
        Box {
            OutlinedTextField(
                value = selected?.name ?: "Pilih item",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = PrimaryDarkGreen) },
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )
            // Lapisan transparan: seluruh kolom bisa ditekan untuk membuka daftar.
            Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text("${item.name} · ${QuantityUtil.withUnit(item.quantity, item.unit)}") },
                        onClick = {
                            onSelect(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (bg, fg, text) = when (status) {
        "habis" -> Triple(Color(0xFFFED7D7), Color(0xFF9B2C2C), "Habis")
        "menipis" -> Triple(Color(0xFFFEFCBF), Color(0xFFB7791F), "Menipis")
        else -> Triple(Color(0xFFC6F6D5), Color(0xFF22543D), "Tersedia")
    }
    Surface(shape = RoundedCornerShape(6.dp), color = bg) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = fg,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun ErrorText(message: String) {
    if (message.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = message, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFFC53030))
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(text = label, fontSize = 12.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
    }
}

/** Ringkasan setelah stok berhasil disimpan (dipakai Stok Masuk dan Stok Keluar). */
@Composable
fun StockResultView(
    title: String,
    result: com.example.kyotomatcha.data.repository.StockActionResult.Success,
    qtyLabel: String,
    details: List<Pair<String, String>>,
    onNavigateToHistory: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val item = result.item
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(20.dp))
        Surface(
            shape = androidx.compose.foundation.shape.CircleShape,
            color = MatchaGreen.copy(alpha = 0.2f),
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = MatchaGreen, modifier = Modifier.size(36.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryDarkGreen)
        Spacer(modifier = Modifier.height(24.dp))

        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = White, shadowElevation = 1.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "${item.name} · $qtyLabel", fontSize = 13.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "${QuantityUtil.withUnit(result.quantityBefore, item.unit)} -> ${QuantityUtil.withUnit(item.quantity, item.unit)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatusChip(item.status)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = White, shadowElevation = 1.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                SummaryRow("Referensi", result.reference)
                details.forEach { (label, value) -> SummaryRow(label, value) }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToHistory,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MatchaGreen)
        ) {
            Text(text = "Lihat Riwayat Stok", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = White)
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = White)
        ) {
            Text(text = "Kembali ke Inventaris", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = PrimaryDarkGreen)
        }
    }
}
