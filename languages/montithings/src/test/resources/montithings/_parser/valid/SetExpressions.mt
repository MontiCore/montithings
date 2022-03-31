// (c) https://github.com/MontiCore/monticore
package valid;

component SetExpressions {
  port in int a, b;
  port in int c;

  pre a isin {1 .. 5};
  pre b isin {1, 5, 7};
  pre b isin {1, 3..4..11, 15};
  pre c isin {format: "2[0-9]*"};
}
