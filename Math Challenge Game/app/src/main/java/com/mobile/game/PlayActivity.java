package com.mobile.game;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobile.game.databinding.ActivityPlayBinding;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {
    private ActivityPlayBinding binding;
    private int sec = 0; // Variable to keep track of the total elapsed time in seconds
    private Handler handler = new Handler(); // Handler to manage timing and scheduling
    private int curQuestion = 0; // Index of the current question
    private Runnable runnable;
    private int curCorrect = 0; // Counter for correct answers
    private boolean isReplay = false; // Flag to check if the user has replayed
    private boolean isTimerRunning = true; // Flag to check if the timer is running
    private CountDownTimer countDownTimer; // Countdown timer for each question
    private DatabaseHelper dbHelper; // Database helper instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayBinding.inflate(getLayoutInflater()); // Inflate the layout using View Binding
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this); // Initialize the database helper

        playMusic();
        startTime(); // Start the total timer
        genQuestion(); // Generate the questions
        nextQues(); // Display the first question

        // Set up the DONE button click listener
        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String replay = binding.replay.getText().toString(); // Get the user answer
                if (replay.isEmpty()) {
                    Toast.makeText(PlayActivity.this, "Please input answer first, and then click the next question", Toast.LENGTH_SHORT).show();
                    return;
                }

                isReplay = true; // Mark that the user has replayed
                if (countDownTimer != null) {
                    countDownTimer.cancel(); // Cancel the countdown timer if it is running
                    countDownTimer = null;
                }

                int awaser = list.get(curQuestion).getAwaser(); // Check the user answer
                if (awaser == Integer.parseInt(replay)) {
                    binding.result.setText("CORRECT!"); // If the answer is correct
                    curCorrect++;
                } else {
                    binding.result.setText("WRONG! \n Answer is " + awaser + "!"); // If the answer is wrong, show the correct answer
                }

                stopTotalTimeCounter(); // Stop the total time counter when the user answers the question

                if (curQuestion >= 9) {
                    binding.resultView.setVisibility(View.VISIBLE); // If the current question is the last one, show the total results
                    Log.d("PlayActivity", "curCorrect: " + curCorrect);

                    binding.resulttResult.setText("Correct: " + curCorrect + " Wrong: " + (10 - curCorrect));
                    binding.resulttTime.setText("Time: " + binding.sec.getText());
                    binding.next.setVisibility(View.GONE);
                    binding.continuebtn.setVisibility(View.VISIBLE);
                    binding.finishtask.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.next.setOnClickListener(new View.OnClickListener() { // Next question button click listener
            @Override
            public void onClick(View view) {
                if (isReplay) { // Clear the result and answer fields
                    binding.result.setText("");
                    binding.replay.setText("");

                    curQuestion++; // Move to the next question
                    if (curQuestion > 9) { // If all questions have been answered, show the results
                        binding.next.setVisibility(View.GONE);
                        binding.continuebtn.setVisibility(View.VISIBLE);
                        binding.finishtask.setVisibility(View.VISIBLE);
                        stopTotalTimeCounter(); // Stop total time counter when game finishes
                    } else {
                        nextQues(); // Else, display the next question
                        startIntval(); // Start the interval timer for the next question
                    }
                    startTotalTimeCounter(); // Resume the total time counter
                } else {
                    Toast.makeText(PlayActivity.this, "Please replay", Toast.LENGTH_SHORT).show(); // Show a toast message if the user hasn't answered the current question
                }
            }
        });

        binding.continuebtn.setOnClickListener(new View.OnClickListener() { // Set up the CONTINUE button click listener
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                LocalDateTime now = LocalDateTime.now(); // Record the game result
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedNow = now.format(formatter);

                // Insert game log into SQLite database
                dbHelper.insertGameLog(formattedNow, formattedNow, sec, curCorrect);

                curCorrect = 0; // Reset the game state for a new round
                curQuestion = 0;
                handler.removeCallbacks(runnable);
                resetTimers(); // Reset total timer and countdown timer
                binding.replay.setText(""); // Clear the input field
                binding.result.setText(""); // Clear the result text
                genQuestion(); // Generate new questions for the new round
                nextQues();

                startTotalTimeCounter(); // Start the total timer
                startIntval(); // Start the countdown timer
                binding.next.setVisibility(View.VISIBLE);
                binding.continuebtn.setVisibility(View.GONE);
                binding.finishtask.setVisibility(View.GONE);
            }
        });

        startIntval(); // Start the interval timer for the first question

        // Set up the FINISH button click listener
        binding.finishtask.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                LocalDateTime now = LocalDateTime.now(); // Record the game result and finish the activity
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedNow = now.format(formatter);

                // Insert game log into SQLite database
                dbHelper.insertGameLog(formattedNow, formattedNow, sec, curCorrect);

                finish(); // Finish the activity
            }
        });
    }

    private void startIntval() { // Start the interval timer for each question
        if (countDownTimer != null) { // If there is an existing countdown timer, cancel it and set it to null
            countDownTimer.cancel();
            countDownTimer = null;
        }
        countDownTimer = new CountDownTimer(10000, 1000) { // Create a new CountDownTimer, 10s to 1s
            @Override
            public void onTick(long millisUntilFinished) {
                binding.interveal.setText(millisUntilFinished / 1000 + " sec");
            }

            @Override
            public void onFinish() {
                binding.interveal.setText("0 sec"); // Countdown timer shows 0 sec when finished
                stopTotalTimeCounter(); // Stop the total time counter immediately
                binding.result.setText("WRONG! \n Answer is " + list.get(curQuestion).getAwaser() + "!");
                if (curQuestion < 9) {
                    showAnim(); // Show the animation immediately if it's not the last question
                } else {
                    handleLastQuestion(); // Handle the last question differently
                }
            }
        };

        countDownTimer.start(); // Start the countdown timer
    }

    private void showAnim() { // Show the animation when a question is answered incorrectly
        stopTotalTimeCounter(); // Stop the total time counter

        ImageView animationView = findViewById(R.id.iv);
        animationView.setVisibility(View.VISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) animationView.getDrawable();
        animationDrawable.setOneShot(true);

        if (animationDrawable != null) {
            animationDrawable.start();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                curQuestion++;
                clearFields(); // Clear the fields before moving to the next question
                nextQues(); // Display the next question
                startIntval(); // Restart the interval timer for the next question
                animationView.setVisibility(View.GONE);
                startTotalTimeCounter(); // Resume the total time counter
            }
        }, 3000);
    }

    private void handleLastQuestion() { // Handle the last question differently
        stopTotalTimeCounter(); // Stop the total time counter

        ImageView animationView = findViewById(R.id.iv);
        animationView.setVisibility(View.VISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) animationView.getDrawable();
        animationDrawable.setOneShot(true);

        if (animationDrawable != null) {
            animationDrawable.start();
        }

        // Display the results after the 10th question
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.resultView.setVisibility(View.VISIBLE);
                binding.resulttResult.setText("Correct: " + curCorrect + " Wrong: " + (10 - curCorrect)); // Set the text for the result of correct and wrong answers
                binding.resulttTime.setText("Time: " + binding.sec.getText()); // Set the text for the total time taken
                binding.next.setVisibility(View.GONE); // Hide the next button
                binding.continuebtn.setVisibility(View.VISIBLE); // Show the continue button
                binding.finishtask.setVisibility(View.VISIBLE);
                animationView.setVisibility(View.GONE);
            }
        }, 3000); // Delay execution by 3 seconds
    }

    private void clearFields() { // Clear the result and answer fields
        binding.result.setText(""); // Clear the result text
        binding.replay.setText(""); // Clear the input field
    }

    private MediaPlayer mediaPlayer;

    private void playMusic() { // Play background music
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = getAssets().openFd("music.mp3");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void nextQues() { // Display the next question
        binding.resultView.setVisibility(View.GONE);

        isReplay = false;
        binding.question.setText("Question " + (curQuestion + 1));
        Question question = list.get(curQuestion);
        int number1 = question.getNumber1(); // Extract the numbers and operator from the current question
        int number2 = question.getNumber2();
        String fuhao = question.getFuhao();

        String content = number1 + " " + fuhao + " " + number2 + "= ?"; // Construct the question content string
        binding.content.setText(content); // Set the question content text in the UI
    }

    private List<Question> list; // Store the generated Question objects

    private void genQuestion() {
        String[] fuhaos = {"+", "-", "*", "/"};
        list = new ArrayList<>(); // Initialize the list to store the generated questions

        Random random = new Random(); // Random number generator

        for (int i = 0; i < 10; i++) { // Loop to generate 10 questions
            String fuhao = fuhaos[random.nextInt(4)]; // Randomly select an operator from the fuhaos array
            Question question = new Question(); // Create a new Question object to store the generated question
            int num1 = 0, num2 = 0;

            switch (fuhao) {
                case "+":
                    num1 = random.nextInt(100) + 1; // Random number between 1 and 100
                    num2 = random.nextInt(100) + 1; // Random number between 1 and 100
                    break;
                case "-":
                    num1 = random.nextInt(100) + 1; // Random number between 1 and 100
                    num2 = random.nextInt(num1) + 1; // Random number between 1 and num1 to ensure non-negative result
                    break;
                case "*":
                    num1 = random.nextInt(100) + 1; // Random number between 1 and 100
                    num2 = random.nextInt(100) + 1; // Random number between 1 and 100
                    break;
                case "/":
                    do {
                        num1 = random.nextInt(100) + 1; // Random number between 1 and 100
                        num2 = random.nextInt(100) + 1; // Random number between 1 and 100
                    } while (num2 == 0 || num1 % num2 != 0); // Valid division to ensure integer result
                    break;
            }

            int result;
            switch (fuhao) { // Handle different operators and generate appropriate operands (calculate the result)
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    result = num1 / num2;
                    break;
                default:
                    result = 0;
                    break;
            }

            question.setNumber1(num1);
            question.setNumber2(num2);
            question.setFuhao(fuhao);
            question.setAwaser(result);
            list.add(question);
        }
    }


    /*
    private void genQuestion() { // generate a list of questions
        String[] fuhaos = {"+", "-", "*", "/"};
        list = new ArrayList<>();// initialize the list store the generated questions.

        for (int i = 0; i < 10; i++) {//Loop to generate 10 questions.
            Random random = new Random();// random number generator
            String fuhao = fuhaos[random.nextInt(4)];//randomly selects an operator from the fuhaos array.
            Question question = new Question();//creates a new Question object to store the generated question.
            int num1, num2;

            switch (fuhao) {
                case "+":
                    do {
                        num1 = random.nextInt(201) - 100; // Random number between -100 and 100
                        num2 = random.nextInt(201) - 100; // Random number between -100 and 100
                    } while (num1 + num2 <= 0); // makesure the result is pos
                    break;
                case "-":
                    boolean useNegatives = random.nextBoolean();
                    if (useNegatives) {
                        do {
                            num1 = random.nextInt(201) - 100; // Random number between -100 and 100
                            num2 = random.nextInt(201) - 100; // Random number between -100 and 100
                        } while (num1 - num2 <= 0); // mkaesure the result is positive
                    } else {
                        num1 = random.nextInt(100) + 1; // Random number between 1 and 100
                        num2 = random.nextInt(num1) + 1; // Random number between 1 and num1
                    }
                    break;
                case "*":
                    num1 = random.nextInt(100) + 1; // Random number between 1 and 100 (pos)
                    num2 = random.nextInt(100) + 1; // Random number between 1 and 100 (pos)
                    break;
                case "/":
                    do {
                        num1 = random.nextInt(201) - 100; // Random number between -100 and 100
                        num2 = random.nextInt(201) - 100; // Random number between -100 and 100
                    } while (num2 == 0 || num1 % num2 != 0 || num1 / num2 <= 0); // valid division and pos result
                    break;
                default:
                    num1 = random.nextInt(100) + 1;
                    num2 = random.nextInt(100) + 1;
                    break;
            }

            int result;
            switch (fuhao) {//handle different operators and generate appropriate operands(calculate the Result )
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    result = num1 / num2;
                    break;
                default:
                    result = 0;
                    break;
            }

            question.setNumber1(num1);
            question.setNumber2(num2);
            question.setFuhao(fuhao);
            question.setAwaser(result);
            list.add(question);
        }
    }

     */

    private void startTime() { // Start the total time counter
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isTimerRunning) {
                    sec++;
                    binding.sec.setText(sec + " sec");
                    handler.postDelayed(this, 1000); // Ensure the runnable schedules itself
                }
            }
        };
        handler.post(runnable); // Make sure the runnable is posted immediately
    }

    private void resetTimers() { // Reset the timers
        sec = 0;
        isTimerRunning = true;
        binding.sec.setText("1 sec");
    }

    private void startTotalTimeCounter() { // Start the total time counter
        isTimerRunning = true;
        handler.post(runnable); // Ensure the runnable is posted immediately
    }

    private void stopTotalTimeCounter() { // Stop the total time counter
        isTimerRunning = false;
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) { // Cancels the timer to prevent it from continuing to run after the activity is destroyed
            countDownTimer.cancel();
        }
        if (mediaPlayer != null) { // Stops the media player
            mediaPlayer.stop();
        }
        if (handler != null && runnable != null) { // Removes callbacks to prevent memory leaks
            handler.removeCallbacks(runnable);
        }
    }
}