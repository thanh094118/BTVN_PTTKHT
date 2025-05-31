package com.example.myapplication.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen() {

    val coroutineScope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPage(R.drawable.onboarding1, "Thanh's Application", "Bui Ngoc Thanh 20225399"),
        OnboardingPage(R.drawable.onboarding2, "Online shopping", ""),
        OnboardingPage(R.drawable.onboarding3, "Let's get started!", "")
    )
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            val item = pages[page]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(painter = painterResource(id = item.image), contentDescription = null)
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = item.title, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Custom indicator
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            repeat(pages.size) { index ->
                val color = if (pagerState.currentPage == index) Color.Black else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(10.dp)
                        .background(color, shape = CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = {
                coroutineScope.launch { pagerState.scrollToPage(pages.size - 1) }
            }) {
                Text("Skip")
            }

            Button(onClick = {
                coroutineScope.launch {
                    val nextPage = if (pagerState.currentPage + 1 < pages.size)
                        pagerState.currentPage + 1 else 0
                    pagerState.animateScrollToPage(nextPage)
                }
            }) {
                Text(if (pagerState.currentPage == pages.size - 1) "Finish" else "Next")
            }
        }
    }
}

data class OnboardingPage(val image: Int, val title: String, val description: String)
