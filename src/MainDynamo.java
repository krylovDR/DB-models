import simulation.DynamoPerformer;

public class MainDynamo {
    
    public static void main(String[] args) {
        var sim1 = new DynamoPerformer(10, 5, 5, 0.8);
        print(sim1);
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
