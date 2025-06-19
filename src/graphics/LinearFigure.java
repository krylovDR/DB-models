package graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LinearFigure {

    public static void plot(String file1, String file2) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python",
                    "src\\graphics\\LinearGraphic.py",
                    file1 + ".csv",
                    file2 + ".csv");
            Process process = processBuilder.start();

            // Читаем вывод скрипта
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Читаем ошибки (если есть)
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            // Ждем завершения процесса
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
