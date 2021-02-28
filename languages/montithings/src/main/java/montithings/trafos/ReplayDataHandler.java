package montithings.trafos;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

class ReplayDataHandler {
    private final JsonObject data;

    protected ReplayDataHandler(File replayDataFile) {
        // reads and parses recording data
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(replayDataFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonReader reader = Json.createReader(fileInputStream);
        this.data = reader.readObject();
    }
}
