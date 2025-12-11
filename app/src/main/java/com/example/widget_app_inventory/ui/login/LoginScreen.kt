package com.example.widget_app_inventory.ui.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.example.widget_app_inventory.R

@Composable
fun LoginScreen(
    isBiometricAvailable: Boolean,
    onAuthenticate: () -> Unit,
    onManualLogin: () -> Unit,
    onError: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.Surface))
    ) {
        Image(
            painter = painterResource(id = R.drawable.image_login),
            contentDescription = "Ilustración",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .size(250.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Inventory",
                color = colorResource(id = R.color.Primary),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        val composition = rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.fingerprint)
        ).value

        val progress = animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        ).value

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(250.dp)
                .offset(y = (-80).dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (isBiometricAvailable) {
                        onAuthenticate()
                    } else {
                        onError("Biometría no disponible. Accediendo sin biometría.")
                        onManualLogin()
                    }
                }
        )
    }
}
