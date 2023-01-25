// (c) https://github.com/MontiCore/monticore


import java.util.Optional;
import java.lang.reflect.Method;
import java.lang.Class;
import java.net.URLClassLoader;
import java.net.URL;
import com.sun.net.httpserver.*;
import java.net.InetSocketAddress;
import java.lang.Exception;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.io.OutputStream;
import org.apache.commons.lang3.StringEscapeUtils;

import network.generator.Generator;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {
    public static void main(String[] args) {
        port(5004);
        post("/hierarchy/Sink", ((request, response) -> {
            try {
                System.out.println("Input: " + (String)request.queryParams("fileUpload"));
                String model = request.queryParams("fileUpload");
                if (model == null || model.equals("")) {
                    throw new Exception("Empty body");
                }
                
                Generator sg = new Generator();
                return sg.generate(model);
            } catch (Exception e) {
                System.out.println(e);
                response.status(500);
                return e.getMessage();
            }
        }));
    }
}
    