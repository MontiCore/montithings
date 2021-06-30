package examples.correct;

component Main {
  port in int value;

  port out int result;
  port out int result2;
  port out int result3;

  Sum sumCom;
  Sum sumComp;
  Sum sumComFoo;

  value -> sumCom.first;
  value -> sumCom.second;
  sumCom.result -> result;

  value -> sumComp.first;
  value -> sumComp.second;
  sumComp.result -> result2;

  value -> sumComFoo.first;
  value -> sumComFoo.second;
  sumComFoo.result -> result3;
}
