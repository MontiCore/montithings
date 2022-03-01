/* (c) https://github.com/MontiCore/monticore */

/**
 * sends data on an internally managed mqtt topic.
 * make a dynamic listener subscribe to this topic by using announce()
 * then send data via sendData() --> The listener will receive the data
 * as demonstrated in applications/dynamics
 */

#ifndef mt_interface_h
#define mt_interface_h

#include <ArduinoMqttClient.h>

#if defined(ARDUINO_SAMD_MKRWIFI1010) || defined(ARDUINO_SAMD_NANO_33_IOT) || defined(ARDUINO_AVR_UNO_WIFI_REV2)
#include <WiFiNINA.h>
#elif defined(ARDUINO_SAMD_MKR1000)
#include <WiFi101.h>
#elif defined(ARDUINO_ESP8266_ESP12)
#include <ESP8266WiFi.h>
#else

#include <WiFi.h>

#endif

#include "arduino_secrets.h"

namespace montithings {

    // To connect with SSL/TLS:
    // 1) Change WiFiClient to WiFiSSLClient. (see above)
    // 2) Change port value from 1883 to 8883. (in tab/arduino_secrets.h)
    // 3) Change broker value to a server with a known SSL/TLS root certificate
    //    flashed in the WiFi module. (in tab/arduino_secrets.h)
    WiFiClient wifiClient;

    MqttClient mqttClient(wifiClient);
    char *deviceID;

    /**
     * Initializes Serial and waits for port to open
     */
    void initSerial(int baud) {
        Serial.begin(baud);
        while (!Serial) { ; // wait for serial port to connect. Needed for native USB port only
        }
    }


    /**
     * connects to a wifi network
     *
     * @param ssid an ssid to a wifi network
     * @param pass the passcode to the wifi network (use for WPA, or use as key for WEP)
     */
    void connectToWifi(char ssid[], char pass[]) {
        // attempt to connect to WiFi network:
        Serial.print("Attempting to connect to WPA SSID: ");
        Serial.println(ssid);
        while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
            // failed, retry
            Serial.print(".");
            delay(5000);
        }

        Serial.println("You're connected to the network");
        Serial.println();
    }


    /**
     * connects to a MQTT broker
     *
     * @param broker an URL to a MQTT broker
     * @param port the port of the broker
     */
    void connectToMqttBroker(const char broker[], int port) {
        Serial.print("Attempting to connect to the MQTT broker: ");
        Serial.println(broker);

        if (!mqttClient.connect(broker, port)) {
            Serial.print("MQTT connection failed! Error code = ");
            Serial.println(mqttClient.connectError());

            while (1);
        }

        Serial.println("You're connected to the MQTT broker!");
        Serial.println();
    }


    /**
     * generates a random UID
     * @return random UID
     */
    char *getUid() {
        randomSeed(analogRead(0));
        char *topic = (char *) malloc(37);

        for (int i = 0; i < 36; i++) {
            sprintf(topic + i, "%x", random(0, 0xf));
        }
        topic[9] = '-';
        topic[14] = '-';
        topic[19] = '-';
        topic[24] = '-';

        return topic;
    }


    /**
     * announces itself to the 'portsInject' topic of another device, causing that device
     * to subscribe to the given subPort of this device
     * @param receiverName the name of the receiving device as found in the mqtt topic /portsInject/receiverName/connect
     * @param subPort a subPort of this device
     */
    void announce(String receiverName, char subPort[]) {
        char *messageID = getUid();
        mqttClient.beginMessage("/portsInject/" + receiverName + "/connect");
        mqttClient.print("{\"value0\":{\"payload\":{\"data\":{\"value0\":{\"value0\":\"");
        mqttClient.print(deviceID);
        mqttClient.print("/");
        mqttClient.print(subPort);
        mqttClient.print("\"}},\"nullopt\":false},\"uuid\":\"");
        mqttClient.print(messageID);
        mqttClient.print("\"}}");
        mqttClient.endMessage();
        free(messageID);
    }

    /**
     * wraps data in a JSON format and sends it to the given subPort of this device
     * @param data the data to be send
     * @param subPort a subPort of this device
     */
    void sendJson(const char data[], char subPort[]) {
        Serial.print("Sending message: ");
        Serial.println(data);

        char *messageID = getUid();

        char buf[200];
        strcpy(buf, "/ports/");
        strcat(buf, deviceID);
        strcat(buf, "/");
        strcat(buf, subPort);

        mqttClient.beginMessage(buf);
        mqttClient.print("{\"value0\":{\"payload\":{\"data\":");
        mqttClient.print(data);
        mqttClient.print(",\"nullopt\":false},\"uuid\":\"");
        mqttClient.print(messageID);
        mqttClient.print("\"}}");
        mqttClient.endMessage();
        free(messageID);

        Serial.println();
    }

    /**
     * sends an int to the given subPort
     * @param data an int
     * @param a subPort of this device
     */
    void send(int data, char subPort[]){
      sendJson(String(data).c_str(), subPort);
    }

    /**
     * sends a float to the given subPort
     * @param data a float
     * @param a subPort of this device
     */
    void send(float data, char subPort[]){
        sendJson(String(data).c_str(), subPort);
    }

    /**
     * sends a char[] to the given subPort
     * @param data a char[] containing data
     * @param a subPort of this device
     */
    void send(char data[], char subPort[]){
        sendJson(data, subPort);
    }


    /**
     * sends a bool to the given subPort
     * @param data a bool
     * @param a subPort of this device
     */
    void send(bool data, char subPort[]){
        sendJson(data? "true":"false", subPort);
    }

    /**
     * sends double to the given subPort
     * @param data a double
     * @param a subPort of this device
     */
    void send(double data, char subPort[]) {
        sendJson(String(data).c_str(), subPort);
    }

    /**
     * initializes the device so it can send data
     */
    void initInterface() {

        //please enter your sensitive data in the Secret tab/arduino_secrets.h
        connectToWifi(SECRET_SSID, SECRET_PASS);

        //please enter broker hostname and port in arduino_secrets.h
        const char broker[] = BROKER_HOSTNAME;
        int port = BROKER_PORT;
        connectToMqttBroker(broker, port);

        deviceID = getUid();
    }
}
#endif
