package com.defendend.geoquiz

import androidx.lifecycle.ViewModel

class QuizViewModel : ViewModel() {

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    var currentIndex = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val questionHaveAnswer: Boolean
        get() = hasAnswer[currentIndex]

    val noMoreHints: Boolean
        get() = quantityOfHint >= 3

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    private val isCheaterOnQuestion = BooleanArray(questionBank.size) { false }
    private var hasAnswer = BooleanArray(questionBank.size) { false }
    private var trueAnswers = 0
    private var allAnswersCount = 0
    private var quantityOfHint = 0

    fun numberOfHints(): Int{
        return quantityOfHint
    }

    fun cheating(): BooleanArray {
        return isCheaterOnQuestion
    }

    fun updateCheating(array: BooleanArray) {
        for ((index, isCheating) in array.withIndex()) {
            isCheaterOnQuestion[index] = isCheating
        }
    }

    fun moveToNext() {
        if (currentIndex < questionBank.size - 1) {
            currentIndex = (currentIndex + 1) % questionBank.size
        }
    }

    fun moveToBack() {
        if (currentIndex != 0) {
            currentIndex = (currentIndex - 1) % questionBank.size
        }
    }


    fun useCheat(isCheater: Boolean) {
        isCheaterOnQuestion[currentIndex] = isCheater
        if (isCheater) {
            quantityOfHint++
        }
    }

    fun checkAnswer(userAnswer: Boolean): Int {
        allAnswersCount++
        val messageResId = when {
            isCheater() -> R.string.judgment_toast
            userAnswer == currentQuestionAnswer -> {
                trueAnswers++
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }
        hasAnswer[currentIndex] = true
        return messageResId
    }

    fun isEverythingWasAnswered(): Boolean {
        return allAnswersCount == questionBank.size
    }

    fun calculateResult(): Double {
        return trueAnswers * 100.0 / questionBank.size
    }

    private fun isCheater(): Boolean {
        return isCheaterOnQuestion[currentIndex]
    }

}