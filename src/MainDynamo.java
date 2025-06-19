import simulation.DynamoPerformer;

public class MainDynamo {
    
    public static void main(String[] args) {
        var sim = new DynamoPerformer(10, 5, 5, 0.4);
        sim.simulate(1_000_000, 1);
        print(sim);
        print("Average AoI: " + sim.getAvgAOI());
    }

    public static void print(Object o) {
        System.out.println(o);
    }
}
