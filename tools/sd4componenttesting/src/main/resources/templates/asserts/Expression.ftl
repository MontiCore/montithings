<#macro printExpression prettyPrinter cppPrettyPrinter sD4CElement>
  // test Expression
  LOG(INFO) << "check expression ${prettyPrinter.prettyprint(sD4CElement)?replace("\n", "")?replace("\r", "")}";
  ASSERT_TRUE(${cppPrettyPrinter.prettyprint(sD4CElement.getExpression())});

</#macro>
