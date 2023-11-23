<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config", "state" "existsHWC")}
<#include "/template/Preamble.ftl">
// (c) https://github.com/MontiCore/monticore
//
// GENERATED FILE. DO NOT EDIT. CHANGES WILL BE OVERWRITTEN!
//
import java.lang.Exception;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

import java.io.File;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.lang.SecurityManager;
import java.lang.SecurityException;
import java.security.Permission;

import javax.servlet.MultipartConfigElement;
import org.apache.commons.io.FileUtils;

import org.eclipse.paho.client.mqttv3.*;


public class Main {
    public static void main(String[] args) {
        MySecurityManager secManager = new MySecurityManager();
        System.setSecurityManager(secManager);
        MqttClient mqttClient;
        try{
            String brokerURI;
            if(args.length >= 2){
                brokerURI = "tcp://" + args[0] + ":" + args[1];
            }else{
                brokerURI = "tcp://127.0.0.1:1883";
            }
            mqttClient = new MqttClient(brokerURI, "calculationMachine.generatorServer");
            mqttClient.connect();


            port(8080);
            <#list state.getInstances() as pair>
            <#assign fullName = pair.getKey().getFullName()>
            <#assign instanceName = pair.getValue()>
            <#if ComponentHelper.isDSLComponent(pair.getKey(),config)>
            
            post("/${GeneratorHelper.replaceDotsBySlashes(instanceName)}", ((request, response) -> {
                
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(os);
                PrintStream old = System.err;
                System.setErr(ps);
                
                try {
                    MultipartConfigElement multipartConfigElement = new MultipartConfigElement("/tmp");
                    request.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);
                    String model = new String(request.raw().getPart("fileUpload").getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("Input (/${GeneratorHelper.replaceDotsBySlashes(fullName)}): " + model);

                    ${GeneratorHelper.getLanguageNameFromLanguagePath("/" + GeneratorHelper.replaceDotsBySlashes(fullName),config)}.generator.Generator sg = new ${GeneratorHelper.getLanguageNameFromLanguagePath("/" + GeneratorHelper.replaceDotsBySlashes(fullName),config)}.generator.Generator();
                    String generatedPy = sg.generate(model);
                    
                    System.err.flush();
                    System.setErr(old);
                    String prints = os.toString("UTF-8");
                    System.out.println(prints);

                    //Send Py to DSL component
                    MqttMessage mqttPy = new MqttMessage(generatedPy.getBytes());
                    mqttClient.publish("/hwc/${GeneratorHelper.replaceDotsBySlashes(instanceName)}",mqttPy);
                    
                    
                    return "<div style=\"white-space: pre-wrap; color: whitesmoke;\">Task successful! Component should be updated.</div>";
                } catch (SecurityException e) {
                    System.err.flush();
                    System.setErr(old);
                    String errorMessage = "<div style=\"white-space: pre-wrap; color: whitesmoke;\">Task failed and the component has not been updated.\n Reason for failed task:\n " + os.toString("UTF-8") + "</div>";
                    System.out.println(errorMessage);
                    response.status(500);
                    return errorMessage;
                }catch (Exception e){
                    System.err.flush();
                    System.setErr(old);
                    String errorMessage = "<div style=\"white-space: pre-wrap; color: whitesmoke;\">Generator crashed. This is an issue, that the software developer has to solve.\n Reason for crash:\n " + os.toString("UTF-8") + "</div>";
                    System.out.println(errorMessage);
                    response.status(500);
                    return errorMessage;
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

class MySecurityManager extends SecurityManager {
  @Override public void checkExit(int status) {
    throw new SecurityException();
  }

  @Override public void checkPermission(Permission perm) {
      // Allow other activities by default
  }
}