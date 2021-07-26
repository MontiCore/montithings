<#macro print expression pp>
  <#assign type=expression.getClass().getName()>
  <#if type="de.monticore.expressions.assignmentsexpressions._ast.ASTAssignmentExpression">
    ${pp.prettyprint(expression)};
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTEqualsExpression">
    EXPECT_EQ(${pp.prettyprint(expression.getLeft())}, ${pp.prettyprint(expression.getRight())});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTLessEqualsExpression">
    EXPECT_LE(${pp.prettyprint(expression.getLeft())}, ${pp.prettyprint(expression.getRight())});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTGreaterEqualsExpression">
    EXPECT_GE(${pp.prettyprint(expression.getLeft())}, ${pp.prettyprint(expression.getRight())});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTLessThanExpression">
    EXPECT_LT(${pp.prettyprint(expression.getLeft())}, ${pp.prettyprint(expression.getRight())});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTGreaterThanExpression">
    EXPECT_GT(${pp.prettyprint(expression.getLeft())}, ${pp.prettyprint(expression.getRight())});
  <#elseif type="de.monticore.expressions.commonexpressions._ast.ASTNotEqualsExpression">
    EXPECT_NE(${pp.prettyprint(expression.getLeft())}, ${pp.prettyprint(expression.getRight())});
  <#else>
    ${pp.prettyprint(expression)};
  </#if>

</#macro>
