package core.dynamo;

import java.util.ArrayList;
import java.util.Collections;

public class DynamoBase implements IDynamo {
    private int n;      // количество узлов в системе
    private int w;      // размер кворума записи
    private int r;      // размер кворума чтения
    private double p;   // вероятность успешной записи
    private int c;      // количество слотов задержки инициализации нового обновления

    private final ArrayList<Node> nodes;    // все узлы
    private int slotsToWait;                // счётчик слотов бездействия

    private final ArrayList<Integer> pStats;  // количество чтений i-й версии в системе (для подсчёта вероятностей
                                              // p1, p2, ..., p_max, - от более актульных версий к менее)
    private int actualTimeStamp;    // временная метка акутальной версии данных в системе
    private int verProfit;          // на сколько успешных записей было больше, чем w
    private int actualVersion;      // актуальный номер обновления (для расчёта среднего времени жизни обновления)
    private int curSlot;            // номер текущего слота в системе


    /**
     * Конструктор РСХД Динамо-типа.
     *
     * @param n - общее количество узлов,
     * @param w - количество узлов в кворуме записи
     * @param r - количество узлов в кворуме чтения
     * @param p - вероятность успешной записи на отдельный узел
     * @param c - количество слотов задержки инициализации нового обновления
     */
    public DynamoBase (int n, int w, int r, double p, int c) {
        if (n <= 0) throw new RuntimeException("Error: expected n > 0");
        if (w <= 0 || w > n) throw new RuntimeException("Error: expected 0 < w <= n");
        if (r <= 0 || r > n) throw new RuntimeException("Error: expected 0 < r <= n");
        if (p < 0.0 || p > 1.0) throw new RuntimeException("Error: expected 0 <= p <= 1");
        if (c < 0) throw new RuntimeException("Error: expected c >= 0");
        
        this.n = n;
        this.w = w;
        this.r = r;
        this.p = p;
        this.c = c;

        nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nodes.add(new Node(i));
        }

        slotsToWait = 0;

        pStats = new ArrayList<>();
        actualTimeStamp = 0;
        verProfit = 0;
        actualVersion = 0;
        curSlot = 0;
    }


    /**
     * Однократное моделирование всего процесса записи на узлы кворума W внутри слота.
     */
    @Override
    public void doWrite() {
        if (slotsToWait != 0) return;

        for (int i = 0; i < n; i++) {
            writeRequest(i);
        }
    }


    /**
     * Однократное моделирование всего процесса записи на узлы кворума W внутри слота
     * (избыточность обновлений исключена, возможное число записей <w).
     */
    public void doWriteNoVP() {
        if (slotsToWait != 0) return;

        for (int i = 0; i < n; i++) {
            if (isUpdateComplete()) return;
            writeRequest(i);
        }
    }


    /**
     * Отправляет запрос на запись в узел id.
     *
     * @param id номер узла, куда отправится запрос
     * @return {@code true} - запись прошла успешно,
     *         {@code false} - записи не было
     */
    @Override
    public boolean writeRequest(int id) {
        if (p >= Math.random()) {
            nodes.get(id).timeStamp = actualTimeStamp;
            nodes.get(id).versionNum = actualVersion;
            return true;
        }
        return false;
    }
    

    /**
     * Метод перехода к следующему слоту с проверками состояния системы.
     */
    public void nextSlot() {
        curSlot++;

        if (slotsToWait != 0) {
            slotsToWait--;

            if (slotsToWait == 0)
                actualTimeStamp = curSlot;  // временная метка текущего обновления
            return;
        }

        if (isUpdateComplete()) {
            actualVersion++;           // номер текущего обновления
            pStats.add(0);          // добавление новой версии, которая может быть получена чтением
            slotsToWait = c;          // начало задержки инициализации будущего обновления
        }
    }


    /**
     * Однократное моделирование всего процесса чтения с узлов кворума R.
     *
     * @return {@code int} - самая свежая временная метка из множества узлов
     *         кворума чтения
     */
    @Override
    public int doRead() {
        var mixedID = new ArrayList<Integer>(n);
        for (int i = 0; i < n; i++) {
            mixedID.add(i);
        }
        Collections.shuffle(mixedID);

        int max = 0;
        int readedVersionNum = 0;   // номер версии, которую возвратит операция чтения

        for (int i = 0; i < r; i++) {
            int curTS = readRequest(mixedID.get(i));
            if (curTS > max) {
                max = curTS;
                readedVersionNum = nodes.get(mixedID.get(i)).versionNum;  // для расчёта вероятностей p1...p_max
            }
        }

        if (readedVersionNum > 0) {
            int index = readedVersionNum - 1;
            
            if (index < pStats.size()) {
                pStats.set(index, pStats.get(index) + 1);
            } else {
                // на случай рассинхронизации – добавить недостающие элементы
                while (pStats.size() <= index) {
                    pStats.add(0);
                }
                pStats.set(index, 1);
            }
        }

        return max;
    }


    /**
     * Отправляет запрос на чтение в узел id.
     *
     * @param id номер узла, куда отправится запрос
     * @return {@code int} - временная метка данных на узле id
     */
    @Override
    public int readRequest(int id) {
        return nodes.get(id).timeStamp;
    }


    /**
     * Получение возраста информации узла id по запросу.
     *
     * @param id номер узла
     * @return {@code double} - возраст информации на узле id
     */
    @Override
    public double getAOI(int id) {
        return curSlot - nodes.get(id).timeStamp;
    }


    /**
     * Опредеяет записано ли обновление на w и более узлов системы.
     *
     * @return {@code true} - обновление записано на w и более узлов,
     *         {@code false} - менее w узлов обновилось
     */
    public boolean isUpdateComplete() {
        int updated = 0;
        verProfit = 0;

        for (Node node : nodes) {
            if (node.timeStamp == actualTimeStamp) updated++;
        }

        if (updated >= w) {
            verProfit = updated - w;
            return true;
        }
        return false;
    }

    public int getVerProfit() {
        return verProfit;
    }

    public int getActualVersion() {
        return actualVersion;
    }

    public int getCurSlot() {
        return curSlot;
    }

    public int getC() {
        return c;
    }

    public int getSlotsToWait() {
        return slotsToWait;
    }

    public ArrayList<Integer> getPStats() {
        return pStats;
    }


    @Override
    public String toString() {
        var sb = new StringBuilder("==== DynamoBase State: ====\n");
        sb.append("[n = " + n + ", ");
        sb.append("w = " + w + ", ");
        sb.append("r = " + r + ", ");
        sb.append("p = " + p + ", ");
        sb.append("c = " + c + "]");

        sb.append("\n\nActual timestamp: " + actualTimeStamp);
        sb.append("\nverProfit: " + verProfit);
        sb.append("\nactualVersion: " + actualVersion);
        sb.append("\nCurrent Slot: " + curSlot);

        sb.append("\n\n==== Nodes: ====\n");
        nodes.forEach(value -> sb.append(value.toString() + "\n"));

        return sb.toString();
    }


    // класс для описания отдельного узла
    private static class Node {
        private int id;             // номер узла
        private int timeStamp;      // временная метка хранящегося обновления
        private int versionNum;     // порядковый номер хранимого обновления

        private Node(int id) {
            this.id = id;
            timeStamp = -1;
            versionNum = -1;
        }

        @Override
        public String toString() {
            return "id: " + id + ", timeStamp: " + timeStamp + ", versionNum: " + versionNum;
        }
    }
}
