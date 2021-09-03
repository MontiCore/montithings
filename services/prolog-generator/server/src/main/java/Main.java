import static spark.Spark.*;
import de.rwth.se.iotlab.facts.generator.FactsGenerator;
import de.rwth.se.iotlab.config.generator.QueryGenerator;

public class Main {
    public static void main(String[] args) {
        port(5004);
        post("/facts", ((request, response) -> {
            try {
                String json = request.body();
                if (json == null || json.equals("")) {
                    throw new Exception("Empty body");
                }
                return FactsGenerator.generateFacts(json);
            } catch (Exception e) {
                response.status(500);
                return e.getMessage();
            }
        }));

        post("/config", ((request, response) -> {
            try {
                String json = request.body();
                if (json == null || json.equals("")) {
                    throw new Exception("Empty body");
                }
                return QueryGenerator.generateQuery(json);
            } catch (Exception e) {
                response.status(500);
                return e.getMessage();
            }
        }));
    }
}
