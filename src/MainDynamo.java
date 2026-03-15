
import java.util.LinkedList;
import java.util.List;

import graphics.FigureSettings;
import graphics.LinearFigure;
import io.CSVHandler;
import simulation.DynamoPerformer;

public class MainDynamo {

    private enum Mode {
        AoI_R,          // график AoI (среднего возраста информации) в зависимости от r (кворума чтения)
        AoI_W,          // график AoI в зависимости от w (кворума записи)
        AoI_W_2,        // 2 графика AoI в зависимости от w (при r = 5 и 15)
        AoI_W_3,        // график AoI в зависимости от w (при r == c)

        VP_AoI_W,       // 2 графика verProfit (все обновившиеся узлы - W) и AoI в зависимости от w
        AoI_NODE,       // процесс изменения возраста информации на узле за 50 слотов

        EW,             // вычисление средней длины кадра (Ew)
        EW_P,           // графики Ew от p (вероятности успешной записи)
        AoI_EW_W,       // 2 графика: AoI и Ew в зависимости от w

        AoI_C_sc,       // график AoI в зависимости от задержки c, а также наличие смещения минимума с
                        // изменением w при разных c
        VP_P_sc,        // график verProfit в зависимости от p при разных w
        VP_W_sc,        // график verProfit с изменением w при разных p
        VP_AoI_W_sc,    // график verProfit и AoI с изменением w

        AoI_W_ReadC     // график AoI в зависимости от w для случая, когда чтение происходит только во
                        // время задержки
    }

    public static final Mode mode = Mode.VP_AoI_W_sc;
    
    public static void main(String[] args) {
        int n = 100;                // количество узлов в системе
        int w = 20;                 // количество узлов в кворуме записи
        int r = 20;                 // количество узлов в кворуме чтения
        double p = 0.5;            // вероятность успешной записи
        int c = 100;                // количество слотов задержки инициализации нового обновления

        switch (mode) {

            /**
             * График среднего возраста информации от увеличения r при постоянных n, w, p
             */
            case AoI_R -> {
                List<Object> valuesR = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                // увеличение r от 1 до n
                for (r = 1; r <= n; r++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
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
             * График среднего возраста информации от увеличения w при постоянных n, r, p
             */
            case AoI_W -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
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
            case AoI_W_2 -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI1 = new LinkedList<>();       // для построения графика AoI, ось Y
                List<Object> valuesAoI2 = new LinkedList<>();       // для построения графика AoI, ось Y

                // увеличение w от 1 до n при первом значении r
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
                    sim.simulate(100_000, 1);

                    valuesW.add(w);
                    valuesAoI1.add(sim.getAvgAOI());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }

                r = 25;
                print("\n==================== r = " + r + " ====================\n");
                // увеличение w от 1 до n при втором значении r
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
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
            case VP_AoI_W -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y
                List<Object> valuesVerProfit = new LinkedList<>();  // для построения графика verProfit, ось Y

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
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
            case AoI_NODE -> {
                List<Object> valuesSlots = new LinkedList<>();      // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                var sim = new DynamoPerformer(n, w, r, p, c);

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
            case AoI_W_3 -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI1 = new LinkedList<>();       // для графика при r = c = 2, ось Y
                List<Object> valuesAoI2 = new LinkedList<>();       // для графика при r = c = 10, ось Y
                List<Object> valuesAoI3 = new LinkedList<>();       // для графика при r = c = 40, ось Y

                int r1 = 2;
                int r2 = 15;
                int r3 = 40;

                // увеличение w от 1 до n при первом значении r
                for (w = 1; w <= n; w++) {
                    var sim1 = new DynamoPerformer(n, w, r1, p, r1);
                    var sim2 = new DynamoPerformer(n, w, r2, p, r2);
                    var sim3 = new DynamoPerformer(n, w, r3, p, r3);
                    
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
            case EW -> {
                n = 10;
                w = 4;
                r = 3;
                p = 0.1;

                var sim = new DynamoPerformer(n, w, r, p, c);
                sim.simulate(1_000_000, 1);

                print("Средняя длина кадра при n = " + n + "; w = " + w + "; p = " + p);
                print("avgFrameSize = " + sim.getAvgFrameSize());
            }
            case EW_P -> {

                // График средней длины кадра в зависимости от вероятности успешной доставки

                List<Object> valuesP = new LinkedList<>();        // для построения графиков, ось X
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
                for (p = 0.1; p < 1.0; p += 0.1) {
                    var sim1 = new DynamoPerformer(n, w1, r, p, c);
                    var sim2 = new DynamoPerformer(n, w2, r, p, c);
                    var sim3 = new DynamoPerformer(n, w3, r, p, c);
                    var sim4 = new DynamoPerformer(n, w4, r, p, c);
                    
                    sim1.simulate(500_000, 1);
                    System.out.print("p = " + p + ";  1...");

                    sim2.simulate(500_000, 1);
                    System.out.print("2...");

                    sim3.simulate(500_000, 1);
                    System.out.print("3...");

                    sim4.simulate(500_000, 1);
                    System.out.println("4...");

                    valuesP.add(p);
                    valuesE1.add(sim1.getAvgFrameSize());
                    valuesE2.add(sim2.getAvgFrameSize());
                    valuesE3.add(sim3.getAvgFrameSize());
                    valuesE4.add(sim4.getAvgFrameSize());

                    print("");
                }

                CSVHandler.createCSV("outX", valuesP);
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

            /**
             * График AoI и Ew в зависимости от w
             */
            case AoI_EW_W -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков,    ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y
                List<Object> valuesEw = new LinkedList<>();         // для построения графика Ew,  ось Y

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
                    sim.simulate(100_000, 1);

                    valuesW.add(w);
                    valuesAoI.add(sim.getAvgAOI());
                    valuesEw.add(sim.getAvgFrameSize());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI() + ", Ew: " + sim.getAvgFrameSize());
                }
                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY", valuesAoI);
                CSVHandler.createCSV("outY2", valuesEw);

                FigureSettings settings = new FigureSettings(2);
                settings.setTitle("Графики AoI и Ew в зависимости от w");
                settings.setAxisX("w");
                settings.setAxisY("slots");
                settings.addGraphicParameters("AoI", "k", "-", "o", 0);
                settings.addGraphicParameters("Ew", "r", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY", "outY2");
            }

            case AoI_C_sc -> {
                List<Object> valuesC = new LinkedList<>();              // для построения графиков,      ось X
                List<Object> valuesAoI1 = new LinkedList<>();           // для построения графика AoI 1, ось Y
                List<Object> valuesAoI2 = new LinkedList<>();           // для построения графика AoI 2, ось Y
                List<Object> valuesAoI3 = new LinkedList<>();           // для построения графика AoI 3, ось Y

                n = 100;
                int w1 = 10;
                int w2 = 20;
                int w3 = 40;
                r = 10;
                p = 0.01;

                // увеличение c от 0 до 500
                for (c = 1; c <= 500; c++) {
                    var sim1 = new DynamoPerformer(n, w1, r, p, c);
                    var sim2 = new DynamoPerformer(n, w2, r, p, c);
                    var sim3 = new DynamoPerformer(n, w3, r, p, c);
                    
                    sim1.simulate(1_000_000, 1);
                    sim2.simulate(1_000_000, 1);
                    sim3.simulate(1_000_000, 1);

                    valuesC.add(c);
                    valuesAoI1.add(sim1.getAvgAOI());
                    valuesAoI2.add(sim2.getAvgAOI());
                    valuesAoI3.add(sim3.getAvgAOI());

                    print("c = " + c + ", avgAoI [1] [2] [3] : [" + sim1.getAvgAOI() + 
                                                            "] [" + sim2.getAvgAOI() + 
                                                            "] [" + sim3.getAvgAOI() + "]");
                }

                CSVHandler.createCSV("outC", valuesC);
                CSVHandler.createCSV("outY1", valuesAoI1);
                CSVHandler.createCSV("outY2", valuesAoI2);
                CSVHandler.createCSV("outY3", valuesAoI3);

                FigureSettings settings = new FigureSettings(3);

                settings.setTitle("Графики AoI в зависимости от задержки c при разных w");
                settings.setAxisX("Длительность задержки, слотов");
                settings.setAxisY("AoI, слотов");

                settings.addGraphicParameters("w = " + w1, "k", "-", "o", 0);
                settings.addGraphicParameters("w = " + w2, "r", "-", "o", 0);
                settings.addGraphicParameters("w = " + w3, "g", "-", "o", 0);

                settings.saveJSON();

                LinearFigure.plot("outC", "outY1", "outY2", "outY3");

                // ======== вычисление AoI в зависимости от w при разных c =================

                List<Object> valuesW = new LinkedList<>();              
                valuesAoI1.clear();
                valuesAoI2.clear();
                valuesAoI3.clear();

                int c1 = 50;
                int c2 = 200;
                int c3 = 400;

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim1 = new DynamoPerformer(n, w, r, p, c1);
                    var sim2 = new DynamoPerformer(n, w, r, p, c2);
                    var sim3 = new DynamoPerformer(n, w, r, p, c3);
                    
                    sim1.simulate(1_000_000, 1);
                    sim2.simulate(1_000_000, 1);
                    sim3.simulate(1_000_000, 1);

                    valuesW.add(w);
                    valuesAoI1.add(sim1.getAvgAOI());
                    valuesAoI2.add(sim2.getAvgAOI());
                    valuesAoI3.add(sim3.getAvgAOI());

                    print("w = " + w + ", avgAoI [1] [2] [3] : [" + sim1.getAvgAOI() + 
                                                            "] [" + sim2.getAvgAOI() + 
                                                            "] [" + sim3.getAvgAOI() + "]");
                }

                CSVHandler.createCSV("outW", valuesW);
                CSVHandler.createCSV("outY1", valuesAoI1);
                CSVHandler.createCSV("outY2", valuesAoI2);
                CSVHandler.createCSV("outY3", valuesAoI3);

                FigureSettings settings2 = new FigureSettings(3);

                settings2.setTitle("Графики AoI в зависимости от размера кворума w при разных c");
                settings2.setAxisX("Размер кворума записи w, слотов");
                settings2.setAxisY("AoI, слотов");

                settings2.addGraphicParameters("c = " + c1, "k", "-", "o", 0);
                settings2.addGraphicParameters("c = " + c2, "r", "-", "o", 0);
                settings2.addGraphicParameters("c = " + c3, "g", "-", "o", 0);

                settings2.saveJSON();

                LinearFigure.plot("outW", "outY1", "outY2", "outY3");

                print("Минимумы [узлов в кворуме, значение]:");
                
                double min1 = (double) valuesAoI1.get(0);
                double min2 = (double) valuesAoI2.get(0);
                double min3 = (double) valuesAoI3.get(0);

                var it1 = valuesAoI1.listIterator();
                var it2 = valuesAoI2.listIterator();
                var it3 = valuesAoI3.listIterator();
                
                while (it1.hasNext() && it2.hasNext() && it3.hasNext()) {
                    double tmp1 = (double) it1.next();
                    double tmp2 = (double) it2.next();
                    double tmp3 = (double) it3.next();

                    min1 = min1 > tmp1 ? tmp1 : min1;
                    min2 = min2 > tmp2 ? tmp2 : min2;
                    min3 = min3 > tmp3 ? tmp3 : min3;
                }

                print("При c = " + c1 + ", [" + valuesAoI1.indexOf((Object) min1) + ", " + min1 + "]");
                print("При c = " + c2 + ", [" + valuesAoI2.indexOf((Object) min2) + ", " + min2 + "]");
                print("При c = " + c3 + ", [" + valuesAoI3.indexOf((Object) min3) + ", " + min3 + "]");
            }

            case VP_P_sc -> {
                List<Object> valuesP = new LinkedList<>();      // для построения графиков,            ось X
                List<Object> valuesVP1 = new LinkedList<>();    // для построения графика verProfit 1, ось Y
                List<Object> valuesVP2 = new LinkedList<>();    // для построения графика verProfit 2, ось Y
                List<Object> valuesVP3 = new LinkedList<>();    // для построения графика verProfit 3, ось Y

                n = 100;
                int w1 = 10;
                int w2 = 20;
                int w3 = 40;
                r = 10;
                c = 100;

                // увеличение c от 0 до 500
                for (p = 0.01; p < 1.0; p += 0.01) {
                    var sim1 = new DynamoPerformer(n, w1, r, p, c);
                    var sim2 = new DynamoPerformer(n, w2, r, p, c);
                    var sim3 = new DynamoPerformer(n, w3, r, p, c);
                    
                    sim1.simulate(100_000, 1);
                    sim2.simulate(100_000, 1);
                    sim3.simulate(100_000, 1);

                    valuesP.add(p);
                    valuesVP1.add(sim1.getAvgVerProfit());
                    valuesVP2.add(sim2.getAvgVerProfit());
                    valuesVP3.add(sim3.getAvgVerProfit());

                    print("p = " + p + ", avgVerProfit [1] [2] [3] : [" + sim1.getAvgVerProfit() + 
                                                                  "] [" + sim2.getAvgVerProfit() + 
                                                                  "] [" + sim3.getAvgVerProfit() + "]");
                }

                CSVHandler.createCSV("outP", valuesP);
                CSVHandler.createCSV("outVP1", valuesVP1);
                CSVHandler.createCSV("outVP2", valuesVP2);
                CSVHandler.createCSV("outVP3", valuesVP3);

                FigureSettings settings = new FigureSettings(3);

                settings.setTitle("Графики verProfit в зависимости от вероятности p при разных w");
                settings.setAxisX("Значение вероятности");
                settings.setAxisY("verProfit, узлов");

                settings.addGraphicParameters("w = " + w1, "k", "-", "o", 0);
                settings.addGraphicParameters("w = " + w2, "r", "-", "o", 0);
                settings.addGraphicParameters("w = " + w3, "g", "-", "o", 0);

                settings.saveJSON();

                LinearFigure.plot("outP", "outVP1", "outVP2", "outVP3");
            }

            case VP_W_sc -> {
                List<Object> valuesW = new LinkedList<>();              
                List<Object> valuesVP1 = new LinkedList<>();    // для построения графика verProfit 1, ось Y
                List<Object> valuesVP2 = new LinkedList<>();    // для построения графика verProfit 2, ось Y
                List<Object> valuesVP3 = new LinkedList<>();    // для построения графика verProfit 3, ось Y

                n = 100;
                r = 10;
                c = 100;
                double p1 = 0.05;
                double p2 = 0.15;
                double p3 = 0.30;

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim1 = new DynamoPerformer(n, w, r, p1, c);
                    var sim2 = new DynamoPerformer(n, w, r, p2, c);
                    var sim3 = new DynamoPerformer(n, w, r, p3, c);
                    
                    sim1.simulate(100_000, 1);
                    sim2.simulate(100_000, 1);
                    sim3.simulate(100_000, 1);

                    valuesW.add(w);
                    valuesVP1.add(sim1.getAvgVerProfit());
                    valuesVP2.add(sim2.getAvgVerProfit());
                    valuesVP3.add(sim3.getAvgVerProfit());

                    print("w = " + w + ", avgVerProfit [1] [2] [3] : [" + sim1.getAvgVerProfit() + 
                                                                  "] [" + sim2.getAvgVerProfit() + 
                                                                  "] [" + sim3.getAvgVerProfit() + "]");
                }

                CSVHandler.createCSV("outW", valuesW);
                CSVHandler.createCSV("outVP1", valuesVP1);
                CSVHandler.createCSV("outVP2", valuesVP2);
                CSVHandler.createCSV("outVP3", valuesVP3);

                FigureSettings settings2 = new FigureSettings(3);

                settings2.setTitle("Графики verProfit в зависимости от размера кворума w при разных p");
                settings2.setAxisX("Размер кворума записи w, слотов");
                settings2.setAxisY("verProfit, узлов");

                settings2.addGraphicParameters("p = " + p1, "k", "-", "o", 0);
                settings2.addGraphicParameters("p = " + p2, "r", "-", "o", 0);
                settings2.addGraphicParameters("p = " + p3, "g", "-", "o", 0);

                settings2.saveJSON();

                LinearFigure.plot("outW", "outVP1", "outVP2", "outVP3");
            }

            case VP_AoI_W_sc -> {
                List<Object> valuesW = new LinkedList<>();              
                List<Object> valuesVP = new LinkedList<>();     // для построения графика verProfit, ось Y
                List<Object> valuesAoI = new LinkedList<>();    // для построения графика AoI, ось Y

                n = 100;
                r = 10;
                c = 100;
                p = 0.4;

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
                    
                    sim.simulate(400_000, 1);

                    valuesW.add(w);
                    valuesVP.add(sim.getAvgVerProfit());
                    valuesAoI.add(sim.getAvgAOI());

                    print("w = " + w + ", [VP] [AoI] : [" + sim.getAvgVerProfit() + 
                                                    "] [" + sim.getAvgAOI() + "]");
                }

                CSVHandler.createCSV("outW", valuesW);
                CSVHandler.createCSV("outVP", valuesVP);
                CSVHandler.createCSV("outAoI", valuesAoI);

                FigureSettings settings = new FigureSettings(2);

                settings.setTitle("Графики избыточности и AoI в зависимости от размера кворума записи w");
                settings.setAxisX("Размер кворума записи w, слотов");
                settings.setAxisY("Избыточность, узлов; AoI, слотов");

                settings.addGraphicParameters("Избыточность", "k", "-", "o", 0);
                settings.addGraphicParameters("AoI", "r", "-", "o", 0);

                settings.saveJSON();

                LinearFigure.plot("outW", "outVP", "outAoI");
            }

            /**
             * График среднего возраста информации от увеличения w при постоянных n, r, p
             * для случая, когда чтение происходит только во время задержки
             */
            case AoI_W_ReadC -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                // параметры системы
                n = 100;
                r = 20;
                p = 0.01;
                c = 100;

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
                    sim.simulateReadC(100_000, 1);

                    valuesW.add(w);
                    valuesAoI.add(sim.getAvgAOI());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }
                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY", valuesAoI);

                FigureSettings settings = new FigureSettings(1);
                settings.setTitle("График AoI при увеличении w (чтение только во время задержки)");
                settings.setAxisX("w");
                settings.setAxisY("AoI");
                settings.addGraphicParameters("p = " + p, "k", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY");
            }
        }
        
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
