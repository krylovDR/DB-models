package simulation;

import core.dynamo.DynamoBase;

import java.util.ArrayList;

public final class DynamoPerformer {
    private DynamoBase dBase;               // экземпляр Dynamo-РСХД

    private double avgAoI;                  // средний возраст информации в системе
    private double avgVersionAge;           // среднее время жизни обновления
    private double avgVerProfit;            // средняя избыточность обновлённых узлов
    private double avgFrameSize;            // средняя длина кадра
    private ArrayList<Double> versionsProb; // вероятности p1...p_max чтения конкретных версий
    
    private boolean showProgress;           // режим вывода прогресса симуляции в реальном времени
    private boolean readAtLatency;          // режим чтения только во время задержки
    private boolean ignoreVP;               // режим с отсутствием избыточности обновлений

    /**
     * Конструктор для симуляции.
     * 
     * @param n - общее количество узлов,
     * @param w - количество узлов в кворуме записи
     * @param r - количество узлов в кворуме чтения
     * @param p - вероятность успешной записи на отдельный узел
     * @param c - количество слотов задержки инициализации нового обновления
     */
    public DynamoPerformer(int n, int w, int r, double p, int c) {
        dBase = new DynamoBase(n, w, r, p, c);

        avgAoI = 0.0;
        avgVersionAge = 0.0;
        avgVerProfit = 0.0;
        avgFrameSize = 0.0;
        versionsProb = new ArrayList<>();

        showProgress = false;
        readAtLatency = false;
        ignoreVP = false;
    }


    /**
     * Метод для симуляции работы системы по слотам.
     *
     * @param numSlots количество слотов симуляции
     * @param readPeriod периодичность операции чтения в системе
     */
    public void simulate(int numSlots, int readPeriod) {
        if (numSlots < 1) throw new RuntimeException("numSlots must be >0");
        if (readPeriod < 1) throw new RuntimeException("readPeriod must be >0");

        int numExp = 0;
        int curVersion = 0;     // текущая версия, нужна для верного подсчёта verProfit

        for (; dBase.getCurSlot() < numSlots;) {
            dBase.nextSlot();

            // чтение в зависимости от режима избыточности
            if (ignoreVP) {
                dBase.doWriteNoVP();
            } else {
                dBase.doWrite();
            }

            // проверка на режим чтения только во время задержки
            if (dBase.getCurSlot() % readPeriod == 0 &&
                    (readAtLatency ? dBase.getSlotsToWait() != 0 : true)) {
                
                avgAoI += dBase.getCurSlot() - dBase.doRead();     // подсчёт возраста информации
                numExp++;
            }
            
            if (curVersion != dBase.getActualVersion()) {
                avgVerProfit += dBase.getVerProfit();    // подсчёт среднего verProfit
                curVersion = dBase.getActualVersion();
            }

            if (showProgress) printStatus(numSlots); // вывод прогресса в консоль
        }
        if (showProgress) System.out.println();
        
        avgAoI = avgAoI / numExp;
        avgVersionAge = dBase.getActualVersion() == 0 ? numSlots : numSlots / dBase.getActualVersion();
        avgVerProfit = avgVerProfit / dBase.getActualVersion();
        avgFrameSize = Double.valueOf(numSlots) / Double.valueOf(dBase.getActualVersion())
                - Double.valueOf(dBase.getC());
        
        for (var cur : dBase.getPStats()) {
            versionsProb.add(cur / Double.valueOf(numExp));
        }
    }


    /**
     * Метод симуляции одного слота работы системы.
     * @param id порядковый номер узла, с которого производится чтение
     * @return возраст информации на узле id
     */
    public int simulateSlot(int id) {
        int res = 0;

        dBase.doWrite();
        res = (int)dBase.getAOI(id);
        dBase.nextSlot();

        return res;
    }


    /**
     * Получение среднего возраста информации в системе
     * 
     * @return {@code double} - средний возраст информации (в слотах)
     */
    public double getAvgAOI() {
        return avgAoI;
    }


    /**
     * Получение среднего времени жизни обновления в системе
     * 
     * @return {@code double} - среднее время жизни обновления (в слотах)
     */
    public double getAvgVersionAge() {
        return avgVersionAge;
    }


    /**
     * Получение среднего значения избыточности обновлённых узлов после заврешения обновления.
     * @return {@code double} - среднее значение избыточности обновлённых узлов (в слотах)
     */
    public double getAvgVerProfit() {
        return avgVerProfit;
    }


    /**
     * Получение среднего значения размера кадра.
     * @return {@code double} - среднее значение размера кадра (в слотах)
     */
    public double getAvgFrameSize() {
        return avgFrameSize;
    }


    /**
     * Получение списка с вероятностями чтения последнего, предпоследнего и более старых версий данных.
     * @return {@code ArrayList<Double>} - список вероятностей
     */
    public ArrayList<Double> getVersionsProb() {
        return versionsProb;
    }


    /**
     * Режим вывода прогресса симуляции в консоль
     * @param value true - выводить, false - не выводить
     */
    public void showProgress(boolean value) {
        showProgress = value;
    }


    /**
     * Режим чтения данных только во время задержки c
     * @param value true - только во время задержки, false - в любой момент
     */
    public void readAtLatency(boolean value) {
        readAtLatency = value;
    }


    /**
     * Режим работы системы без избыточности обновлений (число обновлённых узлов = w)
     * @param value true - исключить избыточность, false - обновлений может быть больше чем w 
     */
    public void ignoreVP(boolean value) {
        ignoreVP = value;
    }


    /**
     * Метод вывода строки состояния процесса симуляции
     * @param numSlots общее число слотов симуляции
     */
    private void printStatus(int numSlots) {
        double progress = (double) dBase.getCurSlot() / numSlots * 100.0;
        int filled = (int) (progress);   // длина заполненной части (макс. 50)
        int empty = 100 - filled;             // длина пустой части

        // Формируем полосу прогресса длиной 50 символов
        String bar = "■".repeat(filled) + " ".repeat(empty);

        // Выводим всё в одной строке с двумя спецификаторами: %f и %s
        System.out.printf("\rProgress: %6.2f%% [%s]", progress, bar);
        System.out.flush();
    }


    @Override
    public String toString() {
        return dBase.toString();
    }


    // ============================================================================================


    // конструктор через Builder
    private DynamoPerformer(Builder builder) {
        dBase = new DynamoBase(builder.n,
                               builder.w,
                               builder.r,
                               builder.p,
                               builder.c);
    }

    // паттерн Builder для удобного заполнения множества аргументов
    public static class Builder {
        private int n;
        private int w;
        private int r;
        private double p;
        private int c;

        public Builder setN(int n) {
            this.n = n;
            return this;
        }

        public Builder setW(int w) {
            this.w = w;
            return this;
        }

        public Builder setR(int r) {
            this.r = r;
            return this;
        }

        public Builder setP(double p) {
            this.p = p;
            return this;
        }

        public Builder setC(int c) {
            this.c = c;
            return this;
        }

        public DynamoPerformer build() {
            return new DynamoPerformer(this);
        }
    }
}
