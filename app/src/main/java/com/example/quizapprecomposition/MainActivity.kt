package com.example.quizapprecomposition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.quizapprecomposition.ui.theme.QuizAppRecompositionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizAppRecompositionTheme {
                QuizApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizApp() {
    val questions = listOf(
        "What is the state is known for the city: Seattle?" to "Washington",
        "What is the capital of New York?" to "Albany",
        "What is the process that turns human blood red?" to "Oxidation",
        "What color is hermit crab blood?" to "Blue",
        "What kpop group has a member named Hanni? (no spaces)" to "NewJeans"
    )

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var userAnswer by remember { mutableStateOf("") }
    var quizCompleted by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Searched up: Coroutine scope to handle side effects in response to button clicks
    val scope = rememberCoroutineScope()

    if (quizCompleted) {
        LaunchedEffect(Unit) {
            snackbarHostState.showSnackbar("Quiz complete! Restarting...")
            quizCompleted = false
            currentQuestionIndex = 0
        }
    }

    val currentQuestion = questions[currentQuestionIndex].first
    val correctAnswer = questions[currentQuestionIndex].second

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Card to display question
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = currentQuestion,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = userAnswer,
                    onValueChange = { userAnswer = it },
                    label = { Text("Your Answer") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(onClick = {
                    scope.launch {
                        if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
                            snackbarHostState.showSnackbar("Correct!", duration = SnackbarDuration.Short)

                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++ // Move to next question
                                // This section takes surprisingly long to do... not sure why
                            } else {
                                quizCompleted = true // Trigger loop of questions
                            }
                        } else {
                            snackbarHostState.showSnackbar("Incorrect. Try again!")
                        }
                        // Clear the input after checking
                        userAnswer = ""
                    }
                }) {
                    Text("Submit Answer")
                }
            }
        }
    )
}