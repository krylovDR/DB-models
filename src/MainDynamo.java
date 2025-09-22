
import java.util.LinkedList;
import java.util.List;

import graphics.FigureSettings;
import graphics.LinearFigure;
import io.CSVHandler;
import simulation.DynamoPerformer;

public class MainDynamo {

    /**
     * R - увеличение r
     * W - увеличение w
     * W2 - увеличение w при r = 5 и 15
     * verProfit - график verProfit в зависимости от w
     * AoI_Node - процесс изменения возраста информации за 50 слотов
     */
    public static final String mode = "AoI_Node";
    
    public static void main(String[] args) {
        int n = 100;            // количество узлов в системе
        int w = 25;             // количество узлов в кворуме записи
        int r = 5;              // количество узлов в кворуме чтения
        double q = 0.01;        // вероятность успешной записи
        int c = 100;            // количество слотов задержки инициализации нового обновления

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
                CSVHandler.createCSV("outY", valuesAoI);

                FigureSettings settings = new FigureSettings(1);
                settings.setTitle("График среднего возраста информации при увеличении r");
                settings.setAxisX("r");
                settings.setAxisY("AoI");
                settings.addGraphicParameters("w = 25", "k", "-", "o", 0);
                settings.saveJSON();

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
                    var sim = new DynamoPerformer(n, w, r, q, c);
                    sim.simulate(100_000, 1);

                    valuesW.add(w);
                    valuesAoI.add(sim.getAvgAOI());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }
                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY", valuesAoI);

                FigureSettings settings = new FigureSettings(1);
                settings.setTitle("График среднего возраста информации при увеличении w");
                settings.setAxisX("w");
                settings.setAxisY("AoI");
                settings.addGraphicParameters("r = 5", "k", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY");
            }

            /**
             * График среднего возраста информации при увеличении w и двух разных r
             */
            case "W2" -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI1 = new LinkedList<>();       // для построения графика AoI, ось Y
                List<Object> valuesAoI2 = new LinkedList<>();       // для построения графика AoI, ось Y

                // увеличение w от 1 до n при первом значении r
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, q, c);
                    sim.simulate(100_000, 1);

                    valuesW.add(w);
                    valuesAoI1.add(sim.getAvgAOI());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }

                r = 25;
                print("\n==================== r = " + r + " ====================\n");
                // увеличение w от 1 до n при втором значении r
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, q, c);
                    sim.simulate(100_000, 1);

                    valuesAoI2.add(sim.getAvgAOI());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }

                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY1", valuesAoI1);
                CSVHandler.createCSV("outY2", valuesAoI2);

                FigureSettings settings = new FigureSettings(2);
                settings.setTitle("График среднего возраста информации при увеличении w и r = 5 и 25");
                settings.setAxisX("w");
                settings.setAxisY("AoI");
                settings.addGraphicParameters("r = 5", "k", "-", "o", 0);
                settings.addGraphicParameters("r = 25", "r", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY1", "outY2");
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

                FigureSettings settings = new FigureSettings(2);
                settings.setTitle("График средней избыточности verProfit и AoI в зависимости от w");
                settings.setAxisX("w");
                settings.setAxisY("AoI");
                settings.addGraphicParameters("AoI", "k", "-", "o", 0);
                settings.addGraphicParameters("verProfit", "r", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY", "verProfit");
            }

            /**
             * График изменения возраста информации на узле в течении 1000 слотов
             */
            case "AoI_Node" -> {
                List<Object> valuesSlots = new LinkedList<>();      // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                var sim = new DynamoPerformer(n, w, r, q, c);

                // пропускаем 10000 слотов до стабильного состояния системы
                for (int i = 0; i < 10000; i++) sim.simulateSlot(4);

                // подсчёт возраста информации в 1000 слотах на узле 4
                for (int slot = 0; slot < 1000; slot++) {
                    valuesSlots.add(slot + 1);
                    valuesAoI.add(sim.simulateSlot(4));
                }

                CSVHandler.createCSV("outX", valuesSlots);
                CSVHandler.createCSV("outY", valuesAoI);

                FigureSettings settings = new FigureSettings(1);
                settings.setTitle("График изменения возраста информации на узле в течении 1000 слотов");
                settings.setAxisX("Количество слотов");
                settings.setAxisY("AoI");
                settings.addGraphicParameters("Возраст информации на узле 4", "k", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY");
            }
        }
        
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
