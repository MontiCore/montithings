// (c) https://github.com/MontiCore/monticore
package cocoTest.genericBindingTest.interfaceImplementsInterface.sensors;

interface component SmokeSensor<T> {

  port
    out T value;
}
