package montithings.trafos;

import javax.json.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

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

    protected List<JsonObject> getRecordings(String qNameComp, String portName) {
        return this.data.getJsonObject("recordings")
                .getJsonArray(qNameComp)
                .stream()
                .filter(record -> record.getValueType() == JsonValue.ValueType.OBJECT)
                .map(record -> (JsonObject) record)
                .filter(record -> record.getString("topic").equals(qNameComp + "." + portName + "/out"))
                .collect(Collectors.toList());
    }
}


