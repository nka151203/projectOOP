package DicGame;

import App.DicCommandLine.DictionaryManagement;
import App.DicCommandLine.Word;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Hangman extends Game {
    DictionaryManagement dictionary;
    List<Word> words;
    List<String> usedWord = new ArrayList<>();
    Scanner scanner;
    int numAttempt;
    int time;
    static int NUM_HINTS = 3;
    static int score = 0;

    public Hangman() {
        dictionary = new DictionaryManagement();
        dictionary.insertFromFile();
        words = dictionary.getWords();

        if (words.isEmpty()) {
            System.out.println("No words in the dictionary.");
            return;
        }
        numAttempt = 0;
    }

    /**
     * Random Word
     */
    Word RandomWord() {
        Random random = new Random();
        Word randomWord = words.get(random.nextInt(words.size()));
        String targetWord = randomWord.getWordTarget().toLowerCase();

        while (!checkWord(targetWord)) {
            randomWord = words.get(random.nextInt(words.size()));
            targetWord = randomWord.getWordTarget().toLowerCase();
        }
        return randomWord;
    }

    /**
     * Check randomWord valid
     */
    private boolean checkWord(String word) {
        // isNotValidWord
        if (word.matches(".*[^a-zA-Z].*")) {
            return false;
        }
        if (word.contains(" ")) {
            return false;
        }

        // isNotDuplicateWord
        for (String w : usedWord) {
            if (w.equalsIgnoreCase(word)) {
                return false;
            }
        }

        return true;
    }

    void showHidden(Word randomWord, StringBuilder hiddenWord) {
        String targetWord = randomWord.getWordTarget();
        for (int i = 0; i < targetWord.length(); i++) {
            hiddenWord.append("_");
        }
    }

    /**
     * use hint to show 1 char of word.
     * @ String targetWord
     * @ StringBuilder hiddenWord
     * @ return hiddenWord
     * Sau khi su dung useHint phai kiem tra xem hiddenWord = targetWord chua
     */
    StringBuilder useHint(Word randomWord, StringBuilder hiddenWord) {
        String targetWord = randomWord.getWordTarget();
        if (NUM_HINTS > 0) {
            NUM_HINTS --;
            char hint = 0;

            for (int i = 0; i < targetWord.length(); i++) {
                if (hiddenWord.charAt(i) == '_') {
                    hint = targetWord.charAt(i);
                }
            }

            for (int i = 0; i < targetWord.length(); i++) {
                if (targetWord.charAt(i) == hint) {
                    hiddenWord.setCharAt(i, hint);
                }
            }
            return hiddenWord;
        }
        return hiddenWord;
    }

    StringBuilder checkChar(Word randomWord, StringBuilder hiddenWord, char c ) {
        String targetWord = randomWord.getWordTarget();
        if (targetWord.contains(String.valueOf(c))) {
            for (int i = 0; i < targetWord.length(); i++) {
                if (targetWord.charAt(i) == c) {
                    hiddenWord.setCharAt(i, c);
                }
            }
        }
        numAttempt ++;
        return hiddenWord;
    }

    int checkWinOrLose(StringBuilder hiddenWord) {
        if (!hiddenWord.toString().contains("_")) {
            score += numAttempt * (time);
            return 1;
        }
        if (numAttempt == 7) {
            return -1;
        }

        return 0;
    }

    public void playGame() {
    };

    String getResultWord(Word randomWord) {
        String result = randomWord.getWordTarget() + "\n";
        result += randomWord.getPronunciation() + "\n";
        result += randomWord.getWordExplain() + "\n";
        return result;
    }
}