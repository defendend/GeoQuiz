package com.defendend.geoquiz

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val KEY_INDEX = "index"
private const val CHEAT = "cheat"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var quantityOfHint: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        savedInstanceState.putBooleanArray(CHEAT, quizViewModel.cheating())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        val cheating = savedInstanceState?.getBooleanArray(CHEAT) ?: BooleanArray(5) { false }
        quizViewModel.updateCheating(cheating)

        trueButton = findViewById(R.id.trueButton)
        falseButton = findViewById(R.id.falseButton)
        nextButton = findViewById(R.id.nextButton)
        backButton = findViewById(R.id.backButton)
        cheatButton = findViewById(R.id.cheatButton)
        questionTextView = findViewById(R.id.questionTextView)
        quantityOfHint = findViewById(R.id.quantityOfHint)

        quantityOfHint.text = getString(R.string.quantity_of_hints, quizViewModel.numberOfHints())

        trueButton.setOnClickListener {
            checkAnswer(true)
            checkHasAnswer()
        }

        falseButton.setOnClickListener {
            checkAnswer(false)
            checkHasAnswer()
        }

        cheatButton.setOnClickListener { view ->
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            val options = ActivityOptions
                .makeClipRevealAnimation(view, 0, 0, view.width, view.height)
            startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
        }

        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            checkHasAnswer()
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            checkHasAnswer()
        }

        backButton.setOnClickListener {
            quizViewModel.moveToBack()
            updateQuestion()
            checkHasAnswer()
        }

        updateQuestion()
        checkHasAnswer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.useCheat(data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false)
        }
        quantityOfHint.text = getString(R.string.quantity_of_hints, quizViewModel.numberOfHints())
    }

    private fun updateQuestion() {
        val correctAnswer = quizViewModel.currentQuestionText
        questionTextView.setText(correctAnswer)
    }

    private fun checkHasAnswer() {
        if (quizViewModel.questionHaveAnswer) {
            trueButton.isClickable = false
            falseButton.isClickable = false
            if (quizViewModel.noMoreHints){
                cheatButton.isClickable = false
            }
        } else {
            trueButton.isClickable = true
            falseButton.isClickable = true
            if (quizViewModel.noMoreHints){
                cheatButton.isClickable = false
            }
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val messageResId = quizViewModel.checkAnswer(userAnswer)
        Toast.makeText(
            this,
            messageResId,
            Toast.LENGTH_SHORT
        ).apply {
            setGravity(Gravity.TOP, 0, 0)
            show()
        }
        if (quizViewModel.isEverythingWasAnswered()) {
            val math = quizViewModel.calculateResult()
            val message = getString(R.string.results, math)
            Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
            ).show()
        }
    }


}