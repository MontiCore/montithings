// (c) https://github.com/MontiCore/monticore
package backendController;

<<deploy>> component Controller {
  component Cellular cellular;
  component Speed speed;

  connect speed.value -> cellular.inSpeed;
}
