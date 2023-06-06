import java.util.Random;
import java.util.Comparator;
import java.util.function.Function;

class Simulator {
    private final long seed;
    private final int mu;
    private final int lambda;
    private final int tournamentSize;
    private final double x;
    private final double y;
    private final ImList<String> names;
    private final ImList<Double> areas;
    private final ImList<Pair<String>> adjacencies;
    private final ImList<Pair<Double>> proportions;
    private static final double EPSILON = 1E-15;

    Simulator(long seed, int mu, int lambda, int tournamentSize,
            double x, double y, ImList<String> names, ImList<Double> areas,
            ImList<Pair<String>> adjacencies, ImList<Pair<Double>> proportions) {
        this.seed = seed;
        this.mu = mu;
        this.lambda = lambda;
        this.tournamentSize = tournamentSize;
        this.x = x;
        this.y = y;
        this.names = names;
        this.areas = areas;
        this.adjacencies = adjacencies;
        this.proportions = proportions;
    }

    ImList<RoomPos> evolve() {
        ImList<ImList<String>> population = new ImList<ImList<String>>();
        for (int i = 0; i < this.mu; ++i) {
            population = population.add(startPolExpr());
        }
        Evolver<ImList<String>> evolver = new Evolver<ImList<String>>(
                this.seed, this.mu, this.lambda,
                selector(), mutator(), crossover(), evaluator());
        population = evolver.evolve(population);
        ImList<String> individual = new ImList<String>();
        double fitness = Double.POSITIVE_INFINITY;
        for (ImList<String> polExpr : population) {
            double newFitness = evaluator().apply(polExpr);
            if (newFitness - fitness < EPSILON) {
                fitness = newFitness;
                individual = polExpr;
            }
        }
        return polExprToRoom(individual).construct(this.x, this.y, this.names);
    }

    private Function<ImList<ImList<String>>, Pair<ImList<String>>> selector() {
        return pop -> {
            ImList<ImList<String>> parents = new ImList<ImList<String>>();
            for (int i = 0; i < 2; ++i) {
                ImList<ImList<String>> newPop = pop;
                ImList<ImList<String>> sample = new ImList<ImList<String>>();
                Random r = new Random(this.seed);
                for (int j = 0; j < this.tournamentSize; ++j) {
                    int index = r.nextInt(newPop.size());
                    sample = sample.add(newPop.get(index));
                    newPop = newPop.remove(index);
                }
                Comparator<ImList<String>> cmp = new Comparator<ImList<String>>() {
                    @Override
                    public int compare(ImList<String> x, ImList<String> y) {
                        double costX = evaluator().apply(x);
                        double costY = evaluator().apply(y);
                        if (costX - costY < -EPSILON) {
                            return -1;
                        } else if (costX - costY > EPSILON) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                };
                ImList<ImList<String>> sorted = sample.sort(cmp);
                parents = parents.add(sorted.get(0));
            }
            return new Pair<ImList<String>>(parents.get(0), parents.get(1));
        };
    }

    private Function<ImList<String>, ImList<String>> mutator() {
        return individual -> PolExpr.randomMove(individual);
    }

    private Function<Pair<ImList<String>>, ImList<String>> crossover() {
        return parents -> PolExpr.ccx(parents.first(), parents.second());
    }

    private Function<ImList<String>, Double> evaluator() {
        return individual -> cost(polExprToRoom(individual).construct(
                    this.x, this.y, this.names));
    }

    private double cost(ImList<RoomPos> rooms) {
        return distanceCost(rooms) + proportionCost(rooms);
    }

    private double distanceCost(ImList<RoomPos> rooms) {
        double distances = 0;
        int numOfAdjacencies = this.adjacencies.size();
        if (numOfAdjacencies <= 0) {
            return 0;
        }
        ImList<String> ids = new ImList<String>();
        for (RoomPos room : rooms) {
            ids = ids.add(room.getId());
        }
        ImList<Pair<Integer>> indexedAdjacencies = new ImList<Pair<Integer>>();
        for (Pair<String> adjacency : this.adjacencies) {
            indexedAdjacencies = indexedAdjacencies.add(
                    new Pair<Integer>(ids.indexOf(adjacency.first()),
                        ids.indexOf(adjacency.second())));
        }
        Graph graph = new Graph(rooms.size());
        for (int i = 0; i < rooms.size(); ++i) {
            for (int j = 0; j < rooms.size(); ++j) {
                if (rooms.get(i).adjacent(rooms.get(j))) {
                    graph = graph.addAdjacency(i, j);
                }
            }
        }
        for (Pair<Integer> adjacency : indexedAdjacencies) {
            int start = adjacency.first();
            int end = adjacency.second();
            distances += graph.distance(start, end) - 1;
        }
        return distances / numOfAdjacencies;
    }

    private double proportionCost(ImList<RoomPos> rooms) {
        double costs = 0;
        ImList<Integer> ids = new ImList<Integer>();
        for (RoomPos room : rooms) {
            ids = ids.add(this.names.indexOf(room.getId()));
        }
        for (int i = 0; i < this.proportions.size(); ++i) {
            Pair<Double> proportion = this.proportions.get(i);
            RoomPos room = rooms.get(ids.indexOf(i));
            double x = proportion.first();
            double y = proportion.second();
            if (x == 0 || y == 0) {
                continue;
            }
            double roomX = room.getX();
            double roomY = room.getY();
            double roomArea = roomX * roomY;
            double totalArea = this.x * this.y;
            double cost = ((totalArea - roomArea) / totalArea) *
                (Math.min(Math.max((x / y) / (roomY / roomX),
                    (roomY / roomX) / (x / y)),
                  Math.max((x / y) / (roomX / roomY),
                    (roomX / roomY) / (x / y))) - 1);
            costs += cost;
        }
        return costs / rooms.size();
    }

    private ImList<String> startPolExpr() {
        int numOfOperands = this.names.size();
        ImList<Integer> ids = new ImList<Integer>();
        for (int i = 0; i < numOfOperands; ++i) {
            ids = ids.add(i);
        }
        Random r = new Random(seed);
        ImList<String> output = new ImList<String>();
        boolean addOperand = false;
        boolean addH = false;
        boolean prevH = false;
        boolean prevV = false;
        int index = -1;
        for (int i = 0; i < 2; ++i) {
            index = r.nextInt(ids.size());
            output = output.add(ids.get(index) + "");
            ids = ids.remove(index);
        }
        int operatorsLeft = 1;
        for (int i = 0; i < 2 * numOfOperands - 4; ++i) {
            addOperand = r.nextBoolean();
            addH = r.nextBoolean();
            if (operatorsLeft <= 0 ||
                    (addOperand && ids.size() > 0)) {
                index = r.nextInt(ids.size());
                output = output.add(ids.get(index) + "");
                ids = ids.remove(index);
                prevH = false;
                prevV = false;
                ++operatorsLeft;
            } else if (prevV || (addH && !prevH)) {
                output = output.add("H");
                prevH = true;
                prevV = false;
                --operatorsLeft;
            } else {
                output = output.add("V");
                prevH = false;
                prevV = true;
                --operatorsLeft;
            }
        }
        addH = r.nextBoolean();
        if (prevV || (addH && !prevH)) {
            output = output.add("H");
        } else {
            output = output.add("V");
        }
        return output;
    }

    private Room polExprToRoom(ImList<String> polExpr) {
        ImList<Room> newRooms = new ImList<Room>();
        for (int i = 0; i < this.areas.size(); ++i) {
            newRooms = newRooms.add(new Room(i + "", this.areas.get(i)));
        }
        ImList<String> newPolExpr = polExpr;
        while (newPolExpr.size() > 1) {
            int v = newPolExpr.indexOf("V");
            int h = newPolExpr.indexOf("H");
            int i = h < 0 ? v : h;
            if (v * h >= 0) {
                i = Math.min(v, h);
            }
            String operator = newPolExpr.get(i);
            int r1 = indexOf(newRooms, newPolExpr.get(i - 2));
            int r2 = indexOf(newRooms, newPolExpr.get(i - 1));
            Room newRoom = newRooms.get(r1).combine(
                    newRooms.get(r2), operator);
            newRooms = newRooms.set(r1, newRoom).remove(r2);
            newPolExpr = newPolExpr.set(i, newRoom.getId())
                .remove(i - 2).remove(i - 2);
        }
        return newRooms.get(0);
    }

    private int indexOf(ImList<Room> rooms, String id) {
        int i = 0;
        for (Room room : rooms) {
            if (id.equals(room.getId())) {
                return i;
            }
            ++i;
        }
        return -1;
    }
}
