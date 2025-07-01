
import java.util.LinkedList;
import java.util.List;

import graphics.FigureSettings;
import graphics.LinearFigure;
import io.CSVHandler;
import simulation.DynamoPerformer;

public class MainDynamo {
    
    public static void main(String[] args) {
        int n = 50;        // количество узлов в системе
        int w = 25;         // количество узлов в кворуме записи
        int r = 4;          // количество узлов в кворуме чтения
        double q = 0.08;     // вероятность успешной записи

        List<Object> valuesX = new LinkedList<>();  // для построения графиков, ось X
        List<Object> valuesY = new LinkedList<>();  // для построения графиков, ось Y

        // увеличение w от 1 до n
        for (w = 1; w <= n; w++) {
            var sim = new DynamoPerformer(n, w, r, q);
            sim.simulate(50_000, 1);

            valuesX.add(w);
            valuesY.add(sim.getAvgAOI());
            print("w = " + w + ", Average AoI: " + sim.getAvgAOI());
        }
        CSVHandler.createCSV("outX", valuesX);
        CSVHandler.createCSV("outY", valuesY);

        FigureSettings settings = new FigureSettings(3);
        LinearFigure.plot("outX", "outY");
        
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
