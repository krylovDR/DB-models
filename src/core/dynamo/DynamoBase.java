package core.dynamo;

public class DynamoBase implements IDynamo {
    private int n;      // количество узлов в системе
    private int w;      // размер кворума записи
    private int r;      // размер кворума чтения
    private double q;   // вероятность успешной записи

    public DynamoBase (int n, int w, int r, double q) {
        if (n <= 0) throw new RuntimeException("Error: expected n > 0");
        if (w <= 0 || w > n) throw new RuntimeException("Error: expected 0 < w <= n");
        if (r <= 0 || r > n) throw new RuntimeException("Error: expected 0 < r <= n");
        if (q < 0.0 || q > 1.0) throw new RuntimeException("Error: expected 0 <= q <= 1");
        
        this.n = n;
        this.w = w;
        this.r = r;
        this.q = q;
    }

    @Override
    public void doWrite() {}

    @Override
    public int doRead() {return 0;}

    @Override
    public boolean writeRequest(int id) {return false;}

    @Override
    public int readRequest(int id) {return 0;}

    @Override
    public double getAOI() {return 0.0;}

    @Override
    public double getAOI(int id) {return 0.0;}

    @Override
    public String toString() {
        var sb = new StringBuilder("DynamoBase State:\n");
        sb.append("[n = " + n + ", ");
        sb.append("w = " + w + ", ");
        sb.append("r = " + r + ", ");
        sb.append("q = " + q + "]");

        return sb.toString();
    }
}
