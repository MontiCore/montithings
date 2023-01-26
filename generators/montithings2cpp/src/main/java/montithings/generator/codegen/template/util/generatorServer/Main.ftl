${tc.signature("languagePaths", "port", "config", "state" "what")}
<#include "/template/Preamble.ftl">
// (c) https://github.com/MontiCore/monticore

import java.lang.Exception;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

import javax.servlet.MultipartConfigElement;
import org.apache.commons.io.FileUtils;

import org.eclipse.paho.client.mqttv3.*;


public class Main {
    public static void main(String[] args) {
        MqttClient mqttClient;
        try{
            String brokerURI = "tcp://127.0.0.1:1883";
            mqttClient = new MqttClient(brokerURI, "calculationMachine.generatorServer");
            mqttClient.connect();


            port(${port});
            <#list state.getInstances() as pair>
            <#assign fullName = pair.getKey().getFullName()>
            <#assign instanceName = pair.getValue()>
            <#if ComponentHelper.isDSLComponent(pair.getKey(),config)>
            
            post("/${GeneratorHelper.replaceDotsBySlashes(instanceName)}", ((request, response) -> {
                try {
                    MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
                    request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
                    String model = new String(request.raw().getPart("fileUpload").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Input (/${GeneratorHelper.replaceDotsBySlashes(fullName)}): " + model);
                    if (model == null || model.equals("")) {
                        throw new Exception("Empty body");
                    }
                    ${GeneratorHelper.getLanguageNameFromLanguagePath("/" + GeneratorHelper.replaceDotsBySlashes(fullName),config)}.generator.Generator sg = new ${GeneratorHelper.getLanguageNameFromLanguagePath("/" + GeneratorHelper.replaceDotsBySlashes(fullName),config)}.generator.Generator();
                    String generatedPy = sg.generate(model);


                    //Send Py to DSL component
                    MqttMessage mqttPy = new MqttMessage(generatedPy.getBytes());
                    mqttClient.publish("/hwc/${GeneratorHelper.replaceDotsBySlashes(instanceName)}",mqttPy);
                    
                    
                    return "";
                } catch (Exception e) {
                    System.out.println(e);
                    response.status(500);
                    return e.getMessage();
                }
            }));

            get("/${GeneratorHelper.replaceDotsBySlashes(instanceName)}", ((request, response) -> {
                try {
                    File html = new File("html/${GeneratorHelper.replaceDotsBySlashes(instanceName)}.html");
                    String cont = FileUtils.readFileToString(html,"UTF-8");
                    return cont;
                } catch (Exception e) {
                    System.out.println(e);
                    response.status(500);
                    return "ERROR: " + e.getMessage();
                }
            }));

            </#if>
            </#list>

 
            


            get("/", ((request, response) -> {
                try {
                    File html = new File("html/Index.html");
                    String cont = FileUtils.readFileToString(html,"UTF-8");
                    return cont;
                } catch (Exception e) {
                    System.out.println(e);
                    response.status(500);
                    return "ERROR: " + e.getMessage();
                }
            }));
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }
}