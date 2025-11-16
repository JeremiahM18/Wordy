package com.example.wordy;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddWordActivity extends AppCompatActivity {

    private EditText etWordInput;
    private Button btnAdd;
    private TextView tvAddWordLabel;
    private Button btnCancel;

    private WordRepository repository;

    // Default label color
    private int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_word);

        // UI references
        etWordInput = findViewById(R.id.etWordInput);
        btnAdd = findViewById(R.id.btnAdd);
        btnCancel = findViewById(R.id.btnCancel);
        tvAddWordLabel = findViewById(R.id.tvAddWordLabel);

        // default label color from colors.xml
        defaultColor = ContextCompat.getColor(this, R.color.black);

        // Firebase repository
        repository = new WordRepository();

        // Cancel just goes back to WordyActivity
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();   // Closes this Activity
            }
        });

        // Show a Toast for now and later plug in validation & Firebase
//        btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String word = etWordInput.getText().toString().trim();
//                Toast.makeText(AddWordActivity.this,
//                        "Add clicked (word: " + word + ")", Toast.LENGTH_SHORT).show();
//            }
//        });
        btnAdd.setOnClickListener(v -> {
            handleAddWord();
        });

        // Edge-to-edge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void handleAddWord() {
        String input = etWordInput.getText().toString().trim();

        // Reset label to original color
        tvAddWordLabel.setTextColor(defaultColor);

        // Validation Rules

        // Empty
        if(input.isEmpty()) {
            showError("Word cannot be empty");
            return;
        }

        // Exactly 5 characters
        if(input.length() != 5) {
            showError("Word must be exactly 5 letters.");
            return;
        }

        // Letters only
        if(!input.matches("[a-zA-Z]+")) {
            showError("Word must contain only letters (A-Z).");
            return;
        }

        // Check duplicate in Firebase (case-insensitive)
        repository.checkDuplicate(input, exists -> {
            if (exists){
                showError("That word already exists.");
            } else {
                // Save the word (lowercased for consistency)
                repository.addWord(input.toLowerCase(), new WordRepository.AddWordCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(AddWordActivity.this,
                                "Word added successfully!", Toast.LENGTH_SHORT).show();
                        etWordInput.setText("");    // Clear input
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        showError("Failed to add word: " + errorMessage);
                    }
                });
            }
        });
    }

    private void showError(String message) {
        // Purple from colors.xml
        int purple = ContextCompat.getColor(this, R.color.label_error_purple);
        tvAddWordLabel.setTextColor(purple);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}