package graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LinearFigure {

    /**
     * Метод построения графиков.
     * @param xValuesFile - название файла с данными для оси X
     * @param yValuesFile - название файла с данными для оси Y
     */
    public static void plot(String xValuesFile, String yValuesFile) {
        try {
            // создание процесса
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "python",
                    "src\\graphics\\GraphicCreator.py",
                    xValuesFile + ".csv",
                    yValuesFile + ".csv");
            Process process = processBuilder.start();

            checkOutput(process);
            checkErrors(process);
            
            // завершение процесса
            int exitCode = process.waitFor();
            System.out.println("Process exited with code: " + exitCode);
            
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Чтение вывода скрипта Python
     * @param task - процесс работы скрипта на Python типа {@code Process}
     * @throws IOException
     */
    private static void checkOutput(Process task) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(task.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * Чтение ошибок (если есть) скрипта Python
     * @param task - процесс работы скрипта на Python типа {@code Process}
     * @throws IOException
     */
    private static void checkErrors(Process task) throws IOException {
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(task.getErrorStream()));
        String line;

        while ((line = errorReader.readLine()) != null) {
            System.err.println(line);
        }
    }
}
