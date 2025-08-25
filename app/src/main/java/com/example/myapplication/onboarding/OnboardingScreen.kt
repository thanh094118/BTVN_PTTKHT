@file:OptIn(ExperimentalFoundationApi::class)

package com.example.myapplication.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.R
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen() {
    val images = listOf(
        R.drawable.onboarding1,  // page0
        R.drawable.onboarding1,  // page1
        R.drawable.onboarding2,  // page2
    )

    val totalPages = images.size
    val imageScale = remember { Animatable(1.5f) }
    val pageIndex = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Thêm các biến cho animation2 (page1 -> page2)
    val slideOffset = remember { Animatable(0f) }
    val isTransitioning = remember { mutableStateOf(false) }

    // Thêm biến cho floating icons animation
    val iconOffset = remember { Animatable(0f) }
    val iconAlpha = remember { Animatable(1f) }
    val iconScaleBack = remember { Animatable(0f) } // Animation cho vuốt page1 -> page0

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            val current = imageScale.value
                            val slideValue = slideOffset.value

                            when (pageIndex.value) {
                                // Animation1: page0 <-> page1 (zoom animation)
                                0 -> {
                                    // Page0: vuốt trái->phải (giảm scale) để sang page1
                                    if (current < 1f) {
                                        coroutineScope.launch {
                                            // Reverse animation từ page0 -> page1
                                            launch { iconOffset.animateTo(-1f, tween(300)) } // Icons bay vào từ trái
                                            launch { iconAlpha.animateTo(1f, tween(300)) }
                                            launch { iconScaleBack.animateTo(-1f, tween(300)) } // Reverse effect
                                            // Main animation
                                            pageIndex.value = 1
                                            imageScale.animateTo(0.4f, tween(300))
                                            // Reset icons cho lần sau
                                            iconOffset.snapTo(0f)
                                            iconAlpha.snapTo(1f)
                                            iconScaleBack.snapTo(0f)
                                        }
                                    } else {
                                        imageScale.animateTo(1.5f, tween(300))
                                    }
                                }
                                1 -> {
                                    when {
                                        // Animation1: page1 -> page0 (vuốt trái, tăng scale)
                                        current > 0.6f -> {
                                            coroutineScope.launch {
                                                // Icons animation khi chuyển về page0
                                                launch { iconOffset.animateTo(1f, tween(300)) }
                                                launch { iconAlpha.animateTo(0f, tween(300)) }
                                                launch { iconScaleBack.animateTo(1f, tween(300)) }
                                                // Main animation
                                                pageIndex.value = 0
                                                imageScale.animateTo(1.5f, tween(300))
                                                // Reset icons cho lần sau
                                                iconOffset.snapTo(0f)
                                                iconAlpha.snapTo(1f)
                                                iconScaleBack.snapTo(0f)
                                            }
                                        }
                                        // Animation2: page1 -> page2 (vuốt phải, slide)
                                        slideValue > 0.3f -> {
                                            coroutineScope.launch {
                                                isTransitioning.value = true
                                                slideOffset.animateTo(1f, tween(300))
                                                pageIndex.value = 2
                                                // Reset scale cho page2
                                                imageScale.snapTo(0.4f)
                                                slideOffset.snapTo(0f)
                                                isTransitioning.value = false
                                            }
                                        }
                                        else -> {
                                            // Reset về vị trí ban đầu
                                            coroutineScope.launch {
                                                launch { slideOffset.animateTo(0f, tween(200)) }
                                                launch { imageScale.animateTo(0.4f, tween(200)) }
                                                // Reset icons về trạng thái bình thường
                                                launch { iconOffset.animateTo(0f, tween(200)) }
                                                launch { iconAlpha.animateTo(1f, tween(200)) }
                                                launch { iconScaleBack.animateTo(0f, tween(200)) }
                                            }
                                        }
                                    }
                                }
                                2 -> {
                                    // Animation2: page2 -> page1 (vuốt trái, slide back)
                                    if (slideValue < -0.3f) {
                                        coroutineScope.launch {
                                            isTransitioning.value = true
                                            // Animation icons từ ngoài vào khi page2 -> page1
                                            launch { iconOffset.animateTo(-1f, tween(300)) } // Icons bay vào từ trái (chạy từ ngoài)
                                            launch { iconAlpha.animateTo(1f, tween(300)) }
                                            launch { iconScaleBack.animateTo(-1f, tween(300)) }
                                            // Main animation
                                            slideOffset.animateTo(-1f, tween(300))
                                            pageIndex.value = 1
                                            imageScale.snapTo(0.4f)
                                            slideOffset.snapTo(0f)
                                            isTransitioning.value = false
                                            // Reset icons
                                            iconOffset.snapTo(0f)
                                            iconAlpha.snapTo(1f)
                                            iconScaleBack.snapTo(0f)
                                        }
                                    } else {
                                        slideOffset.animateTo(0f, tween(200))
                                    }
                                }
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        coroutineScope.launch {
                            when (pageIndex.value) {
                                // Animation1: page0 <-> page1 (scale drag)
                                0 -> {
                                    // Page0: vuốt trái->phải để sang page1
                                    val delta = -dragAmount.x / 1000f
                                    val newScale = (imageScale.value + delta).coerceIn(0.4f, 1.5f)
                                    imageScale.snapTo(newScale)

                                    // Animation từ page0 -> page1 (reverse)
                                    if (newScale < 1f) {
                                        val iconProgress = (1.5f - newScale) / (1.5f - 0.4f)
                                        iconOffset.snapTo(-iconProgress) // Ngược hướng
                                        iconAlpha.snapTo(0.5f + iconProgress * 0.5f) // Fade in
                                        iconScaleBack.snapTo(-iconProgress) // Reverse scale
                                    }
                                }
                                1 -> {
                                    if (dragAmount.x > 50) {
                                        // Vuốt phải -> Animation2 (page1 -> page2)
                                        val delta = dragAmount.x / 1000f
                                        val newOffset = (slideOffset.value + delta).coerceIn(0f, 1f)
                                        slideOffset.snapTo(newOffset)
                                    } else {
                                        // Vuốt trái -> Animation1 (page1 -> page0)
                                        val delta = -dragAmount.x / 1000f
                                        val newScale = (imageScale.value + delta).coerceIn(0.4f, 1.5f)
                                        imageScale.snapTo(newScale)
                                        // Animation cho iconScaleBack khi vuốt trái
                                        val scaleBackValue = ((newScale - 0.4f) / (1.5f - 0.4f)).coerceIn(0f, 1f)
                                        iconScaleBack.snapTo(scaleBackValue)
                                    }
                                }
                                // Animation2: page2 slide back
                                2 -> {
                                    // Page2: vuốt trái->phải để về page1
                                    val delta = -dragAmount.x / 1000f
                                    val newOffset = (slideOffset.value + delta).coerceIn(-1f, 0f)
                                    slideOffset.snapTo(newOffset)

                                    // Animation icons từ ngoài vào khi drag page2 -> page1
                                    if (newOffset < -0.1f) {
                                        val progress = (-newOffset).coerceIn(0f, 1f)
                                        iconOffset.snapTo(-progress) // Icons chạy từ ngoài vào (từ trái)
                                        iconAlpha.snapTo(progress) // Fade in
                                        iconScaleBack.snapTo(-progress) // Scale effect
                                    }
                                }
                            }
                        }
                    }
                )
            }
    ) {
        // Hiển thị hình ảnh với animations
        if (pageIndex.value == 2 && !isTransitioning.value) {
            // Page 2: Hiển thị GifPlayer with scale 0.4f
            GifPlayer(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(0.4f)
                    .graphicsLayer {
                        translationX = slideOffset.value * size.width * 1.5f
                    },
                resId = R.drawable.funny
            )
        } else {
            // Page 0, 1 và transition states
            Box(modifier = Modifier.fillMaxSize()) {
                // Hình chính (onboarding1)
                Image(
                    painter = painterResource(id = images[pageIndex.value]),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(imageScale.value)
                        .graphicsLayer {
                            translationX = slideOffset.value * size.width * 1.5f
                            alpha = if (pageIndex.value == 1 && slideOffset.value > 0.1f) {
                                1f - slideOffset.value // Fade out khi slide phải
                            } else 1f
                        }
                )

                // Hình mới (funny) slide vào từ bên phải khi ở page1
                if (pageIndex.value == 1 && slideOffset.value > 0f) {
                    GifPlayer(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(0.4f)
                            .graphicsLayer {
                                translationX = slideOffset.value * size.width * 1.5f
                                alpha = slideOffset.value // Fade in khi slide vào
                            },
                        resId = R.drawable.funny
                    )
                }

                // Hình cũ (onboarding1) khi đang slide back từ page2
                if (pageIndex.value == 2 && slideOffset.value < 0f) {
                    Image(
                        painter = painterResource(id = R.drawable.onboarding1),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(0.4f)
                            .graphicsLayer {
                                translationX = (slideOffset.value + 1f) * size.width * 1.5f
                                alpha = -slideOffset.value
                            }
                    )
                }
            }
        }

        // Fixed Icons cho ảnh funny (R.drawable.item) - hiển thị khi ở page 2
        if (pageIndex.value == 2) {
            FixedIcons()
        }

        // Floating Icons - hiển thị ở tất cả các trạng thái transition
        if (pageIndex.value == 1 ||
            (pageIndex.value == 0 && imageScale.value < 1f) ||
            (pageIndex.value == 2 && slideOffset.value < -0.1f)) {
            FloatingIcons(
                iconOffset = iconOffset.value,
                iconAlpha = iconAlpha.value,
                slideOffset = slideOffset.value,
                imageScale = imageScale.value,
                iconScaleBack = iconScaleBack.value
            )
        }

        // UI controls (giữ nguyên)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            Spacer(modifier = Modifier.weight(1f))

            // Buttons
            when (pageIndex.value) {
                0 -> {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                pageIndex.value = 2
                                imageScale.animateTo(1f, animationSpec = tween(400))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(60.dp)
                    ) {
                        Text("Sign up", fontSize = 18.sp)
                    }
                }

                1 -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            coroutineScope.launch {
                                pageIndex.value = 2
                                imageScale.animateTo(1f, animationSpec = tween(400))
                            }
                        }) {
                            Text("Sign up")
                        }
                        OutlinedButton(onClick = {
                            coroutineScope.launch {
                                pageIndex.value = 0
                                imageScale.animateTo(1.5f, animationSpec = tween(300))
                            }
                        }) {
                            Text("Return")
                        }
                    }
                }

                2 -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            coroutineScope.launch {
                                pageIndex.value = 2
                                imageScale.animateTo(1f, animationSpec = tween(400))
                            }
                        }) {
                            Text("Sign up")
                        }
                        OutlinedButton(onClick = {
                            coroutineScope.launch {
                                pageIndex.value = 1
                                imageScale.animateTo(0.4f, animationSpec = tween(300))
                            }
                        }) {
                            Text("Return")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FixedIcons() {
    // 6 vị trí cố định cho ảnh funny (3 cạnh + 3 góc) - tính theo scale 0.4f
    val fixedIconPositions = listOf(
        // 3 cạnh
        Pair(0.5f, 0.15f),   // Top center
        Pair(0.85f, 0.5f),   // Right center
        Pair(0.5f, 0.85f),   // Bottom center
        // 3 góc
        Pair(0.2f, 0.2f),    // Top left corner
        Pair(0.8f, 0.2f),    // Top right corner
        Pair(0.2f, 0.8f)     // Bottom left corner
    )

    fixedIconPositions.forEach { (x, y) ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = x * size.width
                    translationY = y * size.height
                }
        ) {
            // Icon từ R.drawable.item
        }
    }
}

@Composable
fun FloatingIcons(
    iconOffset: Float,
    iconAlpha: Float,
    slideOffset: Float,
    imageScale: Float,
    iconScaleBack: Float
) {
    // Vị trí các icons: 3 icon trên cạnh, 2 icon ở góc (tính theo scale 0.4f của onboarding1)
    val iconPositions = listOf(
        Pair(0.5f, 0.2f),   // Top center (cạnh trên)
        Pair(0.8f, 0.35f),  // Right center (cạnh phải)
        Pair(0.5f, 0.8f),   // Bottom center (cạnh dưới)
        Pair(0.3f, 0.3f),   // Top left corner (góc trên trái)
        Pair(0.7f, 0.7f)    // Bottom right corner (góc dưới phải)
    )

    iconPositions.forEachIndexed { index, (x, y) ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Tính toán vị trí icon
                    val baseX = x * size.width
                    val baseY = y * size.height

                    // Tất cả icons di chuyển cùng một hướng (sang phải)
                    val unifiedDirection = 1f
                    val speedMultiplier = 1.5f

                    // Animation chạy từ ngoài vào và dần dừng lại khi vào page1
                    val dynamicOffsetX = when {
                        // Trạng thái 1: Di chuyển theo hướng vuốt khi đang kéo (page1 -> page2)
                        slideOffset > 0f -> {
                            // Icons di chuyển cùng hướng với slide (sang phải)
                            slideOffset * size.width * 1.2f * speedMultiplier
                        }
                        // Trạng thái 2: Di chuyển khi chuyển page (page1 -> page0)
                        imageScale > 0.7f -> {
                            // Icons bay theo hướng thống nhất
                            iconOffset * size.width * 0.4f * unifiedDirection * speedMultiplier
                        }
                        // Trạng thái 3: Animation từ ngoài vào (page0/page2 -> page1)
                        iconOffset < 0f -> {
                            // Icons chạy từ ngoài vào với hiệu ứng dần dừng (easing)
                            val progress = kotlin.math.abs(iconOffset) // 0 -> 1
                            val easingProgress = 1f - (1f - progress) * (1f - progress) // Quadratic ease-out
                            val startDistance = size.width * 1.5f // Khoảng cách bắt đầu từ ngoài màn hình

                            // Icons bay vào từ trái với hiệu ứng dần chậm lại
                            -startDistance * (1f - easingProgress)
                        }
                        iconOffset > 0f && iconOffset != iconScaleBack -> {
                            // Animation từ ngoài vào (page2 -> page1) - FIXED
                            val progress = iconOffset // 0 -> 1
                            val easingProgress = 1f - (1f - progress) * (1f - progress) // Quadratic ease-out
                            val startDistance = size.width * 1.5f // Khoảng cách bắt đầu từ ngoài màn hình

                            // Icons bay vào từ trái với hiệu ứng dần chậm lại (thay vì từ phải)
                            -startDistance * (1f - easingProgress)
                        }
                        // Trạng thái bình thường
                        else -> 0f
                    }

                    // Animation thêm với hiệu ứng bounce khi có scale back effect
                    val scaleBackAnimation = when {
                        iconScaleBack != 0f && kotlin.math.abs(iconScaleBack) != kotlin.math.abs(iconOffset) -> {
                            // Hiệu ứng xoay và scale đồng bộ cho tất cả icons
                            val rotationEffect = iconScaleBack * 30f // Giảm rotation để mượt hơn
                            val scaleEffect = 1f + (kotlin.math.abs(iconScaleBack) * 0.2f) // Giảm scale effect

                            // Thêm hiệu ứng bounce nhẹ
                            val bounceEffect = kotlin.math.sin(kotlin.math.abs(iconScaleBack) * kotlin.math.PI.toFloat()) * 0.1f

                            rotationZ = rotationEffect
                            scaleX = scaleEffect + bounceEffect
                            scaleY = scaleEffect + bounceEffect

                            // Bounce effect đồng bộ nhẹ hơn
                            iconScaleBack * size.width * 0.15f * unifiedDirection
                        }
                        else -> {
                            rotationZ = 0f
                            scaleX = 1f
                            scaleY = 1f
                            0f
                        }
                    }

                    translationX = baseX + dynamicOffsetX + scaleBackAnimation
                    translationY = baseY

                    // Alpha với hiệu ứng fade mượt mà
                    alpha = when {
                        // Biến mất khi slide sang page2
                        slideOffset > 0.4f -> kotlin.math.max(0f, 1f - ((slideOffset - 0.4f) / 0.6f))
                        // Biến mất khi chuyển về page0
                        imageScale > 0.7f -> kotlin.math.max(0f, iconAlpha)
                        // Fade in từ page0/page2 về page1 với easing
                        iconOffset < 0f -> {
                            val progress = kotlin.math.abs(iconOffset)
                            val easingAlpha = progress * progress // Quadratic ease-in cho alpha
                            kotlin.math.min(1f, easingAlpha)
                        }
                        iconOffset > 0f && iconOffset != iconScaleBack -> {
                            val progress = iconOffset
                            val easingAlpha = progress * progress // Quadratic ease-in cho alpha
                            kotlin.math.min(1f, easingAlpha)
                        }
                        // Hiển thị bình thường
                        else -> 1f
                    }
                }
        ) {
            // Icon chat bubble lớn hơn 0.5 lần (45dp thay vì 30dp)
            Box(
                modifier = Modifier
                    .size(45.dp) // Tăng từ 30dp lên 45dp (tăng 0.5 lần)
                    .clip(CircleShape)
                    .background(Color(0xFFE91E63).copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💬",
                    fontSize = 18.sp, // Tăng font size tương ứng từ 12sp lên 18sp
                    color = Color.White
                )
            }
        }
    }
}