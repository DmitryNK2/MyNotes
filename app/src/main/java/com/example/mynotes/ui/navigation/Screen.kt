package com.example.mynotes.ui.navigation

sealed class Screen(val route: String) {
    object Topics : Screen("topics")
    object SubTopics : Screen("sub_topics/{topicId}") {
        fun createRoute(topicId: Long) = "sub_topics/$topicId"
    }
    object Chat : Screen("chat/{subTopicId}") {
        fun createRoute(subTopicId: Long) = "chat/$subTopicId"
    }
    object Settings : Screen("settings")
}