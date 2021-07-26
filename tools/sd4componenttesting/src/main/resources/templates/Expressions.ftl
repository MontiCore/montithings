<#macro print expression pp>
  <#assign type=expression.getClass().getName()>
  <#assign left = expression.getLeft()>
  <#assign op = expression.getOperator()>
  <#assign right = expression.getRight()>
  <#if type="de.monticore.expressions.assignmentsexpressions._ast.ASTAssignmentExpression">
    ${pp.prettyprint(expression)};
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression">
    EXPECT_EQ(${pp.prettyprint(left)}, ${pp.prettyprint(right)});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTLessEqualsExpression">
    EXPECT_LE(${pp.prettyprint(left)}, ${pp.prettyprint(right)});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualsExpression">
    EXPECT_GE(${pp.prettyprint(left)}, ${pp.prettyprint(right)});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression">
    EXPECT_LT(${pp.prettyprint(left)}, ${pp.prettyprint(right)});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression">
    EXPECT_GT(${pp.prettyprint(left)}, ${pp.prettyprint(right)});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression">
    EXPECT_NE(${pp.prettyprint(left)}, ${pp.prettyprint(right)});
  <#else>
    ${pp.prettyprint(expression)};
  </#if>
</#macro>
