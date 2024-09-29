package com.kyang.tftteambuilder.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kyang.tftteambuilder.R

@Composable
fun DetailPane(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(stringResource(R.string.detail_screen))
    }
}