import java.util.Random;
import java.util.Comparator;
import java.util.function.Function;

class Simulator {
    private final Random r;
    private final int mu;
    private final int lambda;
    private final int tournamentSize;
    private final double x;
    private final double y;
    private final ImList<String> names;
    private final ImList<Double> areas;
    private final ImList<Twin<String>> adjacencies;
    private final ImList<Twin<Double>> proportions;
    private final ImList<Boolean> careAboutPos;
    private final ImList<Twin<Double>> pos;
    private final int distanceScale;
    private final int proportionScale;
    private final int posScale;
    private static final double EPSILON = 1E-15;

    Simulator(long seed, int mu, int lambda, int tournamentSize,
            double x, double y, ImList<String> names, ImList<Double> areas,
            ImList<Twin<String>> adjacencies, ImList<Twin<Double>> proportions,
            ImList<Boolean> careAboutPos, ImList<Twin<Double>> pos,
            int distanceScale, int proportionScale, int posScale) {
        this.r = new Random(seed);
        this.mu = mu;
        this.lambda = lambda;
        this.tournamentSize = tournamentSize;
        this.x = x;
        this.y = y;
        this.names = names;
        this.areas = areas;
        this.adjacencies = adjacencies;
        this.proportions = proportions;
        this.careAboutPos = careAboutPos;
        this.pos = pos;
        this.distanceScale = distanceScale;
        this.proportionScale = proportionScale;
        this.posScale = posScale;
    }

    ImList<RoomPos> evolve() {
        ImList<ImList<String>> population = new ImList<ImList<String>>();
        for (int i = 0; i < this.mu; ++i) {
            population = population.add(startPolExpr());
        }
        Evolver<ImList<String>> evolver = new Evolver<ImList<String>>(
                r.nextLong(), this.lambda,
                selector(), mutator(), crossover(), evaluator());
        Pair<ImList<String>, Double> output = evolver.evolve(population);
        ImList<String> individual = output.first();
        return polExprToRoom(individual).construct(this.x, this.y, this.names);
    }

    private Function<ImList<ImList<String>>, Twin<ImList<String>>> selector() {
        return pop -> {
            ImList<ImList<String>> parents = new ImList<ImList<String>>();
            for (int i = 0; i < 2; ++i) {
                ImList<ImList<String>> newPop = pop;
                ImList<ImList<String>> sample = new ImList<ImList<String>>();
                for (int j = 0; j < this.tournamentSize; ++j) {
                    int index = this.r.nextInt(newPop.size());
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
            return new Twin<ImList<String>>(parents.get(0), parents.get(1));
        };
    }

    private Function<ImList<String>, ImList<String>> mutator() {
        return individual -> PolExpr.randomMove(individual, r.nextLong());
    }

    private Function<Twin<ImList<String>>, ImList<String>> crossover() {
        return parents -> PolExpr.ccx(parents.first(), parents.second());
    }

    private Function<ImList<String>, Double> evaluator() {
        return individual -> cost(polExprToRoom(individual).construct(
                    this.x, this.y, this.names));
    }

    private double cost(ImList<RoomPos> rooms) {
        double total = this.distanceScale + this.proportionScale + this.posScale;
        if (total == 0) {
            return 0.0;
        }
        double output = (this.distanceScale / total) * distanceCost(rooms) +
            (this.proportionScale / total) * proportionCost(rooms) +
            (this.posScale / total) * posCost(rooms);
        return output;
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
        ImList<Twin<Integer>> indexedAdjacencies = new ImList<Twin<Integer>>();
        for (Twin<String> adjacency : this.adjacencies) {
            indexedAdjacencies = indexedAdjacencies.add(
                    new Twin<Integer>(ids.indexOf(adjacency.first()),
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
        for (Twin<Integer> adjacency : indexedAdjacencies) {
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
            Twin<Double> proportion = this.proportions.get(i);
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

    private double posCost(ImList<RoomPos> rooms) {
        double costs = 0.0;
        int total = 0;
        ImList<Integer> ids = new ImList<Integer>();
        for (RoomPos room : rooms) {
            ids = ids.add(this.names.indexOf(room.getId()));
        }
        double max = Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
        for (int i = 0; i < this.pos.size(); ++i) {
            boolean care = this.careAboutPos.get(i);
            if (!care) {
                continue;
            }
            Twin<Double> pos = this.pos.get(i);
            RoomPos room = rooms.get(ids.indexOf(i));
            double cost = room.distanceOf(pos) / max;
            costs += cost;
            ++total;
        }
        if (total == 0) {
            return 0.0;
        }
        return costs / total;
    }

    private ImList<String> startPolExpr() {
        int numOfOperands = this.names.size();
        ImList<Integer> ids = new ImList<Integer>();
        for (int i = 0; i < numOfOperands; ++i) {
            ids = ids.add(i);
        }
        ImList<String> output = new ImList<String>();
        boolean addOperand = false;
        boolean addH = false;
        boolean prevH = false;
        boolean prevV = false;
        int index = -1;
        for (int i = 0; i < 2; ++i) {
            index = this.r.nextInt(ids.size());
            output = output.add(ids.get(index) + "");
            ids = ids.remove(index);
        }
        int operatorsLeft = 1;
        for (int i = 0; i < 2 * numOfOperands - 4; ++i) {
            addOperand = this.r.nextBoolean();
            addH = this.r.nextBoolean();
            if (operatorsLeft <= 0 ||
                    (addOperand && ids.size() > 0)) {
                index = this.r.nextInt(ids.size());
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
        addH = this.r.nextBoolean();
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
