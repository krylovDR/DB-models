
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
     * r == c - график увеличения w при r == c
     * avgFrameSize - получение средней длины кадра
     * Ew_graphics - графики E(w) от p
     */
    public static final String mode = "Ew_graphics";
    
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
            case "r == c" -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI1 = new LinkedList<>();       // для графика при r = c = 2, ось Y
                List<Object> valuesAoI2 = new LinkedList<>();       // для графика при r = c = 10, ось Y
                List<Object> valuesAoI3 = new LinkedList<>();       // для графика при r = c = 40, ось Y

                int r1 = 2;
                int r2 = 15;
                int r3 = 40;

                // увеличение w от 1 до n при первом значении r
                for (w = 1; w <= n; w++) {
                    var sim1 = new DynamoPerformer(n, w, r1, q, r1);
                    var sim2 = new DynamoPerformer(n, w, r2, q, r2);
                    var sim3 = new DynamoPerformer(n, w, r3, q, r3);
                    
                    sim1.simulate(100_000, 1);
                    System.out.print("w = " + w + ";  1...");

                    sim2.simulate(100_000, 1);
                    System.out.print("2...");

                    sim3.simulate(100_000, 1);
                    System.out.println("3...");

                    valuesW.add(w);
                    valuesAoI1.add(sim1.getAvgAOI());
                    valuesAoI2.add(sim2.getAvgAOI());
                    valuesAoI3.add(sim3.getAvgAOI());

                    print("При r = " + r1 + ", ср. ко-во слотов в кадре: " + sim1.getAvgFrameSize());
                    print("При r = " + r2 + ", ср. ко-во слотов в кадре: " + sim2.getAvgFrameSize());
                    print("При r = " + r3 + ", ср. ко-во слотов в кадре: " + sim3.getAvgFrameSize());
                    print("");
                }

                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY1", valuesAoI1);
                CSVHandler.createCSV("outY2", valuesAoI2);
                CSVHandler.createCSV("outY3", valuesAoI3);

                FigureSettings settings = new FigureSettings(3);
                settings.setTitle("График среднего возраста информации при увеличении w и r == с");
                settings.setAxisX("w");
                settings.setAxisY("AoI");
                settings.addGraphicParameters("r = 2", "k", "-", "o", 0);
                settings.addGraphicParameters("r = 10", "r", "-", "o", 0);
                settings.addGraphicParameters("r = 40", "b", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY1", "outY2", "outY3");
            }
            case "avgFrameSize" -> {
                n = 10;
                w = 1;
                q = 0.1;

                var sim = new DynamoPerformer(n, w, r, q, c);
                sim.simulate(1_000_000, 1);

                print("Средняя длина кадра при n = " + n + "; w = " + w + "; q = " + q);
                print("avgFrameSize = " + sim.getAvgFrameSize());
            }
            case "Ew_graphics" -> {
                List<Object> valuesQ = new LinkedList<>();        // для построения графиков, ось X
                List<Object> valuesE1 = new LinkedList<>();       // для графика, ось Y
                List<Object> valuesE2 = new LinkedList<>();       // для графика, ось Y
                List<Object> valuesE3 = new LinkedList<>();       // для графика, ось Y
                List<Object> valuesE4 = new LinkedList<>();       // для графика, ось Y

                n = 10;

                int w1 = 1;
                int w2 = 3;
                int w3 = 7;
                int w4 = n;

                // увеличение p от 0.1 до 1.0
                for (q = 0.1; q < 1.0; q += 0.1) {
                    var sim1 = new DynamoPerformer(n, w1, r, q, c);
                    var sim2 = new DynamoPerformer(n, w2, r, q, c);
                    var sim3 = new DynamoPerformer(n, w3, r, q, c);
                    var sim4 = new DynamoPerformer(n, w4, r, q, c);
                    
                    sim1.simulate(500_000, 1);
                    System.out.print("q = " + q + ";  1...");

                    sim2.simulate(500_000, 1);
                    System.out.print("2...");

                    sim3.simulate(500_000, 1);
                    System.out.print("3...");

                    sim4.simulate(500_000, 1);
                    System.out.println("4...");

                    valuesQ.add(q);
                    valuesE1.add(sim1.getAvgFrameSize());
                    valuesE2.add(sim2.getAvgFrameSize());
                    valuesE3.add(sim3.getAvgFrameSize());
                    valuesE4.add(sim4.getAvgFrameSize());

                    print("");
                }

                CSVHandler.createCSV("outX", valuesQ);
                CSVHandler.createCSV("outY1", valuesE1);
                CSVHandler.createCSV("outY2", valuesE2);
                CSVHandler.createCSV("outY3", valuesE3);
                CSVHandler.createCSV("outY4", valuesE4);

                FigureSettings settings = new FigureSettings(4);
                settings.setTitle("График E(w) в зависимости от p");
                settings.setAxisX("p");
                settings.setAxisY("slots");
                settings.addGraphicParameters("w = " + w1, "k", "-", "o", 0);
                settings.addGraphicParameters("w = " + w2, "r", "-", "o", 0);
                settings.addGraphicParameters("w = " + w3, "b", "-", "o", 0);
                settings.addGraphicParameters("w = " + w4, "g", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY1", "outY2", "outY3", "outY4");
            }
        }
        
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
