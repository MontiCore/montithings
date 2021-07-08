package examples.correct;

component Main {
  port in int value;

  port out int foo;
  port out int foo2;
  port out int foo3;

  Sum sumCom;
  Sum sumComp;
  Sum sumComFoo;

  value -> sumCom.first;
  value -> sumCom.second;
  sumCom.result -> foo;

  value -> sumComp.first;
  value -> sumComp.second;
  sumComp.result -> foo2;

  value -> sumComFoo.first;
  value -> sumComFoo.second;
  sumComFoo.result -> foo3;
}
