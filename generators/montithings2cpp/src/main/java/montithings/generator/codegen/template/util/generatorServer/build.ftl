<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("languagePaths","existsHWC")}
<#include "/template/Preamble.ftl">
/* (c) https://github.com/MontiCore/monticore */
/*
 GENERATED FILE. DO NOT EDIT. CHANGES WILL BE OVERWRITTEN!
*/

plugins {
    id 'java'
    id 'com.github.johnrengelman.shadow' version '4.0.4'
}

group 'dsl.generator-server'

repositories {
    mavenCentral()
    maven {
        url = "https://nexus.se.rwth-aachen.de/content/groups/public"
    }
    
}


dependencies {
    implementation group: 'org.slf4j', name: 'slf4j-api', version: '1.7.25'
    implementation group: 'org.slf4j', name: 'slf4j-simple', version: '1.6.1'
    implementation "de.monticore:monticore-runtime:7.0.0"
    implementation "de.monticore:monticore-grammar:7.0.0"
    implementation "com.sparkjava:spark-core:2.8.0"
    
    <#list languagePaths as path>
    implementation files('languages${path}/target/classes/java/main')
    implementation files('languages${path}/target/classes')
    </#list>
    implementation group: 'org.apache.commons', name: 'commons-lang3', version: '3.0'
    implementation group: 'org.eclipse.paho', name: 'org.eclipse.paho.client.mqttv3', version: '1.0.2'


    compileOnly "de.monticore:monticore-grammar:7.0.0"

}

shadowJar {
    manifest {
        attributes "Main-Class": "Main"
    }
    archiveFileName = "GeneratorServer.${r"${archiveExtension.get()}"}" // alternative: "server-${r"${project.version}.${archiveExtension.get()}"}"
    archiveClassifier = "server"
}

jar.dependsOn shadowJar


