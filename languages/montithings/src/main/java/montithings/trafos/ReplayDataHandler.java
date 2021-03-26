package montithings.trafos;

import javax.json.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

    protected List<JsonObject> getRecordings(String qCompName, String qInstancePortName) {
        if (!this.data.getJsonObject("recordings").containsKey(qCompName)) {
            return new ArrayList<>();
        }

        return this.data.getJsonObject("recordings")
                .getJsonArray(qCompName)
                .stream()
                .filter(record -> record.getValueType() == JsonValue.ValueType.OBJECT)
                .map(record -> (JsonObject) record)
                .filter(record -> record.getString("topic").equals(qCompName + "." + qInstancePortName + "/out"))
                .collect(Collectors.toList());
    }

    protected List<Long> getDelays(String qCompSourceName, String qInstanceSourcePortName, String qCompTargetName, String qInstanceTargetPortName) {
        List<Long> delays = new ArrayList<>();
        for (JsonObject recording : getRecordings(qCompSourceName, qInstanceSourcePortName)) {
            if (recording.containsKey("delay")) {
                JsonObject delay = (JsonObject) recording.get("delay");
                if (delay.containsKey(qCompTargetName + qInstanceTargetPortName)) {
                    delays.add(delay.getJsonNumber(qCompTargetName + qInstanceTargetPortName).longValue());
                } else {
                    delays.add(0L);
                }
            } else {
                delays.add(0L);
            }
        }

        return delays;
    }
}


