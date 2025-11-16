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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
            // handle submit guess
        });

        btnClear.setOnClickListener(v -> {
            etGuess.setText("");
        });

        btnRestart.setOnClickListener(v -> {
            // reset game state and get a new word
        });

        // Build the 6X5 board
        createBoard();

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
                box.setBackgroundResource(android.R.drawable.alert_light_frame);    // placeholder boarder

                // Store reference
                letterBoxes[row][col] = box;

                // Add box to row
                rowLayout.addView(box);
            }

            // Add row to the vertical board container
            boardContainer.addView(rowLayout);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, getResources().getDisplayMetrics()));
    }
}