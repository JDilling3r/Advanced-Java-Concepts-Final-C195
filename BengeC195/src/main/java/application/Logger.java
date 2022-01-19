package application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;

// log all user login activity
public class Logger {
    private static final String file = "login_activity.txt";

    public static void log (String userName, boolean success){
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);
            printWriter.println(userName + " " + (success ? "Successful Login Attempt at " : "Failed Login Attempt at ") + Instant.now().toString());
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
