import java.util.function.Function;
import java.util.Random;

class Evolver<T> {
    private final Random r;
    private final int lambda;
    private final Function<ImList<T>, Twin<T>> selector;
    private final Function<T, T> mutator;
    private final Function<Twin<T>, T> crossover;
    private final Function<T, Double> evaluator;
    private static final double EPSILON = 1E-9;
    private static final double GEN_CAP = 50000;
    private static final int GENS_WITHOUT_IMPROVEMENT = 7500;
    private static final double MUTATION_PROB = 0.9;

    Evolver(long seed, int lambda,
            Function<ImList<T>, Twin<T>> selector, Function<T,T> mutator,
            Function<Twin<T>, T> crossover, Function<T, Double> evaluator) {
        this.r = new Random(seed);
        this.lambda = lambda;
        this.selector = selector;
        this.mutator = mutator;
        this.crossover = crossover;
        this.evaluator = evaluator;
    }

    Pair<T, Double> evolve(ImList<T> startPop) {
        T best = startPop.get(0);
        int gen = 0;
        ImList<T> pop = startPop;
        ImList<T> bestPop = pop;
        double fitness = fitness(pop).second();
        double newFitness = fitness;
        double bestFitness = fitness;
        int gensWithoutImprovement = 0;
        while (gensWithoutImprovement < GENS_WITHOUT_IMPROVEMENT) {
            ImList<T> children = new ImList<T>();
            ImList<T> selectedParents = pop;
            for (int i = 0; i < this.lambda; ++i) {
                Twin<T> parents = this.selector.apply(pop);
                if (this.r.nextDouble() - MUTATION_PROB < -EPSILON) {
                    parents = new Twin<T>(this.mutator.apply(parents.first()),
                            parents.second());
                }
                if (this.r.nextDouble() - MUTATION_PROB < -EPSILON) {
                    parents = new Twin<T>(parents.first(),
                            this.mutator.apply(parents.second()));
                }
                T child = this.crossover.apply(parents);
                children = children.add(child);
                selectedParents = selectedParents.remove(
                        this.r.nextInt(selectedParents.size()));
            }
            ImList<T> newPop = selectedParents;
            for (T child : children) {
                newPop = newPop.add(child);
            }
            Pair<T, Double> output = fitness(newPop);
            T newBest = output.first();
            newFitness = output.second();
            if (newFitness - fitness < -EPSILON) {
                gensWithoutImprovement = 0;
            } else {
                ++gensWithoutImprovement;
            }
            if (newFitness - bestFitness < -EPSILON) {
                bestPop = newPop;
                best = newBest;
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
        return new Pair<T, Double>(best, fitness);
    }

    private Pair<T, Double> fitness(ImList<T> population) {
        double fitness = Double.POSITIVE_INFINITY;
        T best = population.get(0);
        for (T t : population) {
            double newFitness = this.evaluator.apply(t);
            if (newFitness - fitness < -EPSILON) {
                fitness = newFitness;
                best = t;
            }
        }
        return new Pair<T, Double>(best, fitness);
    }
}
