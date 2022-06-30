// (c) https://github.com/MontiCore/monticore

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.odbasis._ast.ASTObjectDiagram;
import montithings._parser.MontiThingsParser;
import montithings.services.prolog_generator.config.generator.QueryGenerator;
import montithings.services.prolog_generator.devicedescription._parser.DeviceDescriptionParser;
import montithings.services.prolog_generator.devicedescription.generator.ObjectDiagramToPrologConverter;
import montithings.services.prolog_generator.facts.generator.FactsGenerator;
import montithings.services.prolog_generator.oclquery.generator.OCLToPrologConverter;

import java.util.Optional;

import static spark.Spark.port;
import static spark.Spark.post;

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

        post("/device-description", ((request, response) -> {
            try {
                String od = request.body();
                if (od == null || od.equals("")) {
                    throw new Exception("Empty body");
                }
                DeviceDescriptionParser parser = new DeviceDescriptionParser();
                Optional<ASTObjectDiagram> e = parser.parse_StringObjectDiagram(od);
                if (!e.isPresent()) {
                    throw new Exception("Non-parsable Object Diagram.");
                }
                return ObjectDiagramToPrologConverter.generateFacts(e.get());
            } catch (Exception e) {
                response.status(500);
                return e.getMessage();
            }
        }));

        post("/ocl-query", ((request, response) -> {
            try {
                String ocl = request.body();
                if (ocl == null || ocl.equals("")) {
                    throw new Exception("Empty body");
                }
                MontiThingsParser parser = new MontiThingsParser();
                Optional<ASTExpression> e = parser.parse_StringExpression(ocl);
                if (!e.isPresent()) {
                    throw new Exception("Non-parsable OCL expression.");
                }
                return OCLToPrologConverter.generateOCLQuery(e.get());
            } catch (Exception e) {
                response.status(500);
                return e.getMessage();
            }
        }));
    }
}
