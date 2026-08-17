package com.example.data.repository

import com.example.data.local.PromptDao
import com.example.data.model.TutorialItem
import kotlinx.coroutines.flow.Flow

class TutorialRepository(
    private val promptDao: PromptDao
) {
    val allTutorials: Flow<List<TutorialItem>> = promptDao.getAllTutorials()

    suspend fun addTutorial(tutorial: TutorialItem) {
        promptDao.insertTutorials(listOf(tutorial))
    }
}
