import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("1. Normal Wordle" + "\n" +
                "2. Urban Dictionary Wordle" +
                "\n3. Pokemon Wordle");
        System.out.println("Input the number for the game you want to play: ");
        int gameMode = scanner.nextInt();
        ArrayList<String> words = readCSV(gameMode);
        System.out.println("How many letters should the words be?");
        int wordLength = scanner.nextInt();
        ArrayList<String> possibleWords = getPossibleWords(words, wordLength);
        String correctWord = pickCorrectWord(possibleWords);
        play(correctWord, wordLength, possibleWords);
        scanner.close();
    }
    public static ArrayList<String> readCSV(int gameMode){
        String line = "";
        String splitBy = ",";
        //String alsoSplit = "#";
        ArrayList<String> res = new ArrayList<String>();
        if(gameMode == 1) { //normal gamemode
            try {
                //parsing a CSV file into BufferedReader class constructor
                BufferedReader br = new BufferedReader(new FileReader("unigram_freq.csv"));
                while ((line = br.readLine()) != null) {
                    String newLine = line.split(splitBy)[0];
                    res.add(newLine);
                    //res.add(newLine.split(alsoSplit)[0]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(gameMode == 2){ //urbanDictionary words
            try {
                //parsing a CSV file into BufferedReader class constructor
                BufferedReader br = new BufferedReader(new FileReader("urban_dictionary.csv"));
                while ((line = br.readLine()) != null) {
                    res.add(line.split(splitBy)[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(gameMode == 3){ //Pokemon Wordle
            try{BufferedReader br = new BufferedReader(new FileReader("pokedex.csv"));
                while((line = br.readLine()) != null){
                    res.add(line.split(splitBy)[2]);
                }
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        return res;
    }
    public static String pickCorrectWord(ArrayList<String> possibleWords){
        String res;
        Random random = new Random();
        res = possibleWords.get(random.nextInt(possibleWords.size())).toLowerCase();
        return res;
    }
    public static ArrayList<String> getPossibleWords(ArrayList<String> words, int length){
        ArrayList<String> possibleWords = new ArrayList<String>();
        for(int i = 0; i < words.size(); i++){
            if(words.get(i).length() == length){
                possibleWords.add(words.get(i));
            }
        }
        for(String word : possibleWords){
            if(word.contains(" ")){
                possibleWords.remove(word);
            }
        }
        return possibleWords;
    }
    public static void play(String correctWord, int length, ArrayList<String> possibleWords){
        int turnAmount = length+(length%5);
        correctWord = correctWord.toLowerCase();
        Scanner scanner = new Scanner(System.in);
        ArrayList<Word> guesses = new ArrayList<Word>();
        String newGuess = "";
        try {
            for (int i = 0; i + 1 <= turnAmount; i++) {
                System.out.println("The word length is " + length + ".");
                System.out.println("You have " + (turnAmount - i) + " guesses left.");
                System.out.println("Guess a word: ");
                String guess = scanner.nextLine();
                guess = guess.toLowerCase();
                boolean hasGuess = false;
                for (String str : possibleWords) {
                    if (guess.equals(str.toLowerCase())) {
                        hasGuess = true;
                        break;
                    }
                }
                if (!hasGuess) {
                    System.out.println("This is not a possible word. Please guess again.");
                    i--;
                } else if (guess.length() != length) {
                    System.out.print("This is not the correct length. Please guess again.");
                    i--;
                } else {
                    if (guess.equals(correctWord)) {
                        for(int t = 0; t < guesses.size(); t++){
                            System.out.println(guesses.get(t).getName() + RESET + "\n");
                        }
                        guess = GREEN + guess + RESET;
                        System.out.println(guess);
                        return;
                    } else {
                        char[] guessArray = guess.toCharArray();
                        char[] correctArray = correctWord.toCharArray();
                        //ArrayList<String> guessList = new ArrayList<String>();
                        //ArrayList<String> corrList = new ArrayList<String>();
                        HashMap<Character, Integer> duoLetter = new HashMap<Character, Integer>();
                        int letterAmt = 0;
                        ArrayList<Character> passThrough = new ArrayList<Character>();
                        for (int k = 0; k < guessArray.length; k++) {
                            for (char ch : correctArray) {
                                if (correctArray[k] == ch && !passThrough.contains(ch)) {
                                    if (!duoLetter.containsKey(ch)) {
                                        duoLetter.put(ch, 1);
                                    } else {
                                        duoLetter.put(ch, duoLetter.get(ch) + 1);
                                    }
                                }
                            }
                            passThrough.add(correctArray[k]);
                        }
                        /*for (int y = 0; y < guessArray.length; y++) {
                            guessList.add(Character.toString(guessArray[y]));
                        }*/
                        for (int j = 0; j < guessArray.length; j++) {
                            if (guessArray[j] == correctArray[j]) {
                                newGuess += GREEN + guess.substring(j, j+1) + RESET;
                                //guessList.set(i, GREEN + guessArray[j] + RESET);
                            } else if (guessArray[j] != correctArray[j]) {
                                if(duoLetter.containsValue(guessArray[j])){
                                    letterAmt++;
                                }
                                else{
                                    letterAmt=1;
                                }
                                if (correctWord.contains(Character.toString(guessArray[j])) && letterAmt <= duoLetter.get(correctArray[j])) {
                                    newGuess += YELLOW + guess.substring(j, j+1) + RESET;
                                    //guessList.set(i, YELLOW + guessArray[j] + RESET);
                                } else{
                                    newGuess += guessArray[j];
                                }
                            }
                        }
                        /*
                        guess = "";
                        for(String str : guessList){
                            guess += str;
                        }*/
                    }
                    Word guessWord = new Word(newGuess);
                    guesses.add(guessWord);
                }
                for(int m = 0; m < guesses.size(); m++){
                    System.out.println(guesses.get(m).getName() + RESET + "\n");
                    System.out.println("\n");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("The correct word is: " + correctWord);
        scanner.close();
    }
}
