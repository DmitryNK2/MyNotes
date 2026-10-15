package com.example.mynotes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mynotes.data.preferences.ThemeOption
import com.example.mynotes.data.preferences.ThemePreferences
import com.example.mynotes.data.repository.NotesRepository
import com.example.mynotes.ui.screens.ChatScreen
import com.example.mynotes.ui.screens.SettingsScreen
import com.example.mynotes.ui.screens.SubTopicsScreen
import com.example.mynotes.ui.screens.TopicsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: NotesRepository,
    themePreferences: ThemePreferences,
    coroutineScope: CoroutineScope
) {
    val currentTheme by themePreferences.selectedTheme.collectAsState(initial = ThemeOption.SYSTEM_DEFAULT)

    NavHost(navController = navController, startDestination = Screen.Topics.route) {

        composable(Screen.Topics.route) {
            TopicsScreen(
                repository = repository,
                onTopicClick = { topicId ->
                    navController.navigate(Screen.SubTopics.createRoute(topicId))
                },
                onDirectChatClick = { subTopicId ->
                    navController.navigate(Screen.Chat.createRoute(subTopicId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.SubTopics.route,
            arguments = listOf(navArgument("topicId") { type = NavType.LongType })
        ) { backStackEntry ->
            val topicId = backStackEntry.arguments?.getLong("topicId") ?: return@composable
            SubTopicsScreen(
                topicId = topicId,
                repository = repository,
                onSubTopicClick = { subTopicId ->
                    navController.navigate(Screen.Chat.createRoute(subTopicId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("subTopicId") { type = NavType.LongType })
        ) { backStackEntry ->
            val subTopicId = backStackEntry.arguments?.getLong("subTopicId") ?: return@composable
            ChatScreen(
                subTopicId = subTopicId,
                repository = repository,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                currentTheme = currentTheme,
                onThemeSelected = { theme ->
                    coroutineScope.launch {
                        themePreferences.setSelectedTheme(theme)
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}