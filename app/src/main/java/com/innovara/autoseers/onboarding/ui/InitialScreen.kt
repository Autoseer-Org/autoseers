package com.innovara.autoseers.onboarding.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.innovara.autoseers.R

@Composable
fun InitialScreen(
    onStartPressed: (isSignUpFlow: Boolean) -> Unit = {},
) {
    val bitmap = ImageBitmap.imageResource(id = R.drawable.initial_car_1)
    Scaffold(
        modifier = Modifier
            .fillMaxHeight()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            HeaderSection()
            Image(
                bitmap = bitmap,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .offset {
                        IntOffset(0, 0)
                    },
                contentScale = ContentScale.FillWidth,
            )
            Box(modifier = Modifier.align(Alignment.CenterHorizontally).weight(1f)) {
                ButtonSection(
                    onStartPressed = onStartPressed,
                    onSigningPressed = onStartPressed,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(id = R.string.title_app_name),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Start
        )
        val geminiText = buildAnnotatedString {
            append(stringResource(id = R.string.powered_by))
            append(" ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)) {
                append("Gemini AI")
            }
        }
        Text(
            text = geminiText,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier.offset { IntOffset(0, -18) }
        )
        Text(
            text = stringResource(id = R.string.sub_title),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(top = 16.dp)
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
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                onStartPressed(true)
            }, modifier = Modifier
                .width(300.dp)
                .height(50.dp)

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