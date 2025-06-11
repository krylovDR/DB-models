package core.dynamo;

import java.util.ArrayList;

public class DynamoBase implements IDynamo {
    private int n;      // количество узлов в системе
    private int w;      // размер кворума записи
    private int r;      // размер кворума чтения
    private double q;   // вероятность успешной записи

    private final ArrayList<Node> nodes;  // все узлы


    /**
     * Конструктор РСХД Динамо-типа.
     *
     * @param n общее количество узлов,
     * @param w количество узлов в кворуме записи
     * @param r количество узлов в кворуме чтения
     * @param q вероятность успешной записи на отдельный узел
     */
    public DynamoBase (int n, int w, int r, double q) {
        if (n <= 0) throw new RuntimeException("Error: expected n > 0");
        if (w <= 0 || w > n) throw new RuntimeException("Error: expected 0 < w <= n");
        if (r <= 0 || r > n) throw new RuntimeException("Error: expected 0 < r <= n");
        if (q < 0.0 || q > 1.0) throw new RuntimeException("Error: expected 0 <= q <= 1");
        
        this.n = n;
        this.w = w;
        this.r = r;
        this.q = q;

        nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nodes.add(new Node(i));
        }
    }


    /**
     * Однократное моделирование всего процесса записи на узлы кворума W.
     */
    @Override
    public void doWrite() {

    }


    /**
     * Однократное моделирование всего процесса чтения с узлов кворума R.
     *
     * @return {@code int} - самая свежая временная метка из множества узлов
     *         кворума чтения
     */
    @Override
    public int doRead() {
        return 0;
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
        return false;
    }


    /**
     * Отправляет запрос на чтение в узел id.
     *
     * @param id номер узла, куда отправится запрос
     * @return {@code int} - временная метка данных на узле id
     */
    @Override
    public int readRequest(int id) {
        return 0;
    }


    /**
     * Получение возраста информации по запросу на чтение.
     *
     * @return {@code double} - возраст информации
     */
    @Override
    public double getAOI() {
        return 0.0;
    }


    /**
     * Получение возраста информации узла id.
     *
     * @param id номер узла
     * @return {@code double} - возраст информации на узле id
     */
    @Override
    public double getAOI(int id) {
        return 0.0;
    }


    @Override
    public String toString() {
        var sb = new StringBuilder("==== DynamoBase State: ====\n");
        sb.append("[n = " + n + ", ");
        sb.append("w = " + w + ", ");
        sb.append("r = " + r + ", ");
        sb.append("q = " + q + "]");

        sb.append("\n\n==== Nodes: ====\n");
        nodes.forEach(value -> sb.append(value.toString() + "\n"));

        return sb.toString();
    }


    // класс для описания отдельного узла
    private static class Node {
        private int id;         // номер узла
        private int timeStamp;  // временная метка хранящегося обновления

        private Node(int id) {
            this.id = id;
            timeStamp = 0;
        }

        @Override
        public String toString() {
            return "id: " + id + ", timeStamp: " + timeStamp;
        }
    }
}
