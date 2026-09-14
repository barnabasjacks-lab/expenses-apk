package com.example.chinese.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.chinese.data.ChineseCategory
import com.example.chinese.data.ChineseLessonData
import com.example.chinese.data.ChinesePhrase
import com.example.chinese.util.ChineseTtsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppTab {
    LESSONS,
    FLASHCARDS,
    QUIZ,
    ABOUT
}

data class QuizQuestion(
    val phrase: ChinesePhrase,
    val options: List<String>,
    val correctAnswer: String
)

data class ChineseUiState(
    val currentTab: AppTab = AppTab.LESSONS,
    val selectedCategory: ChineseCategory = ChineseLessonData.categories.first(),
    val searchQuery: String = "",
    val favoritePhraseIds: Set<String> = emptySet(),
    // Flashcard state
    val flashcardIndex: Int = 0,
    val isFlashcardFlipped: Boolean = false,
    // Quiz state
    val quizScore: Int = 0,
    val quizCurrentQuestionIndex: Int = 0,
    val quizSelectedOption: String? = null,
    val isQuizAnswerSubmitted: Boolean = false
)

class ChineseViewModel(application: Application) : AndroidViewModel(application) {
    private val ttsManager = ChineseTtsManager(application)

    private val _uiState = MutableStateFlow(ChineseUiState())
    val uiState: StateFlow<ChineseUiState> = _uiState.asStateFlow()

    fun setTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectCategory(category: ChineseCategory) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            flashcardIndex = 0,
            isFlashcardFlipped = false
        )
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleFavorite(phraseId: String) {
        val currentFavs = _uiState.value.favoritePhraseIds.toMutableSet()
        if (currentFavs.contains(phraseId)) {
            currentFavs.remove(phraseId)
        } else {
            currentFavs.add(phraseId)
        }
        _uiState.value = _uiState.value.copy(favoritePhraseIds = currentFavs)
    }

    fun playChineseAudio(text: String) {
        ttsManager.speakChinese(text)
    }

    // Flashcard controls
    fun nextFlashcard(totalCount: Int) {
        if (totalCount <= 0) return
        val nextIdx = (_uiState.value.flashcardIndex + 1) % totalCount
        _uiState.value = _uiState.value.copy(flashcardIndex = nextIdx, isFlashcardFlipped = false)
    }

    fun prevFlashcard(totalCount: Int) {
        if (totalCount <= 0) return
        val prevIdx = if (_uiState.value.flashcardIndex - 1 < 0) totalCount - 1 else _uiState.value.flashcardIndex - 1
        _uiState.value = _uiState.value.copy(flashcardIndex = prevIdx, isFlashcardFlipped = false)
    }

    fun flipFlashcard() {
        _uiState.value = _uiState.value.copy(isFlashcardFlipped = !_uiState.value.isFlashcardFlipped)
    }

    // Quiz controls
    fun selectQuizOption(option: String) {
        if (_uiState.value.isQuizAnswerSubmitted) return
        _uiState.value = _uiState.value.copy(quizSelectedOption = option)
    }

    fun submitQuizAnswer(correctAnswer: String) {
        val selected = _uiState.value.quizSelectedOption ?: return
        val isCorrect = selected == correctAnswer
        val newScore = if (isCorrect) _uiState.value.quizScore + 10 else _uiState.value.quizScore
        _uiState.value = _uiState.value.copy(
            isQuizAnswerSubmitted = true,
            quizScore = newScore
        )
    }

    fun nextQuizQuestion(totalQuestions: Int) {
        val nextIndex = (_uiState.value.quizCurrentQuestionIndex + 1) % totalQuestions
        _uiState.value = _uiState.value.copy(
            quizCurrentQuestionIndex = nextIndex,
            quizSelectedOption = null,
            isQuizAnswerSubmitted = false
        )
    }

    fun resetQuiz() {
        _uiState.value = _uiState.value.copy(
            quizScore = 0,
            quizCurrentQuestionIndex = 0,
            quizSelectedOption = null,
            isQuizAnswerSubmitted = false
        )
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
