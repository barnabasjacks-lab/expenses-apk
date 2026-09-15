package com.example.runner.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.runner.game.GameState
import com.example.runner.game.Lane
import com.example.runner.game.Obstacle
import com.example.runner.game.ObstacleType
import com.example.runner.game.PlayerAction
import com.example.runner.game.RunnerUiState
import com.example.runner.game.RunnerViewModel
import kotlin.math.abs

@Composable
fun RunnerGameScreen(
    viewModel: RunnerViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            // Gesture Swipes: Kushoto, Kulia, Kuruka (Juu), Kujikunja (Chini)
            .pointerInput(uiState.gameState) {
                if (uiState.gameState == GameState.PLAYING) {
                    var totalDragX = 0f
                    var totalDragY = 0f
                    detectDragGestures(
                        onDragStart = {
                            totalDragX = 0f
                            totalDragY = 0f
                        },
                        onDragEnd = {
                            val threshold = 40f
                            if (abs(totalDragX) > abs(totalDragY)) {
                                if (totalDragX > threshold) {
                                    viewModel.moveRight()
                                } else if (totalDragX < -threshold) {
                                    viewModel.moveLeft()
                                }
                            } else {
                                if (totalDragY < -threshold) {
                                    viewModel.jump()
                                } else if (totalDragY > threshold) {
                                    viewModel.slide()
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDragX += dragAmount.x
                            totalDragY += dragAmount.y
                        }
                    )
                }
            }
    ) {
        // Road and Game Canvas
        GameCanvas(uiState = uiState)

        // Top Heads-Up Display (HUD)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Developer Branding Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.55f)
                ) {
                    Text(
                        text = "DAR ES SALAAM RUNNER 🇹🇿",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFACC15),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF991B1B).copy(alpha = 0.8f)
                ) {
                    Text(
                        text = "Barnabas Msuku",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scoreboard Row (UMBALI, COINS, REKODI)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Umbali
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.9f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("UMBALI", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        Text(
                            "${uiState.distanceMeters}m",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Sarafu (Coins)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.9f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("COINS", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFBBF24))
                        Text(
                            "${uiState.coins} 🪙",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFBBF24)
                        )
                    }
                }

                // Rekodi Bora (High Score)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B).copy(alpha = 0.9f)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text("REKODI", style = MaterialTheme.typography.labelSmall, color = Color(0xFF34D399))
                        Text(
                            "${uiState.recordMeters}m 🏆",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF34D399)
                        )
                    }
                }
            }
        }

        // On-screen Control Buttons (Pamoja na Swipe, vitufe hivi vinarahisisha sana kucheza)
        if (uiState.gameState == GameState.PLAYING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left / Right controls
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            IconButton(onClick = { viewModel.moveLeft() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kushoto",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.25f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            IconButton(onClick = { viewModel.moveRight() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Kulia",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    // Jump & Slide controls
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF59E0B).copy(alpha = 0.85f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            IconButton(onClick = { viewModel.slide() }) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = "Inama Chini",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF10B981).copy(alpha = 0.9f),
                            modifier = Modifier.size(62.dp)
                        ) {
                            IconButton(onClick = { viewModel.jump() }) {
                                Icon(
                                    Icons.Default.ArrowUpward,
                                    contentDescription = "Ruka Juu",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // START SCREEN OVERLAY
        if (uiState.gameState == GameState.READY) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏃‍♂️", fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Kukimbia Barabarani",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFACC15),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Dar es Salaam Runner",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF334155),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("JINSI YA KUCHEZA:", fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8), fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Swipe Kushoto / Kulia au bofya mishale kukwepa Daladala", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                                Text("• Swipe Juu au bofya Kijani ⬆️ kuruka Mashimo na Bodaboda", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                                Text("• Swipe Chini au bofya Njano ⬇️ kuteleza chini ya Vizuizi", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                                Text("• Kusanya Sarafu 🪙 za dhahabu nyingi uwezavyo!", color = Color(0xFFFBBF24), fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Mchezo Umetengenezwa na Barnabas Msuku 🇹🇿",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { viewModel.startGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ANZA KUCHEZA SASA", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        // GAME OVER OVERLAY
        if (uiState.gameState == GameState.GAME_OVER) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💥", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "MCHEZO UMEISHA!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFEF4444)
                        )
                        Text(
                            text = uiState.lastHitReason,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFCBD5E1),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Results Box kama ulivyotaka
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF0F172A),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("UMBALI:", fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                                    Text("${uiState.distanceMeters}m", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 18.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("COINS:", fontWeight = FontWeight.Bold, color = Color(0xFFFBBF24))
                                    Text("${uiState.coins} 🪙", fontWeight = FontWeight.ExtraBold, color = Color(0xFFFBBF24), fontSize = 18.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("REKODI:", fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                                    Text("${uiState.recordMeters}m 🏆", fontWeight = FontWeight.ExtraBold, color = Color(0xFF34D399), fontSize = 18.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Umetengenezwa na Barnabas Msuku",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { viewModel.startGame() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("JARIBU TENA (RETRY)", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameCanvas(uiState: RunnerUiState) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 1. Sky & City Sunset Background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF0F172A), // Dark blue
                    Color(0xFF831843), // Pinkish sunset
                    Color(0xFFEA580C)  // Orange horizon
                ),
                startY = 0f,
                endY = height * 0.45f
            ),
            size = Size(width, height * 0.45f)
        )

        // Draw distant city buildings silhouette
        val buildingY = height * 0.32f
        val buildingColors = listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF1E293B))
        var bX = 0f
        var bIndex = 0
        while (bX < width) {
            val bWidth = 40f + (bIndex % 4) * 20f
            val bHeight = 70f + (bIndex % 5) * 25f
            drawRect(
                color = buildingColors[bIndex % buildingColors.size],
                topLeft = Offset(bX, buildingY - bHeight),
                size = Size(bWidth, bHeight)
            )
            bX += bWidth + 5f
            bIndex++
        }

        // 2. Road Perspective (Dar es Salaam Highway)
        // Horizon top width is narrow, bottom width is full
        val horizonY = height * 0.35f
        val roadTopLeftX = width * 0.28f
        val roadTopRightX = width * 0.72f
        val roadBottomLeftX = width * 0.05f
        val roadBottomRightX = width * 0.95f

        // Green Grass / Roadside
        drawRect(
            color = Color(0xFF064E3B), // Dark green roadside
            topLeft = Offset(0f, horizonY),
            size = Size(width, height - horizonY)
        )

        // Asphalt Road (Polygon)
        val roadPath = Path().apply {
            moveTo(roadTopLeftX, horizonY)
            lineTo(roadTopRightX, horizonY)
            lineTo(roadBottomRightX, height)
            lineTo(roadBottomLeftX, height)
            close()
        }
        drawPath(path = roadPath, color = Color(0xFF1E293B))

        // Road lane dividers (Milia ya njano na nyeupe ya barabara ya Dar)
        val numDashes = 10
        val offsetFactor = (uiState.distanceMeters % 10) / 10f
        for (i in 0..numDashes) {
            val progress = ((i + offsetFactor) / numDashes).coerceIn(0f, 1f)
            val currY = horizonY + (height - horizonY) * progress
            val currLeftX = roadTopLeftX + (roadBottomLeftX - roadTopLeftX) * progress
            val currRightX = roadTopRightX + (roadBottomRightX - roadTopRightX) * progress
            val roadW = currRightX - currLeftX

            // Lane 1 divider
            val lane1X = currLeftX + roadW * 0.333f
            // Lane 2 divider
            val lane2X = currLeftX + roadW * 0.666f

            val dashH = 10f + 25f * progress
            drawRect(
                color = Color(0xFFFDE047), // Yellow lane marks
                topLeft = Offset(lane1X - 3f, currY),
                size = Size(6f, dashH)
            )
            drawRect(
                color = Color(0xFFFDE047),
                topLeft = Offset(lane2X - 3f, currY),
                size = Size(6f, dashH)
            )
        }

        // Helper function to get X, Y on 3D perspective
        fun getCoordinates(lane: Lane, yProgress: Float): Offset {
            val progress = yProgress.coerceIn(0f, 1f)
            val currY = horizonY + (height - horizonY) * progress
            val currLeftX = roadTopLeftX + (roadBottomLeftX - roadTopLeftX) * progress
            val currRightX = roadTopRightX + (roadBottomRightX - roadTopRightX) * progress
            val roadW = currRightX - currLeftX

            val laneCenterX = when (lane) {
                Lane.LEFT -> currLeftX + roadW * 0.166f
                Lane.CENTER -> currLeftX + roadW * 0.5f
                Lane.RIGHT -> currLeftX + roadW * 0.833f
            }
            return Offset(laneCenterX, currY)
        }

        // 3. Draw Coins
        for (coin in uiState.coinsOnRoad) {
            if (coin.yProgress in 0f..1.1f && !coin.collected) {
                val pos = getCoordinates(coin.lane, coin.yProgress)
                val scale = 0.4f + 0.6f * coin.yProgress
                val radius = 18f * scale

                // Gold coin with shine
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = radius,
                    center = pos
                )
                drawCircle(
                    color = Color(0xFFFDE047),
                    radius = radius * 0.7f,
                    center = pos
                )
            }
        }

        // 4. Draw Obstacles (Daladala, Bodaboda, Pothole, Barrier)
        for (obs in uiState.obstacles) {
            if (obs.yProgress in 0f..1.15f) {
                val pos = getCoordinates(obs.lane, obs.yProgress)
                val scale = 0.35f + 0.65f * obs.yProgress

                when (obs.type) {
                    ObstacleType.DALADALA -> {
                        // Iconic Dar Daladala Minibus (White with Blue/Red Stripe)
                        val busW = 85f * scale
                        val busH = 110f * scale
                        val topLeft = Offset(pos.x - busW / 2, pos.y - busH)

                        // Bus Body (White)
                        drawRoundRect(
                            color = Color.White,
                            topLeft = topLeft,
                            size = Size(busW, busH),
                            cornerRadius = CornerRadius(8f * scale)
                        )
                        // Roof / Blue Daladala Stripe (Rangi ya Daladala za Dar)
                        drawRect(
                            color = Color(0xFF2563EB), // Blue stripe
                            topLeft = Offset(topLeft.x, topLeft.y + busH * 0.5f),
                            size = Size(busW, busH * 0.25f)
                        )
                        // Windshield / Kioo cha nyuma
                        drawRect(
                            color = Color(0xFF0F172A),
                            topLeft = Offset(topLeft.x + 6f * scale, topLeft.y + 10f * scale),
                            size = Size(busW - 12f * scale, busH * 0.35f)
                        )
                        // Taillights
                        drawCircle(
                            color = Color(0xFFDC2626),
                            radius = 6f * scale,
                            center = Offset(topLeft.x + 12f * scale, topLeft.y + busH - 12f * scale)
                        )
                        drawCircle(
                            color = Color(0xFFDC2626),
                            radius = 6f * scale,
                            center = Offset(topLeft.x + busW - 12f * scale, topLeft.y + busH - 12f * scale)
                        )
                    }

                    ObstacleType.BODABODA -> {
                        // Bodaboda motorcycle
                        val motoW = 45f * scale
                        val motoH = 65f * scale
                        val topLeft = Offset(pos.x - motoW / 2, pos.y - motoH)

                        // Rider jacket (Reflector kijani)
                        drawCircle(
                            color = Color(0xFF84CC16),
                            radius = 16f * scale,
                            center = Offset(pos.x, topLeft.y + 18f * scale)
                        )
                        // Helmet
                        drawCircle(
                            color = Color(0xFFEA580C),
                            radius = 10f * scale,
                            center = Offset(pos.x, topLeft.y + 10f * scale)
                        )
                        // Bike body
                        drawRect(
                            color = Color(0xFF1E293B),
                            topLeft = Offset(topLeft.x + 10f * scale, topLeft.y + 35f * scale),
                            size = Size(motoW - 20f * scale, motoH - 35f * scale)
                        )
                        // Red tail light
                        drawCircle(
                            color = Color(0xFFEF4444),
                            radius = 5f * scale,
                            center = Offset(pos.x, topLeft.y + motoH - 8f * scale)
                        )
                    }

                    ObstacleType.POTHOLE -> {
                        // Shimo la barabara (Ellipse)
                        val holeW = 75f * scale
                        val holeH = 30f * scale
                        drawOval(
                            color = Color(0xFF020617), // Deep black
                            topLeft = Offset(pos.x - holeW / 2, pos.y - holeH / 2),
                            size = Size(holeW, holeH)
                        )
                        drawOval(
                            color = Color(0xFF334155),
                            topLeft = Offset(pos.x - holeW / 2 + 4f, pos.y - holeH / 2 + 2f),
                            size = Size(holeW - 8f, holeH - 4f)
                        )
                    }

                    ObstacleType.BARRIER -> {
                        // Kizuizi cha Barabara cha juu (Barrier)
                        val bW = 85f * scale
                        val bH = 45f * scale
                        val topLeft = Offset(pos.x - bW / 2, pos.y - bH)

                        // Stripes za njano na nyeusi
                        drawRect(
                            color = Color(0xFFFACC15),
                            topLeft = topLeft,
                            size = Size(bW, 14f * scale)
                        )
                        // Legs
                        drawRect(
                            color = Color(0xFF94A3B8),
                            topLeft = Offset(topLeft.x + 8f, topLeft.y + 14f * scale),
                            size = Size(6f * scale, bH - 14f * scale)
                        )
                        drawRect(
                            color = Color(0xFF94A3B8),
                            topLeft = Offset(topLeft.x + bW - 14f, topLeft.y + 14f * scale),
                            size = Size(6f * scale, bH - 14f * scale)
                        )
                    }
                }
            }
        }

        // 5. Draw Player Character (Mchezaji anayekimbia)
        val playerPos = getCoordinates(uiState.playerLane, 0.88f)
        val playerYOffset = when (uiState.playerAction) {
            PlayerAction.JUMPING -> -75f  // Anaruka juu
            PlayerAction.SLIDING -> 20f   // Anateleza chini
            PlayerAction.RUNNING -> 0f
        }

        val charCenter = Offset(playerPos.x, playerPos.y + playerYOffset)

        // Shadow under player
        drawOval(
            color = Color.Black.copy(alpha = if (uiState.playerAction == PlayerAction.JUMPING) 0.2f else 0.45f),
            topLeft = Offset(playerPos.x - 24f, playerPos.y + 35f),
            size = Size(48f, 18f)
        )

        if (uiState.playerAction == PlayerAction.SLIDING) {
            // Mchezaji akiwa amejikunja akiteleza (Sliding)
            drawRoundRect(
                color = Color(0xFF10B981), // Green sporty jersey
                topLeft = Offset(charCenter.x - 30f, charCenter.y),
                size = Size(60f, 26f),
                cornerRadius = CornerRadius(10f)
            )
            // Head
            drawCircle(
                color = Color(0xFF78350F), // African skin tone
                radius = 12f,
                center = Offset(charCenter.x + 22f, charCenter.y + 10f)
            )
        } else {
            // Mchezaji akiruka au akimbia (Running / Jumping)
            // Running shoes / legs
            val legSwing = if (uiState.playerAction == PlayerAction.RUNNING) {
                ((uiState.distanceMeters % 4) - 2) * 6f
            } else 0f

            // Left leg
            drawRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(charCenter.x - 14f + legSwing, charCenter.y + 12f),
                size = Size(8f, 28f)
            )
            // Right leg
            drawRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(charCenter.x + 6f - legSwing, charCenter.y + 12f),
                size = Size(8f, 28f)
            )

            // Shoes (Red sneakers)
            drawCircle(
                color = Color(0xFFDC2626),
                radius = 6f,
                center = Offset(charCenter.x - 10f + legSwing, charCenter.y + 40f)
            )
            drawCircle(
                color = Color(0xFFDC2626),
                radius = 6f,
                center = Offset(charCenter.x + 10f - legSwing, charCenter.y + 40f)
            )

            // Torso (Jersey ya Kizalendo ya Tanzania 🇹🇿)
            drawRoundRect(
                color = Color(0xFF0284C7), // Blue jersey
                topLeft = Offset(charCenter.x - 18f, charCenter.y - 20f),
                size = Size(36f, 34f),
                cornerRadius = CornerRadius(6f)
            )
            // Yellow stripe on jersey
            drawRect(
                color = Color(0xFFFACC15),
                topLeft = Offset(charCenter.x - 18f, charCenter.y - 6f),
                size = Size(36f, 6f)
            )

            // Head (African tone)
            drawCircle(
                color = Color(0xFF78350F),
                radius = 14f,
                center = Offset(charCenter.x, charCenter.y - 34f)
            )
            // Hair
            drawCircle(
                color = Color(0xFF0F172A),
                radius = 12f,
                center = Offset(charCenter.x, charCenter.y - 38f)
            )
        }
    }
}
