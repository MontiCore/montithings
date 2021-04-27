// (c) https://github.com/MontiCore/monticore
package ocl;

component Source {

  port out int value;

  int lastValue = 0;

  pre let
          setVar = {x in {1:100} | y = {1:100}, x % 3 == 0};
          calculatedBalance = iterate { balance in {x in {1:100} | x % 3 == 0};
                                        double sum = 0.0 :
                                        sum = sum + balance }
        in
          84 == calculatedBalance;


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
