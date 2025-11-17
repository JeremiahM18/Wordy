package com.example.wordy;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WordyActivity extends AppCompatActivity {

    private LinearLayout boardContainer;
    private EditText etGuess;

    private Button btnAddWord;
    private Button btnSubmit;
    private Button btnClear;
    private Button btnRestart;

    // 6 rows X 5 columns
    private TextView[][] letterBoxes = new TextView[6][5];

    private WordRepository repository;
    private String currentWord = "";
    private int currentRow = 0;
    private boolean gameOver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.wordy_activity);

        // UI references
        boardContainer = findViewById(R.id.boardContainer);
        etGuess = findViewById(R.id.etGuess);
        btnAddWord = findViewById(R.id.btnAddWord);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnClear = findViewById(R.id.btnClear);
        btnRestart = findViewById(R.id.btnRestart);

        repository = new WordRepository();

        // Navigate to AddWordActivity
        btnAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WordyActivity.this, AddWordActivity.class);
                startActivity(intent);
            }
        });

        // Implement later with game logic
        btnSubmit.setOnClickListener(v -> {
            handleSubmit();
        });

        btnClear.setOnClickListener(v -> {
            etGuess.setText("");
        });

        btnRestart.setOnClickListener(v -> {
            restartGame();
        });

        // Build the 6X5 board
        createBoard();
        loadNewWord();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void createBoard(){
        int rows = 6;
        int cols = 5;

        for(int row = 0; row < rows; row++) {
            // Create a horizontal row layout
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = dpToPx(8);
            rowLayout.setLayoutParams(rowParams);

            // Create 5 boxes for this row
            for (int col = 0; col < cols; col++) {
                TextView box = new TextView(this);

                LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(dpToPx(45), dpToPx(45));
                boxParams.leftMargin = dpToPx(4);
                boxParams.rightMargin = dpToPx(4);
                box.setLayoutParams(boxParams);

                box.setGravity(Gravity.CENTER);
                box.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                box.setText("");    // will fill with guessed letter

                styleEmptyBox(box);

                // Store reference
                letterBoxes[row][col] = box;

                // Add box to row
                rowLayout.addView(box);
            }

            // Add row to the vertical board container
            boardContainer.addView(rowLayout);
        }
    }

    private void styleEmptyBox(TextView box) {
//        box.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
//        box.setTextColor(ContextCompat.getColor(this, R.color.black));
        box.setBackgroundResource(R.drawable.tile_boarder);
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics()));
    }

    // Game Logic
    private void loadNewWord() {
        repository.getRandomWord(new WordRepository.RandomWordCallback() {
            @Override
            public void onSuccess(String word) {
                currentWord = word.toUpperCase();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(WordyActivity.this,
                        "Failed to load word: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSubmit() {
        if(gameOver) {
            Toast.makeText(this,
                    "Game over. Restart to play again.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentWord == null || currentWord.isEmpty()) {
            Toast.makeText(this,
                    "No word loaded yet. Try again in a moment.", Toast.LENGTH_SHORT).show();
            return;
        }

        String guess = etGuess.getText().toString().trim().toUpperCase();

        if(guess.length() != 5) {
            Toast.makeText(this,
                    "Please enter a 5-letter word.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!guess.matches("[A-Z]+")) {
            Toast.makeText(this,
                    "Guess must contain only letters (A-Z).", Toast.LENGTH_SHORT).show();
            return;
        }

        // Put letters into current row
        char[] guessChars = guess.toCharArray();
        for(int i = 0; i < 5; i++) {
            letterBoxes[currentRow][i].setText(String.valueOf(guessChars[i]));
        }

        // Color the row based on match
        colorRow(guess);

        // Check win/lose
        if(guess.equals(currentWord)) {
            Toast.makeText(this, "You got it!", Toast.LENGTH_SHORT).show();
            gameOver = true;
        } else {
            currentRow++;
            if(currentRow >= 6) {
                Toast.makeText(this,
                        "Out of guesses! The word was: " + currentWord, Toast.LENGTH_SHORT).show();
                gameOver = true;
            }
        }

        etGuess.setText("");
    }

    private void colorRow(String guess) {
        int[] result = new int[5];  // 2 = green, 1 = yellow, 0 = gray
        char[] ans = currentWord.toCharArray();
        char[] g = guess.toCharArray();
        int[] counts = new int[26];

        // Count letters in answer
        for(char c : ans) {
            counts[c - 'A']++;
        }

        // First pass: greens
        for(int i = 0; i < 5; i++) {
            if(g[i] == ans[i]) {
                result[i] = 2;
                counts[g[i] - 'A']--;
            }
        }

        // Second pass: yellow
        for(int i = 0; i < 5; i++) {
            if(result[i] == 0) {
                int idx = g[i] -'A';
                if(idx >= 0 && idx < 26 && counts[idx] > 0) {
                    result[i] = 1;
                    counts[idx]--;
                }
            }
        }

        // Apply colors
        int grey = ContextCompat.getColor(this, R.color.grey);
        int yellow = ContextCompat.getColor(this, R.color.yellow);
        int green = ContextCompat.getColor(this, R.color.green);
        int text = ContextCompat.getColor(this, R.color.white);

        for(int i = 0; i < 5; i++) {
            TextView box = letterBoxes[currentRow][i];
            box.setTextColor(text);

            if(result[i] == 2) {
                box.setBackgroundColor(green);
            } else if(result[i] == 1) {
                box.setBackgroundColor(yellow);
            } else {
                box.setBackgroundColor(grey);
            }
        }
    }

    private void restartGame() {
        // Reset flags
        gameOver = false;
        currentRow = 0;

        // Clear board
        for(int r = 0; r < 6; r++) {
            for(int c = 0; c < 5; c++) {
                TextView box = letterBoxes[r][c];
                box.setText("");
                styleEmptyBox(box);
            }
        }

        etGuess.setText("");
        loadNewWord();
    }

}