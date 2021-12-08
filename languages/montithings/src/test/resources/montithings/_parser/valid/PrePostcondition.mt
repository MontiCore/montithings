// (c) https://github.com/MontiCore/monticore
package valid;

component PrePostcondition {
  port in int input;
  port out int result;

  pre input > 0;

  pre input <= 300;
  catch { input = 300; }

  post result = input + 50;
}
