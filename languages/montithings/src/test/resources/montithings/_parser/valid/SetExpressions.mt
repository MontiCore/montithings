// (c) https://github.com/MontiCore/monticore
package valid;

component SetExpressions {
  port in int a, b;
  port in String c;

  pre a in {1 : 5};
  pre b in {1, 5, 7};
  pre b in {1, 3:4:11, 15};
  pre c in {format: "2[0-9]*"};
}
