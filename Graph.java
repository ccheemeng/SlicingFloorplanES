import java.util.Optional;

class Graph {
    final ImList<ImList<Integer>> graph;

    Graph(int i) {
        ImList<ImList<Integer>> newGraph = new ImList<ImList<Integer>>();
        for (int j = 0; j < i; ++j) {
            newGraph = newGraph.add(new ImList<Integer>());
        }
        this.graph = newGraph;
    }

    Graph(ImList<ImList<Integer>> graph) {
        this.graph = graph;
    }

    Graph add(ImList<Integer> adjacencies) {
        return new Graph(this.graph.add(adjacencies));
    }

    Graph remove(int i) {
        return new Graph(this.graph.remove(i));
    }

    Graph set(int i, ImList<Integer> adjacencies) {
        return new Graph(this.graph.set(i, adjacencies));
    }

    ImList<Integer> get(int i) {
        return this.graph.get(i);
    }

    Graph addAdjacency(int i, int adjacency) {
        return new Graph(this.graph.set(i,
                    this.graph.get(i).add(adjacency)));
    }

    Graph removeAdjacency(int i, int j) {
        return new Graph(this.graph.set(i,
                    this.graph.get(i).remove(j)));
    }

    Graph setAdjacency(int i, int j, int adjacency) {
        return new Graph(this.graph.set(i,
                    this.graph.get(i).set(j, adjacency)));
    }

    int getAdjacency(int i, int j) {
        return this.graph.get(i).get(j);
    }

    int distance(int start, int end) {
        int curr = start;
        int count = -1;
        ImMap<Integer, Integer> parentNodes = new ImMap<Integer, Integer>();
        ImList<Boolean> visited = new ImList<Boolean>();
        for (ImList<Integer> node : this.graph) {
            visited = visited.add(false);
        }
        ImList<Integer> queue = new ImList<Integer>();
        visited = visited.set(curr, true);
        queue = queue.add(curr);
        while (queue.size() > 0) {
            curr = queue.get(0);
            queue = queue.remove(0);
            if (curr == end) {
                Optional<Integer> target = Optional.<Integer>of(end);
                while (target.isPresent()) {
                    ++count;
                    target = parentNodes.get(target.get());
                }
                break;
            }
            for (int i = 0; i < this.graph.get(curr).size(); ++i) {
                int next = this.graph.get(curr).get(i);
                if (!visited.get(next)) {
                    parentNodes = parentNodes.put(next, curr);
                    visited = visited.set(next, true);
                    queue = queue.add(next);
                }
            }
        }
        return count;
    }

    @Override
    public String toString() {
        String output = "";
        for (ImList<Integer> adjacency : this.graph) {
            output += adjacency.toString() + "\n";
        }
        return output;
    }
}
