package theoretical;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ScriptHandler {
    public static final String PATH = "results\\";

    /**
     * Метод для запуска скрипта Python для теоретического рассчёта оптимальных значений кворума записи.
     * Результат сохраняется в текстовый файл
     * @param maxN - предельное число узлов системы, для каждого из которых будут рассчитываться оптимальные значения
     * кворума записи
     * @param r - размер кворума чтения
     * @param p - вероятность успешной записи обновления
     * @param c - длительность задержки перед следующим обновлением
     */
    public static void runWopt(String maxN, String r, String p, String c) {
        try {
            String[] commands = new String[6];
            commands[0] = "python";
            commands[1] = "src\\theoretical\\Wopt_N.py";
            commands[2] = maxN;
            commands[3] = r;
            commands[4] = p;
            commands[5] = c;

            // создание процесса
            ProcessBuilder processBuilder = new ProcessBuilder(commands);
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
