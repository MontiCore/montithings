package hierarchy;

import hierarchy.MathExpression;

component GenericBinding<T extends MathExpression> {

  T mathExpression;
  Source source;
  Sink sink;

  source.value -> mathExpression.x;
    mathExpression.y -> sink.value;

}
