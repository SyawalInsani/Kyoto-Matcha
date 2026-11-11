package com.example.kyotomatcha.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kyotomatcha.R
import com.example.kyotomatcha.ui.theme.PrimaryDarkGreen
import com.example.kyotomatcha.ui.theme.TextMuted

@Composable
fun OmochaLogo(
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    Image(
        painter = painterResource(id = R.drawable.kyoto_matcha),
        contentDescription = "O-MACHA Logo",
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size)
    )
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, selected: Boolean, onClick: (() -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (!selected) {
                    onClick?.invoke()
                }
            }
            .width(68.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(22.dp),
            tint = if (selected) PrimaryDarkGreen else TextMuted
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) PrimaryDarkGreen else TextMuted,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
