// (c) https://github.com/MontiCore/monticore
package hierarchy;

component Source {

  port out int value;

  int lastValue = 0;

  behavior {
    lastValue++;
    if (exists i in {x in {1:100} | x % 3 == 0}: i == lastValue) {
      value = lastValue;
    }
  }

  post value % 3 == 0;
  post lastValue == lastValue@pre + 1;

  post exists i in {x in {1:100} | x % 3 == 0}:
         i == value;
  catch { value = 0; }

  update interval 10ms;
}
