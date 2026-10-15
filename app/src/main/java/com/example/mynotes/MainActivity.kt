package com.example.mynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mynotes.data.preferences.ThemeOption
import com.example.mynotes.data.preferences.ThemePreferences
import com.example.mynotes.ui.navigation.NavGraph
import com.example.mynotes.ui.theme.MyNotesTheme

class MainActivity : ComponentActivity() {
    private lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        themePreferences = ThemePreferences(this)

        setContent {
            val currentTheme by themePreferences.selectedTheme.collectAsState(initial = ThemeOption.SYSTEM_DEFAULT)
            val coroutineScope = rememberCoroutineScope()

            MyNotesTheme(
                dynamicColor = currentTheme == ThemeOption.SYSTEM_DEFAULT,
                themeOption = currentTheme
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val navController = rememberNavController()
                    val app = application as NotesApplication
                    NavGraph(
                        navController = navController,
                        repository = app.repository,
                        themePreferences = themePreferences,
                        coroutineScope = coroutineScope
                    )
                }
            }
        }
    }
}