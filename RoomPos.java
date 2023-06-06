class RoomPos {
    private static final double EPSILON = 1E-4;
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
        boolean horIntersect = ((bot1 - EPSILON < bot2) &&
                 (top1 + EPSILON > bot2)) ||
                ((bot1 - EPSILON < top2) &&
                 (top1 + EPSILON > top2)) ||
                ((bot2 - EPSILON < bot1) &&
                 (top2 + EPSILON > bot1)) ||
                ((bot2 - EPSILON < top1) &&
                 (top2 + EPSILON > top1));
        boolean verIntersect = ((left1 - EPSILON < left2) &&
                 (right1 + EPSILON > left2)) ||
                ((left1 - EPSILON < right2) &&
                 (right1 + EPSILON > right2)) ||
                ((left2 - EPSILON < left1) &&
                 (right2 + EPSILON > left1)) ||
                ((left2 - EPSILON < right1) &&
                 (right2 + EPSILON > right1));
        return horIntersect && verIntersect;
    }

    private boolean doubleEquals(double d1, double d2) {
        return Math.abs(d1 - d2) < EPSILON;
    }

    private boolean doubleLessThanEquals(double d1, double d2) {
        return d1 - d2 < EPSILON;
    }

    private boolean doubleMoreThan(double d1, double d2) {
        return d1 - d2 > EPSILON;
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
