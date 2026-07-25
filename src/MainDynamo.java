
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import graphics.FigureSettings;
import graphics.LinearFigure;
import io.CSVHandler;
import simulation.DynamoPerformer;

public class MainDynamo {

    private enum Mode {
        // ЧТЕНИЕ В ЛЮБОМ СЛОТЕ
            // построение графиков AoI

            AoI_R,              // график AoI (среднего возраста информации) в зависимости от r (кворума чтения)
            AoI_W,              // график AoI в зависимости от w (кворума записи)
            AoI_W_R_equal_C,    // 3 графика AoI от w, при трёх значениях r (при этом c = r)

            // специфичные задачи

            AoI_NODE,           // процесс изменения возраста информации на узле за 1000 слотов

            // для небольших статей (WECONF-2026, Вопросы радиоэлектроники)
            
            VP_P_sc,            // график verProfit в зависимости от p при разных w
            VP_W_sc,            // график verProfit в зависимости от w при разных p
            VP_AoI_W_sc,        // 2 графика: verProfit (все обновившиеся узлы - w) и AoI в зависимости от w

            AoI_C_sc,           // график AoI в зависимости от задержки c, а также наличие смещения минимума с
                                // изменением w при разных c


        // ЧТЕНИЕ ТОЛЬКО ВО ВРЕМЯ ЗАДЕРЖКИ C
            // построение графиков AoI
        
            AoI_W_R5_R25,       // 2 графика AoI в зависимости от w (при r = 5 и 25)

            AoI_W_ReadC,        // график AoI в зависимости от w (чтение во время задержки)
            AoI_W_teorW,        // 2 графика AoI в зависимости от w (моделирование и теория)

            // средняя длина кадра Ew

            Ew,                 // вычисление средней длины кадра (Ew)
            Ew_P,               // графики Ew от p (вероятности успешной записи)
            Ew_AoI_W,           // 2 графика: AoI и Ew в зависимости от w

            // рассчёты

            VER_PROBS,          // расчёт среднего возраста информации (теория) и вероятностей чтения отдельных версий
                                // операцией чтения
            AoI_SPLIT,          // расчёт среднего возраста информации используя большее количество экспериментов и
                                // меньшее количество слотов
            AoI_THREADS,        // многопоточное вычисление среднего возраста информации для различных наборов параметров
            AoI_THREADS_INFO    // многопоточное вычисление AoI с отслеживанием прогресса
    }

    public static final Mode mode = Mode.VER_PROBS;
    
    public static void main(String[] args) {
        int n = 100;                // количество узлов в системе
        int w = 20;                 // количество узлов в кворуме записи
        int r = 20;                 // количество узлов в кворуме чтения
        double p = 0.5;             // вероятность успешной записи
        int c = 100;                // количество слотов задержки инициализации нового обновления

        switch (mode) {

            /**
             * График среднего возраста информации от увеличения r при постоянных n, w, p
             */
            case AoI_R -> {
                List<Object> valuesR = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                // параметры системы
                n = 100;
                w = 20;
                p = 0.01;
                c = 100;

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

                // параметры системы
                n = 100;
                r = 20;
                p = 0.01;
                c = 100;

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
            case AoI_W_R5_R25 -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI1 = new LinkedList<>();       // для построения графика AoI, ось Y
                List<Object> valuesAoI2 = new LinkedList<>();       // для построения графика AoI, ось Y

                // параметры системы
                n = 100;
                r = 5;
                p = 0.01;
                c = 100;

                // увеличение w от 1 до n при первом значении r
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
                    sim.simulateReadC(100_000, 1);

                    valuesW.add(w);
                    valuesAoI1.add(sim.getAvgAOI());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }

                r = 25;
                print("\n==================== r = " + r + " ====================\n");
                // увеличение w от 1 до n при втором значении r
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
                    sim.simulateReadC(100_000, 1);

                    valuesAoI2.add(sim.getAvgAOI());
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }

                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY1", valuesAoI1);
                CSVHandler.createCSV("outY2", valuesAoI2);

                FigureSettings settings = new FigureSettings(2);
                settings.setTitle("");
                settings.setAxisX("w, узлов");
                settings.setAxisY("Средний возраст информации, слотов");
                settings.addGraphicParameters("r = 5", "k", "-", "o", 0);
                settings.addGraphicParameters("r = 25", "r", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY1", "outY2");
            }

            /**
             * График изменения возраста информации на узле в течении 1000 слотов
             */
            case AoI_NODE -> {
                List<Object> valuesSlots = new LinkedList<>();      // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y

                // параметры системы
                n = 100;
                w = 20;
                r = 20;
                p = 0.01;
                c = 100;

                int id = 4;  // номер узла для чтения

                var sim = new DynamoPerformer(n, w, r, p, c);

                // пропускаем 10000 слотов до стабильного состояния системы
                for (int i = 0; i < 10000; i++) sim.simulateSlot(id);

                // подсчёт возраста информации в 1000 слотах на узле id
                for (int slot = 0; slot < 1000; slot++) {
                    valuesSlots.add(slot + 1);
                    valuesAoI.add(sim.simulateSlot(id));
                }

                CSVHandler.createCSV("outX", valuesSlots);
                CSVHandler.createCSV("outY", valuesAoI);

                FigureSettings settings = new FigureSettings(1);
                settings.setTitle("");
                settings.setAxisX("t, слотов");
                settings.setAxisY("AoI, слотов");
                settings.addGraphicParameters("Возраст информации на " + id + "-м узле", "k", 
                                                                "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY");
            }

            /*
            * Построение 3-х графиков AoI от w, при трёх значениях r (при этом c = r)
            */
            case AoI_W_R_equal_C -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков, ось X
                List<Object> valuesAoI1 = new LinkedList<>();       // для графика при r = c = 2, ось Y
                List<Object> valuesAoI2 = new LinkedList<>();       // для графика при r = c = 10, ось Y
                List<Object> valuesAoI3 = new LinkedList<>();       // для графика при r = c = 40, ось Y

                n = 100;
                p = 0.01;

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
                settings.addGraphicParameters("r = " + r1, "k", "-", "o", 0);
                settings.addGraphicParameters("r = " + r2, "r", "-", "o", 0);
                settings.addGraphicParameters("r = " + r3, "b", "-", "o", 0);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY1", "outY2", "outY3");
            }

            /* 
            * Вычисление средней длины кадра Ew
            */
            case Ew -> {
                n = 10;
                w = 4;
                r = 3;
                p = 0.1;
                c = 100;

                var sim = new DynamoPerformer(n, w, r, p, c);
                sim.simulateReadC(1_000_000, 1);

                print("Средняя длина кадра при n = " + n + "; w = " + w + "; p = " + p);
                print("avgFrameSize = " + sim.getAvgFrameSize());
            }

            /*
            * График средней длины кадра в зависимости от вероятности успешной доставки
            */
            case Ew_P -> {
                List<Object> valuesP = new LinkedList<>();        // для построения графиков, ось X
                List<Object> valuesE1 = new LinkedList<>();       // для графика, ось Y
                List<Object> valuesE2 = new LinkedList<>();       // для графика, ось Y
                List<Object> valuesE3 = new LinkedList<>();       // для графика, ось Y
                List<Object> valuesE4 = new LinkedList<>();       // для графика, ось Y

                // параметры системы
                n = 10;
                r = 20;
                c = 100;

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
                    
                    sim1.simulateReadC(500_000, 1);
                    System.out.print("p = " + p + ";  1...");

                    sim2.simulateReadC(500_000, 1);
                    System.out.print("2...");

                    sim3.simulateReadC(500_000, 1);
                    System.out.print("3...");

                    sim4.simulateReadC(500_000, 1);
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
            case Ew_AoI_W -> {
                List<Object> valuesW = new LinkedList<>();          // для построения графиков,    ось X
                List<Object> valuesAoI = new LinkedList<>();        // для построения графика AoI, ось Y
                List<Object> valuesEw = new LinkedList<>();         // для построения графика Ew,  ось Y

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

            /*
            * график AoI в зависимости от задержки c, а также наличие смещения минимума с
            * изменением w при разных c
            */
            case AoI_C_sc -> {
                List<Object> valuesC = new LinkedList<>();              // для построения графиков,      ось X
                List<Object> valuesAoI1 = new LinkedList<>();           // для построения графика AoI 1, ось Y
                List<Object> valuesAoI2 = new LinkedList<>();           // для построения графика AoI 2, ось Y
                List<Object> valuesAoI3 = new LinkedList<>();           // для построения графика AoI 3, ось Y

                // параметры системы
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

            /*
            * график verProfit в зависимости от p при разных w
            */
            case VP_P_sc -> {
                List<Object> valuesP = new LinkedList<>();      // для построения графиков,            ось X
                List<Object> valuesVP1 = new LinkedList<>();    // для построения графика verProfit 1, ось Y
                List<Object> valuesVP2 = new LinkedList<>();    // для построения графика verProfit 2, ось Y
                List<Object> valuesVP3 = new LinkedList<>();    // для построения графика verProfit 3, ось Y

                // параметры системы
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

            /*
            * график verProfit с изменением w при разных p
            */
            case VP_W_sc -> {
                List<Object> valuesW = new LinkedList<>();              
                List<Object> valuesVP1 = new LinkedList<>();    // для построения графика verProfit 1, ось Y
                List<Object> valuesVP2 = new LinkedList<>();    // для построения графика verProfit 2, ось Y
                List<Object> valuesVP3 = new LinkedList<>();    // для построения графика verProfit 3, ось Y

                // параметры системы
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

            /*
            * график verProfit и AoI с изменением w
            */
            case VP_AoI_W_sc -> {
                List<Object> valuesW = new LinkedList<>();              
                List<Object> valuesVP = new LinkedList<>();     // для построения графика verProfit, ось Y
                List<Object> valuesAoI = new LinkedList<>();    // для построения графика AoI, ось Y

                // параметры системы
                n = 100;
                r = 10;
                c = 100;
                p = 0.23;

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
                    
                    sim.simulate(1_000_000, 1);

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

                settings.setTitle("");
                settings.setAxisX("Размер кворума записи w, узлы");
                settings.setAxisY("Средняя избыточность обновлений, узлы");

                settings.addGraphicParameters("Средняя избыточность обновлений", "k", "-", "o", 0);
                settings.addGraphicParameters("Средний возраст информации", "r", "--", "o", 0);

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
                    sim.simulateReadC(10_000, 1);

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

            /*
            * 2 графика AoI в зависимости от w (моделирование и теория через вероятности)
            */
            case AoI_W_teorW -> {
                List<Object> valuesW = new LinkedList<>();               // для построения графиков, ось X
                List<Object> valuesAoI = new LinkedList<>();             // для построения графика AoI, ось Y
                List<Object> valuesTheorAoI = new LinkedList<>();        // для построения графика AoI (теория), ось Y

                // параметры системы
                n = 100;
                r = 5;
                p = 0.3;
                c = 120;

                // увеличение w от 1 до n
                for (w = 1; w <= n; w++) {
                    var sim = new DynamoPerformer(n, w, r, p, c);
                    sim.simulateReadC(500_000, 1);

                    valuesW.add(w);
                    valuesAoI.add(sim.getAvgAOI());
                    valuesTheorAoI.add(calculateAoI(n, w, r, p, c, sim.getAvgFrameSize(), sim.getVersionsProb()));
                    print("w = " + w + ", avg AoI: " + sim.getAvgAOI());
                }
                CSVHandler.createCSV("outX", valuesW);
                CSVHandler.createCSV("outY", valuesAoI);
                CSVHandler.createCSV("outY2", valuesTheorAoI);

                FigureSettings settings = new FigureSettings(2);
                settings.setTitle("");
                settings.setAxisX("Размер кворума записи w, узлов");
                settings.setAxisY("Средний возраст информации, слотов");
                settings.addGraphicParameters("Моделирование", "r", "-", "o", 0);
                settings.addGraphicParameters("Теоретический расчёт (new)", "b", "--", "x", 3);
                settings.saveJSON();

                LinearFigure.plot("outX", "outY", "outY2");
            }

            /**
             * Расчёт среднего возраста информации (моделирование и формула), 
             * распределение вероятностей чтения каждой версии операцией чтения
             */
            case VER_PROBS -> {

                // параметры системы
                n = 100;
                w = 20;
                r = 20;
                p = 0.01;
                c = 100;

                var sim = new DynamoPerformer(n, w, r, p, c);
                sim.simulateReadC(1_000_000, 1);

                print("[n = " + n + ", w = " + w + ", r = " + r + ", p = " + p + ", c = " + c + "]\n");

                print("AoI: " + sim.getAvgAOI());
                print("AoI theoretical: " + calculateAoI(n, w, r, p, c, sim.getAvgFrameSize(), sim.getVersionsProb()));
                
                print("\nverProb:");
                int i = 1;
                for (Double cur : sim.getVersionsProb()) {
                    print("p_" + i + " = " + cur);
                    i++;

                    if (i == 101) break;
                }
            }

            /*
            * Расчёт среднего возраста информации используя большее количество экспериментов и
            * меньшее количество слотов
            */
            case AoI_SPLIT -> {
                int numExp = 500;
                int numSlots = 40000;

                // наборы параметров для экспериментов
                record Params (int n, int r, double p, int c) {}
                var paramList = new ArrayList<Params>();

                paramList.add(new Params(100, 20, 0.01, 100));
                paramList.add(new Params(100, 20, 0.15, 100));
                paramList.add(new Params(100, 20, 0.4, 100));
                paramList.add(new Params(100, 5, 0.01, 100));
                paramList.add(new Params(50, 20, 0.01, 100));
                paramList.add(new Params(100, 20, 0.01, 150));

                List<Object> valuesAoI = new LinkedList<>();
                
                for (var cur : paramList) {
                    print(cur);

                    for (w = 1; w <= cur.n(); w++) {
                        double tempSum = 0.0;
                        
                        for (int i = 0; i < numExp; i++) {
                            var sim = new DynamoPerformer(cur.n(), w, cur.r(), cur.p(), cur.c());
                            sim.simulateReadC(numSlots, 1);
                            
                            tempSum += sim.getAvgAOI();
                            printStatus("w = " + w, i, numExp);
                        }

                        valuesAoI.add(tempSum / (double) numExp);

                        print("avg AoI: " + valuesAoI.get(w - 1));
                    }
                    CSVHandler.createCSV(cur.toString(), valuesAoI);

                    valuesAoI.clear();
                }
            }

            /*
            * Многопоточное вычисление среднего возраста информации для различных наборов параметров
            */
            case AoI_THREADS -> {
                int numExp = 100;
                int numSlots = 10000;

                // наборы параметров для экспериментов
                record Params(int n, int r, double p, int c) {}
                var paramList = new ArrayList<Params>();

                paramList.add(new Params(100, 20, 0.01, 100));
                paramList.add(new Params(100, 20, 0.15, 100));
                paramList.add(new Params(100, 20, 0.4, 100));
                paramList.add(new Params(100, 5, 0.01, 100));
                paramList.add(new Params(50, 20, 0.01, 100));
                paramList.add(new Params(100, 20, 0.01, 150));

                for (var cur : paramList) {
                    print(cur);
                    double[] results = new double[cur.n()];   // результаты для каждого w

                    // Создаём пул потоков (размер не более числа доступных процессоров, но не больше n)
                    int threads = Math.min(cur.n(), Runtime.getRuntime().availableProcessors());
                    ExecutorService executor = Executors.newFixedThreadPool(threads);
                    List<Future<?>> futures = new ArrayList<>();

                    for (w = 1; w <= cur.n(); w++) {
                        final int wIndex = w;
                        Future<?> future = executor.submit(() -> {
                            double sum = 0.0;
                            for (int i = 0; i < numExp; i++) {
                                var sim = new DynamoPerformer(cur.n(), wIndex, cur.r(), cur.p(), cur.c());
                                sim.simulateReadC(numSlots, 1);
                                sum += sim.getAvgAOI();
                            }
                            double avg = sum / (double) numExp;
                            results[wIndex - 1] = avg;   // запись в свою ячейку – безопасно
                        });
                        futures.add(future);
                    }

                    // Ожидаем завершения всех задач
                    for (Future<?> f : futures) {
                        try {
                            f.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    executor.shutdown();

                    // Формируем список значений в порядке возрастания w
                    List<Object> valuesAoI = new ArrayList<>(cur.n());
                    for (double v : results) {
                        valuesAoI.add(v);
                    }

                    // Выводим средние значения (как в оригинале, но после завершения всех потоков)
                    for (w = 1; w <= cur.n(); w++) {
                        System.out.println("w = " + w + ", avg AoI: " + results[w - 1]);
                    }

                    // Сохраняем в CSV
                    CSVHandler.createCSV(cur.toString(), valuesAoI);
                }
            }

            /*
            * Многопоточное вычисление AoI с отслеживанием прогресса выполнения
            */
            case AoI_THREADS_INFO -> {
                int numExp = 100;
                int numSlots = 10000;

                // Вспомогательный класс для возврата результата вместе с номером w
                record Result(int w, double avg) {}

                // наборы параметров для экспериментов
                record Params(int n, int r, double p, int c) {}
                var paramList = new ArrayList<Params>();

                paramList.add(new Params(100, 20, 0.01, 100));
                paramList.add(new Params(100, 20, 0.15, 100));
                paramList.add(new Params(100, 20, 0.4, 100));
                paramList.add(new Params(100, 5, 0.01, 100));
                paramList.add(new Params(50, 20, 0.01, 100));
                paramList.add(new Params(100, 20, 0.01, 150));

                for (var cur : paramList) {
                    print(cur);
                    double[] results = new double[cur.n()];

                    // Пул потоков – не более числа доступных процессоров, но не больше n
                    int threads = Math.min(cur.n(), Runtime.getRuntime().availableProcessors());
                    ExecutorService executor = Executors.newFixedThreadPool(threads);
                    CompletionService<Result> completionService = new ExecutorCompletionService<>(executor);

                    // Отправляем задачи для каждого w
                    for (w = 1; w <= cur.n(); w++) {
                        final int wIndex = w;
                        completionService.submit(() -> {
                            double sum = 0.0;
                            for (int i = 0; i < numExp; i++) {
                                var sim = new DynamoPerformer(cur.n(), wIndex, cur.r(), cur.p(), cur.c());
                                sim.simulateReadC(numSlots, 1);
                                sum += sim.getAvgAOI();
                            }
                            double avg = sum / (double) numExp;
                            return new Result(wIndex, avg);
                        });
                    }

                    // Ожидаем завершения всех задач и выводим прогресс
                    for (int completed = 1; completed <= cur.n(); completed++) {
                        try {
                            Future<Result> future = completionService.take();
                            Result result = future.get();
                            results[result.w() - 1] = result.avg();

                            // Вывод прогресса: сколько задач из n выполнено
                            printStatus("Completed w", completed, cur.n());
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    executor.shutdown();

                    // Переход на новую строку после полосы прогресса
                    System.out.println();

                    // Выводим средние значения для всех w (как в оригинале)
                    for (w = 1; w <= cur.n(); w++) {
                        System.out.println("avg AoI: " + results[w - 1]);
                    }

                    // Сохраняем в CSV
                    List<Object> valuesAoI = new ArrayList<>(cur.n());
                    for (double v : results) {
                        valuesAoI.add(v);
                    }
                    CSVHandler.createCSV(cur.toString(), valuesAoI);
                }
            }
        }
    }


    /*
    * Вычисление среднего возраста информации по теоретической формуле по заданным параметрам
    */
    public static double calculateAoI(int n, int w, int r, double p, int c, double mu, ArrayList<Double> verP) {
        double AoI = mu + ((c - 1) / 2.0);

        for (int i = 1; i < verP.size(); i++) {
            if (verP.get(i) == 0.0) continue;

            AoI += verP.get(i) * i * (mu + c);
        }
        return AoI;
    }

    public static void printStatus(String info, int from, int to) {
        double progress = (double) from / to * 100.0;
        int filled = (int) (progress);        // длина заполненной части (макс. 100)
        int empty = 100 - filled;             // длина пустой части

        // Формируем полосу прогресса длиной 100 символов
        String bar = "■".repeat(filled) + " ".repeat(empty);

        // Выводим всё в одной строке с двумя спецификаторами: %f и %s
        System.out.printf("\r%s: %6.2f%% [%s]", info, progress, bar);
        System.out.flush();
    }

    /*
    * Сокращённый вывод в консоль
    */
    public static void print(Object o) {
        System.out.println(o);
    }
}
