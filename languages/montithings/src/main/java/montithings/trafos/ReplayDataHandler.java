package montithings.trafos;

import javax.json.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
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

    protected List<Long> getNetworkDelays(String qCompSourceName, String qInstanceSourcePortName, String qCompTargetName, String qInstanceTargetPortName) {
        List<Long> delays = new ArrayList<>();
        for (JsonObject recording : getRecordings(qCompSourceName, qInstanceSourcePortName)) {
            if (recording.containsKey("delay")) {
                JsonObject delay = (JsonObject) recording.get("delay");
                if (delay.containsKey(qCompTargetName)) {
                    delays.add(delay.getJsonNumber(qCompTargetName).longValue());
                } else {
                    delays.add(0L);
                }
            } else {
                delays.add(0L);
            }
        }

        return delays;
    }

    protected HashMap<Integer, Long> getComputationLatencies(String qCompName) {
        HashMap<Integer, Long> delays = new HashMap<Integer, Long>();

        if (!this.data.getJsonObject("computation_latency").containsKey(qCompName)) {
            return delays;
        }

        JsonObject computationLatencies = this.data.getJsonObject("computation_latency")
                .getJsonObject(qCompName);

        for (String key : computationLatencies.keySet()) {
            delays.put(Integer.parseInt(key),
                    computationLatencies.getJsonNumber(key).longValue());
        }

        return delays;
    }
}


