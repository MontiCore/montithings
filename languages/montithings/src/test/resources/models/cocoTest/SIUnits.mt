// (c) https://github.com/MontiCore/monticore
package cocoTest;

component SIUnits (km<float> meter, mm^2/kVA^2h test = 20 m^2/mVA^2h){
  port out m<double> testOutPort;

  mm^2/kVA^2h lastValue = 20 mm^2/VA^2h;

  behavior {
    mm^2/kVA^2h testVariable = 23 m^2/mVA^2h;
    if(lastValue == 20m^2/VA^2h) testOutPort = 65m;
    else testOutPort = 12m;
  }
}
