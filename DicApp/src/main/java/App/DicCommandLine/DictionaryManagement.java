package App.DicCommandLine;

import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collections;
import Trie.Trie;

public class DictionaryManagement extends Dictionary {
    private Trie trie;

    public DictionaryManagement() {
        trie = new Trie();
    }

    private int indexSepate = 33;

    /**
     * Add new words from cmd.
     */
    public void insertFromCommandline() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of words: ");
        int numWords = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        for (int i = 0; i < numWords; i++) {
            System.out.println("Word #" + (i + 1));
            System.out.print("Enter the English word: ");
            String englishWord = scanner.nextLine();
            System.out.print("Enter the Vietnamese meaning: ");
            String vietnameseMeaning = scanner.nextLine();

            Word word = new Word(englishWord, vietnameseMeaning);
            addWord(word);
        }
    }

    /**
     * Add new words from file.
     */
    public void insertFromFile() {

        try (BufferedReader reader = new BufferedReader(new FileReader("DicApp\\src\\main\\java\\App\\DicCommandLine\\dictionaries.txt"))) {
            String line;
            String word = "" ;
            String pronunciation= "" ;
            String meaning= "" ;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("@") && line.contains("/")) {
                    word = line.substring(1,line.indexOf('/')-1);
                    pronunciation = line.substring(line.indexOf('/'));
                } else if (!word.isEmpty()) {
                    meaning += line;
                    meaning += '\n';
                    while ((line = reader.readLine()) != null && !line.isEmpty()) {
                        meaning += line;
                        meaning += '\n';
                    }

                    addWord(new Word(word, pronunciation, meaning));
                    meaning = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Export current dictionary data to file.
     */
    public void dictionaryExportToFile() {
        Scanner sc = new Scanner(System.in);

        //listWord myList = new listWord();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("DicApp\\src\\main\\java\\App\\DicCommandLine\\dictionaries.txt",false))) {
            List<Word> words = getWords();
            writer.write("-----" + "\n");
            for (Word word : words)
                writer.write("@" + word.getWordTarget() + " " + word.getPronunciation() + "\n" + word.getWordExplain() + "\n");

        } catch (Exception e) {
            System.out.println("Error exporting dictionary.");
            e.printStackTrace();
        }
    }

    /**
     * Add new word.
     */
    public void addWord() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter English word: ");
        String wordTarget = scanner.nextLine();
        System.out.print("Enter pronunciation: ");
        String pronunciation = scanner.nextLine();
        System.out.print("Enter Vietnamese meaning: ");
        String wordExplain = scanner.nextLine();

        Word word = new Word(wordTarget, pronunciation, wordExplain);
        addWord(word);

        dictionaryExportToFile();
        System.out.println("Word added successfully.");
    }

    /**
     * Remove word.
     */
    public void removeWord() {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the word to remove: ");
        String wordTarget = scanner.nextLine();

        List<Word> words = getWords();

        Word neededWord = binarySearchWord(words.subList(0, indexSepate), wordTarget);

        if (neededWord == null) {
            neededWord = binarySearchWord(words.subList(indexSepate, words.size()), wordTarget);
        }

        if (neededWord != null) {
            removeWord(neededWord);
            dictionaryExportToFile();
            System.out.println("Word removed successfully.");
            return;
        }

        System.out.println("Word \"" + wordTarget + "\" not found in the dictionary.");

    }

    /**
     * Update word.
     */
    public void updateWord() {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the word to update: ");
        String wordTarget = scanner.nextLine();

        List<Word> words = getWords();

        Word neededWord = binarySearchWord(words.subList(0, indexSepate), wordTarget);

        if (neededWord == null) {
            neededWord = binarySearchWord(words.subList(indexSepate, words.size()), wordTarget);
        }

        if (neededWord != null) {

            System.out.print("Enter new English word: ");
            String newWordTarget = scanner.nextLine();
            System.out.print("Enter new pronunciation: ");
            String newPronunciation = scanner.nextLine();
            System.out.print("Enter new Vietnamese meaning: ");
            String newWordExplain = scanner.nextLine();

            neededWord.setWordTarget(newWordTarget);
            neededWord.setPronunciation(newPronunciation);
            neededWord.setWordExplain(newWordExplain);

            dictionaryExportToFile();
            System.out.println("Word updated successfully.");
            return;
        }

        System.out.println("Word \"" + wordTarget + "\" not found in the dictionary.");

    }

    /**
     * Display a list of words.
     */
    public void displayAllWords() {
        List<Word> words = getWords();
        int index = 1;
        for (Word word : words) {
            System.out.println(index + ". " + word.getWordTarget());
            System.out.println(word.getPronunciation());
            System.out.println(word.getWordExplain());
            index ++;
        }
    }

    /**
     * Find English word.
     */
    public void dictionaryLookup() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a word to look up: ");
        String wordTarget = scanner.nextLine();

        List<Word> words = getWords();

        Word neededWord = binarySearchWord(words.subList(0, indexSepate), wordTarget);

        if (neededWord == null) {
            neededWord = binarySearchWord(words.subList(indexSepate, words.size()), wordTarget);
        }

        if (neededWord != null) {
                System.out.println("Word: " + neededWord.getWordTarget());
                System.out.println("Pronunciation: " + neededWord.getPronunciation());
                System.out.println("Meaning: " + neededWord.getWordExplain());
                return;
        }

        System.out.println("Word \"" + wordTarget + "\" not found in the dictionary.");
    }

    /**
     * Search words with prefix.
     */
    public void dictionarySearcher() {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a prefix to search: ");
        String prefix = scanner.nextLine().toLowerCase();

        List<Word> words = getWords();
        for (Word word : words) {
            trie.insertWord(word.getWordTarget());
        }

        List<String> matchedWords = trie.searchWordsWithPrefix(prefix);
        Collections.sort(matchedWords);

        int count = 0;

        if (matchedWords.isEmpty()) {
            System.out.println("No words found.");
        } else {
            System.out.println("Words starting with \"" + prefix + "\":");
            for (String word : matchedWords) {
                System.out.println(count + ". " + word);
                count ++;
                if (count == 20 && count > matchedWords.size()) break;
            }
        }
    }

    private Word binarySearchWord(List<Word> words, String target) {
        int left = 0;
        int right = words.size() - 1;
        int cnt = 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Word word = words.get(mid);
            String wordTarget = word.getWordTarget();
            System.out.println(cnt + ". " + wordTarget);
            cnt ++;

            int compareResult = wordTarget.compareToIgnoreCase(target);
            if (compareResult == 0) {
                return word;
            } else if (compareResult < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null;
    }

}


