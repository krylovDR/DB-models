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
            dBase.doWrite();

            if (dBase.getCurSlot() % readPeriod == 0) {
                avgAoI += dBase.getCurSlot() - dBase.doRead();     // подсчёт возраста информации
                numExp++;
            }
            
            if (curVersion != dBase.getActualVersion()) {
                avgVerProfit += dBase.getVerProfit();    // подсчёт среднего verProfit
                curVersion = dBase.getActualVersion();
            }
        }
        avgAoI = avgAoI / numExp;
        avgVersionAge = numSlots / dBase.getActualVersion();
        avgVerProfit = avgVerProfit / dBase.getActualVersion();
        avgFrameSize = Double.valueOf(numSlots) / Double.valueOf(dBase.getActualVersion())
                - Double.valueOf(dBase.getC());
        
        for (var cur : dBase.getPStats()) {
            versionsProb.add(Double.valueOf((cur / numExp) * 100));
        }
    }


    /**
     * Метод для симуляции работы системы по слотам (чтение происходит только во время задержки).
     *
     * @param numSlots количество слотов симуляции
     * @param readPeriod периодичность операции чтения в системе
     */
    public void simulateReadC(int numSlots, int readPeriod) {
        if (numSlots < 1) throw new RuntimeException("numSlots must be >0");
        if (readPeriod < 1) throw new RuntimeException("readPeriod must be >0");

        int numExp = 0;
        int curVersion = 0;     // текущая версия, нужна для верного подсчёта verProfit

        for (; dBase.getCurSlot() < numSlots;) {
            dBase.nextSlot();
            dBase.doWrite();

            if ((dBase.getCurSlot() % readPeriod == 0) && dBase.getSlotsToWait() != 0) {
                avgAoI += dBase.getCurSlot() - dBase.doRead();     // подсчёт возраста информации
                numExp++;
            }
            
            if (curVersion != dBase.getActualVersion()) {
                avgVerProfit += dBase.getVerProfit();    // подсчёт среднего verProfit
                curVersion = dBase.getActualVersion();
            }
        }
        avgAoI = avgAoI / numExp;
        avgVersionAge = numSlots / dBase.getActualVersion();
        avgVerProfit = avgVerProfit / dBase.getActualVersion();
        avgFrameSize = Double.valueOf(numSlots) / Double.valueOf(dBase.getActualVersion())
                - Double.valueOf(dBase.getC());

        for (var cur : dBase.getPStats()) {
            versionsProb.addLast(cur / Double.valueOf(numExp));
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


    public double getAvgFrameSize() {
        return avgFrameSize;
    }


    public ArrayList<Double> getVersionsProb() {
        return versionsProb;
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
