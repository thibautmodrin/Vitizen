package com.vitizen.app.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vitizen.app.R

@Composable
fun Logo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.vitizen_logo_v7),
        contentDescription = "Logo Vitizen",
        modifier = modifier.size(120.dp)
    )
} 