import java.util.Optional;

class Room {
    private final String id;
    private final double area;
    private final Optional<Twin<Room>> children;
    private final String operator;

    Room(String id, double area) {
        this.id = id;
        this.area = area;
        this.children = Optional.<Twin<Room>>empty();
        this.operator = "";
    }

    private Room(String id, double area,
            Optional<Twin<Room>> children, String operator) {
        this.id = id;
        this.area = area;
        this.children = children;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return this.id;
    }

    Room combine(Room other, String operator) {
        return new Room(this.id + other.id + operator,
                this.area + other.area, Optional.<Twin<Room>>of(
                    new Twin<Room>(this, other)), operator);
    }

    ImList<RoomPos> construct(double x, double y, ImList<String> names) {
        return construct(x, y, names,
                new Twin<Double>(0.0, 0.0), new ImList<RoomPos>());
    }

    ImList<RoomPos> construct(double x, double y, ImList<String> names,
            Twin<Double> pos, ImList<RoomPos> oldRooms) {
        if (this.children.isEmpty()) {
            return oldRooms.add(new RoomPos(
                        names.get(Integer.parseInt(this.id)), x, y, pos));
        } else {
            double x1 = x;
            double x2 = x;
            double y1 = y;
            double y2 = y;
            Twin<Double> pos2 = pos;
            Room r1 = this.children.get().first();
            Room r2 = this.children.get().second();
            if (operator == "V") {
                x2 = (r2.area / (r1.area + r2.area)) * x;
                x1 = x - x2;
                pos2 = new Twin<Double>(
                        (r1.area / (r1.area + r2.area)) * x + pos.first(),
                       pos.second());
            } else {
                y2 = (r2.area / (r1.area + r2.area)) * y;
                y1 = y - y2;
                pos2 = new Twin<Double>(pos.first(),
                        (r1.area / (r1.area + r2.area)) * y + pos.second());
            }
            ImList<RoomPos> newRooms = oldRooms;
            ImList<RoomPos> newRooms1 = r2.construct(x2, y2, names,
                    pos2, new ImList<RoomPos>());
            ImList<RoomPos> newRooms2 = r1.construct(x1, y1, names,
                    pos, new ImList<RoomPos>());
            for (RoomPos room : newRooms1) {
                newRooms = newRooms.add(room);
            }
            for (RoomPos room : newRooms2) {
                newRooms = newRooms.add(room);
            }
            return newRooms;
        }
    }

    String getId() {
        return this.id;
    }
}
