# Wordy
**Created by Jeremiah McDonald**

    Wordy is a Wordle-style guessing game for Android. You get six chances to guess a random 
five-letter word, and the app gives color feedback to help you figure out the answer.

- **Green** - right letter, right spot
- **Yellow** - right letter, wrong spot
- **Grey** - letter not in the word

    The game includes an "Add Word" screen, where you can add your own five-letter
words. Words are stored in Firebase and added to the shared word band, allowing 
the app to select a new random word each time you play.

## Features

- Six-turn word guessing gameplay
- Color-coded feedback for each guess
- Add your own five-letter words
- Firebase-backed word storage
- Random word selection each game

## Installation & Setup

1. Clone the repository:
2. Open the project in **Android Studio**
3. Make sure you have **Android SDK 33+** installed
4. Build and run the app on an emulator or device

> Note: The project uses Firebase.
> You must add your own 'google-services.json' file inside the 'app/' folder fo the app to compile.


## How to Play

1. Open the app- a new random word will be selected.
2. Enter a five-letter guess
3. Check the colors to see which letters are correct
4. Try to solve the word within six guesses
5. Tap **Add Word** if you want to contribute new words to the word bank


## Author

**Jeremiah McDonald**