import java.util.Scanner;
import java.util.Random;

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
        ImList<Twin<Double>> proportions = new ImList<Twin<Double>>();
        ImList<Boolean> careAboutPos = new ImList<Boolean>();
        ImList<Twin<Double>> pos = new ImList<Twin<Double>>();
        int numOfRooms = sc.nextInt();
        int distanceScale = sc.nextInt();
        int proportionScale = sc.nextInt();
        int posScale = sc.nextInt();
        for (int i = 0; i < numOfRooms; ++i) {
            names = names.add(sc.next());
            areas = areas.add(sc.nextDouble());
            proportions = proportions.add(new Twin<Double>(
                        sc.nextDouble(), sc.nextDouble()));
            careAboutPos = careAboutPos.add(
                    Boolean.parseBoolean(sc.next()));
            pos = pos.add(new Twin<Double>(
                        sc.nextDouble(), sc.nextDouble()));
        }
        ImList<Twin<String>> adjacencies = new ImList<Twin<String>>();
        String adjacency = "";
        String room1 = "";
        String room2 = "";
        while (sc.hasNext()) {
            adjacency = sc.next();
            if (!adjacency.contains("&")) {
                break;
            }
            String[] rooms = adjacency.split("&");
            room1 = rooms[0];
            room2 = rooms[1];
            adjacencies = adjacencies.add(new Twin<String>(room1, room2));
        }
        sc.close();
        Simulator simulator = new Simulator(seed, mu, lambda, tournamentSize,
                x, y, names, areas, adjacencies, proportions, careAboutPos, pos,
                distanceScale, proportionScale, posScale);
        ImList<RoomPos> rooms = simulator.evolve();
        Visualiser.visualise(rooms);
    }
}
