package com.example.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.util.Constants
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    testTag: String = "splash_screen"
) {
    val scale = remember { Animatable(0.6f) }
    val carAlpha = remember { Animatable(1f) }
    val bikeAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "glow_transition")
    val glowAnim by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_anim"
    )

    LaunchedEffect(Unit) {
        // Step 1: Scale up logo canvas
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )

        // Step 2: Car silhouette morphs into bike silhouette
        delay(600)
        carAlpha.animateTo(0f, animationSpec = tween(400))
        bikeAlpha.animateTo(1f, animationSpec = tween(400))

        // Step 3: Text & Tagline fade in
        delay(200)
        textAlpha.animateTo(1f, animationSpec = tween(600))

        // Step 4: Auto navigate
        delay(1000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(PrimaryBackground, SecondaryBackground)
                )
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // Radial Background Glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ChampagneGold.copy(alpha = 0.22f * glowAnim),
                        Color.Transparent
                    ),
                    center = center,
                    radius = size.minDimension * 0.7f
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated Vehicle Silhouette Container
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale.value),
                contentAlignment = Alignment.Center
            ) {
                // Car Icon
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier
                        .size(80.dp)
                        .alpha(carAlpha.value)
                )

                // Bike Icon (Morph Target)
                Icon(
                    imageVector = Icons.Default.TwoWheeler,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier
                        .size(80.dp)
                        .alpha(bikeAlpha.value)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Brand Title
            Text(
                text = Constants.APP_NAME,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 42.sp,
                    letterSpacing = 2.sp
                ),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tagline
            Text(
                text = Constants.TAGLINE,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha.value)
            )
        }
    }
}
