import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

class Visualiser {
    static void visualise(ImList<RoomPos> rooms) {
        String output = "";
        for (RoomPos room : rooms) {
            output += room.getId() + " " +
                room.getPos().first() + "," +
                room.getPos().second() + " " +
                room.getX() + " " + room.getY() + "\n";
        }
        try {
            File file = new File("output.out");
            FileWriter fileWriter = new FileWriter(file);
            file.createNewFile();
            fileWriter.write(output);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("could not write output");
        }
    }
}
