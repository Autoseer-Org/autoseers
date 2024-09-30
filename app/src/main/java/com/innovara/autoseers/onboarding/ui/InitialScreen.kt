package com.innovara.autoseers.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.innovara.autoseers.R

@Composable
fun InitialScreen(
    onStartPressed: (isSignUpFlow: Boolean) -> Unit = {},
) {
    val bitmap = ImageBitmap.imageResource(id = R.drawable.car)
    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier
            .fillMaxHeight()
            .drawBehind {
                bitmap.prepareToDraw()
                drawImage(
                    bitmap,
                    dstSize = IntSize(900, 600),
                    srcOffset = IntOffset(0, 35)
                )
            }
            .padding(12.dp)
    ) {
        val deviceHeight = with(LocalDensity.current) {
            LocalConfiguration.current.screenHeightDp.dp.toPx()
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Spacer(modifier = Modifier.height(100.dp))
            HeaderSection()
            Spacer(modifier = Modifier.height(deviceHeight.dp.times(.14f)))
            ButtonSection(
                onStartPressed = onStartPressed,
                onSigningPressed = onStartPressed
            )
        }
    }
}

@Composable
fun HeaderSection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.title_app_name),
            style = MaterialTheme.typography.headlineLarge,
            color = Color.Black
        )
        Text(
            text = stringResource(id = R.string.sub_title),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ButtonSection(
    onStartPressed: (Boolean) -> Unit = {},
    onSigningPressed: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Button(
            onClick = {
                onStartPressed(true)
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 74.dp)
        ) {
            Text(
                text = stringResource(id = R.string.button_start),
                style = MaterialTheme.typography.bodySmall
            )
        }
        TextButton(onClick = {
            onSigningPressed(false)
        }) {
            Text(
                text = stringResource(id = R.string.start_with_signin),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview
@Composable
fun PreviewInitialScreen() {
    InitialScreen()
}

@Preview
@Composable
fun PreviewButtonSection() {
    ButtonSection()
}

@Preview
@Composable
fun PreviewHeaderSection() {
    HeaderSection()
}