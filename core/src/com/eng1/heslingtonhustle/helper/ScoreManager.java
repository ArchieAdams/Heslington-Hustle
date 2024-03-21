package com.eng1.heslingtonhustle.helper;

import java.io.*;

public class ScoreManager {
    private static final String HIGHSCORE_FILE = "highscore.txt";

    public static int loadHighScore() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(HIGHSCORE_FILE));
            String line = reader.readLine();
            reader.close();
            return Integer.parseInt(line);
        } catch (IOException | NumberFormatException e) {
            System.out.println("Highscore was not found 0 was returned");
            return 0; // Return 0 if there's an error or no high score saved
        }
    }

    public static void saveHighScore(int score) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE));
            writer.write(Integer.toString(score));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}