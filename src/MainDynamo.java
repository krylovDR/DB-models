
import java.util.LinkedList;
import java.util.List;

import graphics.LinearFigure;
import io.CSVHandler;
import simulation.DynamoPerformer;

public class MainDynamo {

    /**
     * R - увеличение r
     * W - увеличение w
     * verProfit - график verProfit в зависимости от w
     */
    public static final String mode = "W";
    
    public static void main(String[] args) {
        int n = 100;         // количество узлов в системе
        int w = 25;         // количество узлов в кворуме записи
        int r = 5;          // количество узлов в кворуме чтения
        double q = 0.01;     // вероятность успешной записи
        int c = 100;        // количество слотов задержки инициализации нового обновления

        switch (mode) {

            /**
             * График увеличения r при постоянных n, w, q
             */
            case "R" -> {
                List<Object> valuesR = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                // увеличение r от 1 до n
                for (r = 1; r <= n; r++) {
                    var sim = new DynamoPerformer(n, w, r, q, c);
                    sim.simulate(100_000, 1);

                    valuesR.add(r);
                    valuesAoI.add(sim.getAvgAOI());
                    print("r = " + r + ", avg AoI: " + sim.getAvgAOI());
                }
                CSVHandler.createCSV("outX", valuesR);
                CSVHandler.createCSV("outY1", valuesAoI);

                LinearFigure.plot("outX", "outY1");
            }

            /**
             * График увеличения w при постоянных n, r, q
             */
            case "W" -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, q, c);
                    sim.simulate(100_000, 1);

                    valuesW.add(w);
                    valuesAoI.add(sim.getAvgAOI());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }
                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY", valuesAoI);

                LinearFigure.plot("outX", "outY");
            }

            /**
             * Графики средней избыточности verProfit и среднего возраста информации в зависимости от w
             */
            case "verProfit" -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y
                List<Object> valuesVerProfit = new LinkedList<>();  // для построения графика verProfit, ось Y

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, q, c);
                    sim.simulate(100_000, 1);

                    valuesW.add(w);
                    valuesAoI.add(sim.getAvgAOI());
                    valuesVerProfit.add(sim.getAvgVerProfit());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI()
                            + ", avg verProfit: " + sim.getAvgVerProfit());
                }
                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY", valuesAoI);
                CSVHandler.createCSV("verProfit", valuesVerProfit);

                LinearFigure.plot("outX", "outY", "verProfit");
            }
        }
        
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
