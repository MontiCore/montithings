package examples.correct;

component Main {
  port in int value;

  port out int result;

  Sum sum;

  value -> sum.first;
  value -> sum.second;
  sum.result -> result;
}
