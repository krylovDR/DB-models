
import java.util.LinkedList;
import java.util.List;

import graphics.FigureSettings;
import graphics.LinearFigure;
import io.CSVHandler;
import simulation.DynamoPerformer;

public class MainDynamo {

    public static final String mode = "R";  // R - увеличение r
                                            // W - увеличение w
    
    public static void main(String[] args) {
        int n = 50;         // количество узлов в системе
        int w = 25;         // количество узлов в кворуме записи
        int r = 25;         // количество узлов в кворуме чтения
        double q = 0.1;     // вероятность успешной записи

        switch (mode) {

            /**
             * График увеличения r при постоянных n, w, q
             */
            case "R" -> {
                List<Object> valuesR = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                // увеличение w от 1 до n
                for (r = 1; r <= n; r++) {
                    var sim = new DynamoPerformer(n, w, r, q);
                    sim.simulate(100_000, 1);

                    valuesR.add(r);
                    valuesAoI.add(sim.getAvgAOI());
                    print("r = " + r + ", avg AoI: " + sim.getAvgAOI());
                }
                CSVHandler.createCSV("outX", valuesR);
                CSVHandler.createCSV("outY", valuesAoI);

                LinearFigure.plot("outX", "outY");
            }

            /**
             * График увеличения w при постоянных n, r, q
             */
            case "W" -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, q);
                    sim.simulate(100_000, 1);

                    valuesW.add(w);
                    valuesAoI.add(sim.getAvgAOI());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }
                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY", valuesAoI);

                LinearFigure.plot("outX", "outY");
            }
        }
        
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
