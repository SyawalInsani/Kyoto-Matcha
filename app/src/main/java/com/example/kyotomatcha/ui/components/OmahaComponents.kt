package com.example.kyotomatcha.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kyotomatcha.R

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
