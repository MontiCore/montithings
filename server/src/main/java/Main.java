import static spark.Spark.*;
import de.rwth.se.iotlab.facts.generator.FactsGenerator;
import de.rwth.se.iotlab.config.generator.QueryGenerator;

public class Main {
    public static void main(String[] args) {
        post("/facts", ((request, response) -> {
            String json = request.body();

            return FactsGenerator.generateFacts(json);
        }));

        post("/config", ((request, response) -> {
            String json = request.body();

            return QueryGenerator.generateQuery(json);
        }));
    }
}
