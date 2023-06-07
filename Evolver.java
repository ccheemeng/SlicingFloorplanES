import java.util.function.Function;
import java.util.Random;

class Evolver<T> {
    private final long seed;
    private final int mu;
    private final int lambda;
    private final Function<ImList<T>, Pair<T>> selector;
    private final Function<T, T> mutator;
    private final Function<Pair<T>, T> crossover;
    private final Function<T, Double> evaluator;
    private static final double EPSILON = 1E-9;
    private static final double GEN_CAP = 10000;
    private static final int GENS_WITHOUT_IMPROVEMENT = 7500;
    private static final double MUTATION_PROB = 0.9;

    Evolver(long seed, int mu, int lambda,
            Function<ImList<T>, Pair<T>> selector, Function<T,T> mutator,
            Function<Pair<T>, T> crossover, Function<T, Double> evaluator) {
        this.seed = seed;
        this.mu = mu;
        this.lambda = lambda;
        this.selector = selector;
        this.mutator = mutator;
        this.crossover = crossover;
        this.evaluator = evaluator;
    }

    ImList<T> evolve(ImList<T> startPop) {
        int gen = 0;
        Random r = new Random(this.seed);
        ImList<T> pop = startPop;
        ImList<T> bestPop = pop;
        double fitness = fitness(pop);
        double newFitness = fitness;
        double bestFitness = fitness;
        int gensWithoutImprovement = 0;
        while (gensWithoutImprovement < GENS_WITHOUT_IMPROVEMENT) {
            ImList<T> children = new ImList<T>();
            ImList<T> selectedParents = pop;
            for (int i = 0; i < this.lambda; ++i) {
                Pair<T> parents = this.selector.apply(pop);
                if (r.nextDouble() - MUTATION_PROB < -EPSILON) {
                    parents = new Pair<T>(this.mutator.apply(parents.first()),
                            parents.second());
                }
                if (r.nextDouble() - MUTATION_PROB < -EPSILON) {
                    parents = new Pair<T>(parents.first(),
                            this.mutator.apply(parents.second()));
                }
                T child = this.crossover.apply(parents);
                children = children.add(child);
                selectedParents = selectedParents.remove(
                        r.nextInt(selectedParents.size()));
            }
            ImList<T> newPop = selectedParents;
            for (T child : children) {
                newPop = newPop.add(child);
            }
            newFitness = fitness(newPop);
            if (newFitness - fitness < -EPSILON) {
                gensWithoutImprovement = 0;
            } else {
                ++gensWithoutImprovement;
            }
            if (newFitness - bestFitness < -EPSILON) {
                bestPop = newPop;
            }
            pop = newPop;
            fitness = newFitness;
            ++gen;
            System.out.println("fitness:" + fitness + ", " +
                    "gen:" + gen + ", " +
                    "gen w/o improve:" + gensWithoutImprovement);
            if (fitness < EPSILON || gen >= GEN_CAP) {
                break;
            }
        }
        return bestPop;
    }

    private double fitness(ImList<T> population) {
        double fitness = Double.POSITIVE_INFINITY;
        T best = population.get(0);
        for (T t : population) {
            double newFitness = this.evaluator.apply(t);
            if (newFitness - fitness < -EPSILON) {
                fitness = newFitness;
                best = t;
            }
        }
        return fitness;
    }
}
