import simulation.DynamoPerformer;

public class MainDynamo {
    
    public static void main(String[] args) {
        var sim = new DynamoPerformer(10, 5, 5, 0.9);
        sim.simulate(5);
        print(sim);
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
