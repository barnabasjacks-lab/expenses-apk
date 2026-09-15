package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dukaplus.ui.DukaPlusMainScreen
import com.example.dukaplus.viewmodel.DukaPlusViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dukaViewModel: DukaPlusViewModel = viewModel()
            val isDarkMode by dukaViewModel.isDarkMode.collectAsState()

            MyApplicationTheme(themeMode = if (isDarkMode) ThemeMode.DARK else ThemeMode.LIGHT) {
                DukaPlusMainScreen(viewModel = dukaViewModel)
            }
        }
    }
}

