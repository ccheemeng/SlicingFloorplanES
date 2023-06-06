import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        long seed = new Random().nextLong();
        if (sc.hasNext("seed:")) {
            sc.skip("seed:");
            seed = sc.nextLong();
        }
        int mu = sc.nextInt();
        int lambda = sc.nextInt();
        int tournamentSize = sc.nextInt();
        double x = sc.nextDouble();
        double y = sc.nextDouble();
        ImList<String> names = new ImList<String>();
        ImList<Double> areas = new ImList<Double>();
        ImList<Pair<Double>> proportions = new ImList<Pair<Double>>();
        int numOfRooms = sc.nextInt();
        for (int i = 0; i < numOfRooms; ++i) {
            names = names.add(sc.next());
            areas = areas.add(sc.nextDouble());
            proportions = proportions.add(new Pair<Double>(
                        sc.nextDouble(), sc.nextDouble()));
        }
        ImList<Pair<String>> adjacencies = new ImList<Pair<String>>();
        String adjacency = "";
        String room1 = "";
        String room2 = "";
        while (sc.hasNext()) {
            adjacency = sc.next();
            if (adjacency.equals("d")) {
                break;
            }
            String[] rooms = adjacency.split("&");
            room1 = rooms[0];
            room2 = rooms[1];
            adjacencies = adjacencies.add(new Pair<String>(room1, room2));
        }
        sc.close();
        Simulator simulator = new Simulator(seed, mu, lambda, tournamentSize,
                x, y, names, areas, adjacencies, proportions);
        ImList<RoomPos> rooms = simulator.evolve();
        Visualiser.visualise(rooms);
    }
}
