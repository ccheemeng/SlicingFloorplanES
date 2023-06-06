class Visualiser {
    Visualiser() {}

    String visualise(ImList<RoomPos> rooms) {
        String output = "";
        for (RoomPos room : rooms) {
            output += room.getId() + " " +
                room.getPos().first() + "," +
                room.getPos().second() + " " +
                room.getX() + " " + room.getY() + "\n";
        }
        return output;
    }
}
