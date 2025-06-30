package simulation;

import java.util.LinkedList;
import java.util.List;

import core.dynamo.DynamoBase;

public final class DynamoPerformer {
    private DynamoBase dBase;               // экземпляр Dynamo-РСХД

    private double avgAoI;                  // средний возраст информации в системе
    private List<Object> verProfitList;     // List для verProfit
    private List<Object> slots;             // List для номеров слотов

    /**
     * Конструктор для симуляции.
     * 
     * @param n общее количество узлов,
     * @param w количество узлов в кворуме записи
     * @param r количество узлов в кворуме чтения
     * @param q вероятность успешной записи на отдельный узел
     */
    public DynamoPerformer(int n, int w, int r, double q) {
        dBase = new DynamoBase(n, w, r, q);

        avgAoI = 0.0;
        verProfitList = new LinkedList<>();
        slots = new LinkedList<>();
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
                avgAoI += curSlot - dBase.doRead();
                numExp++;
            }
            verProfitList.add(dBase.getVerProfit());    // подсчёт verProfit в List
            slots.add(curSlot);                         // подсчёт слотов в List
            
            dBase.nextSlot();
        }
        avgAoI = avgAoI / numExp;
    }


    /**
     * Получение списка verProfit
     * 
     * @return {@code List<Object>} - verProfit (в слотах)
     */
    public List<Object> getVerProfitList() {
        return verProfitList;
    }


    /**
     * Получение номеров слотов в виде List
     * 
     * @return {@code List<Object>} - список номеров слотов
     */
    public List<Object> getSlotsList() {
        return slots;
    }


    /**
     * Получение среднего возраста информации в системе
     * 
     * @return {@code double} - средний возраст информации (в слотах)
     */
    public double getAvgAOI() {
        return avgAoI;
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
                               builder.q);
    }

    // паттерн Builder для удобного заполнения множества аргументов
    public static class Builder {
        private int n;
        private int w;
        private int r;
        private double q;

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

        public DynamoPerformer build() {
            return new DynamoPerformer(this);
        }
    }
}
