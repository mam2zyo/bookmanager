package log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static config.AppConfig.APP_LOG_FILE;

public class AppLog {

    public static void writeLog(String msg) {
        try (FileWriter fw = new FileWriter(APP_LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(msg);
            bw.newLine();

        } catch (IOException e) {
            System.err.println("Log 기록 중 오류 발생 " + e.getMessage());
            e.printStackTrace();
        }
    }
}