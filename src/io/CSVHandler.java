package io;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVHandler {
    public static final String PATH = "results/";

    /**
     * Запись данных {@code values} в {@code filename}.csv файл.
     * 
     * @param filename - название файла (без .csv)
     * @param values - данные подлежащие записи в файл
     */
    public static void createCSV(String filename, List<Object> values) {
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(PATH + filename + ".csv"))) {
            for (Object value : values) {
                writer.writeNext(new String[] {value.toString()});
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
