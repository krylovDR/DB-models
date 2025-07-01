package simulation;

import core.dynamo.DynamoBase;

public final class DynamoPerformer {
    private DynamoBase dBase;               // экземпляр Dynamo-РСХД

    private double avgAoI;                  // средний возраст информации в системе
    private double avgVersionAge;           // среднее время жизни обновления
    private double avgVerProfit;            // средняя избыточность обновлённых узлов

    /**
     * Конструктор для симуляции.
     * 
     * @param n - общее количество узлов,
     * @param w - количество узлов в кворуме записи
     * @param r - количество узлов в кворуме чтения
     * @param q - вероятность успешной записи на отдельный узел
     * @param c - количество слотов задержки инициализации нового обновления
     */
    public DynamoPerformer(int n, int w, int r, double q, int c) {
        dBase = new DynamoBase(n, w, r, q, c);

        avgAoI = 0.0;
        avgVersionAge = 0.0;
        avgVerProfit = 0.0;
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

        for (int curSlot = 0; curSlot < numSlots; curSlot++) {
            dBase.doWrite();
            if (curSlot % readPeriod == 0) {
                avgAoI += curSlot - dBase.doRead();     // подсчёт возраста информации
                numExp++;
            }
            avgVerProfit = avgVerProfit + dBase.getVerProfit();    // подсчёт среднего verProfit
            
            dBase.nextSlot();
        }
        avgAoI = avgAoI / numExp;
        avgVersionAge = numSlots / dBase.getActualVersion();
        avgVerProfit = avgVerProfit / dBase.getActualVersion();
        
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
     * Получение среднее значение избыточности обновлённых узлов после заврешения обновления.
     * @return {@code double} - среднее значение избыточности обновлённых узлов (в слотах)
     */
    public double getAvgVerProfit() {
        return avgVerProfit;
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
                               builder.q,
                               builder.c);
    }

    // паттерн Builder для удобного заполнения множества аргументов
    public static class Builder {
        private int n;
        private int w;
        private int r;
        private double q;
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

        public Builder setQ(double q) {
            this.q = q;
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
