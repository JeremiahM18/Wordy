package com.example.wordy;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordRepository {

    private final DatabaseReference wordRef;

    public WordRepository(){
        // Points to: wordBank in Firebase
        wordRef = FirebaseDatabase.getInstance().getReference("wordBank");
    }

    // Add Word
    public void addWord(String word, AddWordCallback callback){
        String id = wordRef.push().getKey();

        if(id == null){
            callback.onFailure("Error generating word ID.");
            return;
        }

        wordRef.child(id).setValue(word)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface AddWordCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    // Check Duplicate
    public void checkDuplicate(String word, DuplicateCheckCallback callback) {
        wordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot child : snapshot.getChildren()) {
                    String storedWord = child.getValue(String.class);

                    if(storedWord != null && storedWord.equalsIgnoreCase(word)) {
                        callback.onResult(true);
                        return;
                    }
                }

                callback.onResult(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false);
            }
        });
    }

    public interface DuplicateCheckCallback {
        void onResult(boolean exists);
    }

    // Get all words
    public void getAllWords(AllWordsCallback callback) {
        wordRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<String> words = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String word = child.getValue(String.class);
                    if (word != null){
                        words.add(word);
                    }
                }

                callback.onSuccess(words);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public interface AllWordsCallback {
        void onSuccess(List<String> words);
        void onFailure(String errorMessage);
    }

    // Get random word
    public void getRandomWord(RandomWordCallback callback) {
        getAllWords(new AllWordsCallback() {
            @Override
            public void onSuccess(List<String> words) {
                if(words.isEmpty()) {
                    callback.onFailure("No words in the database.");
                    return;
                }

                Random random = new Random();
                String randomWord = words.get(random.nextInt(words.size()));

                callback.onSuccess(randomWord);
            }

            @Override
            public void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }
        });
    }

    public interface RandomWordCallback {
        void onSuccess(String word);
        void onFailure(String errorMessage);
    }

}
