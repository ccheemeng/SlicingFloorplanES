class RoomPos {
    private static final double THRESHOLD = 1E-4;
    private static final double EPSILON = 1E-15;
    private final String id;
    private final double x;
    private final double y;
    private final Pair<Double> pos;

    RoomPos(String id, double x, double y, Pair<Double> pos) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return String.format("%s at %s (%fx%f)",
                this.id, this.pos.toString(), this.x, this.y);
    }

    boolean adjacent(RoomPos other) {
        double left1 = this.pos.first();
        double right1 = left1 + this.x;
        double bot1 = this.pos.second();
        double top1 = bot1 + this.y;
        double left2 = other.pos.first();
        double right2 = left2 + other.x;
        double bot2 = other.pos.second();
        double top2 = bot2 + other.y;
        boolean horIntersect = ((bot1 - THRESHOLD < bot2) &&
                 (top1 + THRESHOLD > bot2)) ||
                ((bot1 - THRESHOLD < top2) &&
                 (top1 + THRESHOLD > top2)) ||
                ((bot2 - THRESHOLD < bot1) &&
                 (top2 + THRESHOLD > bot1)) ||
                ((bot2 - THRESHOLD < top1) &&
                 (top2 + THRESHOLD > top1));
        boolean verIntersect = ((left1 - THRESHOLD < left2) &&
                 (right1 + THRESHOLD > left2)) ||
                ((left1 - THRESHOLD < right2) &&
                 (right1 + THRESHOLD > right2)) ||
                ((left2 - THRESHOLD < left1) &&
                 (right2 + THRESHOLD > left1)) ||
                ((left2 - THRESHOLD < right1) &&
                 (right2 + THRESHOLD > right1));
        return horIntersect && verIntersect;
    }

    double distanceOf(Pair<Double> pt) {
        double left = this.pos.first();
        double right = left + this.x;
        double bot = this.pos.second();
        double top = bot + this.y;
        double x = pt.first();
        double y = pt.second();
        double horDiff = 0.0;
        double verDiff = 0.0;
        if (x - left < -EPSILON) {
            horDiff = x - left;
        } else if (x - right > EPSILON) {
            horDiff = x - right;
        }
        if (y - bot < -EPSILON) {
            verDiff = y - bot;
        } else if (y - top > EPSILON) {
            verDiff = y - top;
        }
        return Math.sqrt(Math.pow(horDiff, 2) + Math.pow(verDiff, 2));
    }

    String getId() {
        return this.id;
    }

    double getX() {
        return this.x;
    }

    double getY() {
        return this.y;
    }

    Pair<Double> getPos() {
        return this.pos;
    }
}
