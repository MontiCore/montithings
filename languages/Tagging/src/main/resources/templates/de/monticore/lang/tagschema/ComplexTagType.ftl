${tc.signature("packageName", "schemaName", "tagTypeName", "complexVars", "isUnit")}

package ${packageName}.${schemaName};

import de.monticore.lang.tagging._symboltable.TagKind;
import de.monticore.lang.tagging._symboltable.TagSymbol;

import org.jscience.physics.amount.Amount;
import javax.measure.quantity.*;

/**
 * Created by ValuedTagType.ftl
 */
public class ${tagTypeName}Symbol extends TagSymbol {
  public static final ${tagTypeName}Kind KIND = ${tagTypeName}Kind.INSTANCE;

  public ${tagTypeName}Symbol(
  <#list complexVars?keys as name>
    Amount<?> ${name}<#if (name_has_next)>,</#if>
  </#list>
  ) {
    super(KIND,
    <#list complexVars?keys as name>
    (Amount<${complexVars[name]}>)${name}<#if (name_has_next)>,</#if>
    </#list>
    );
  }

  <#list complexVars?keys as name>
  <#if complexVars[name] == 'Number' || complexVars[name] == 'String' || complexVars[name] == 'Boolean'>
  public ${complexVars[name]} get${name?cap_first}() {
    return getValue(${name?index});
  }
  <#else>
  public Amount<${complexVars[name]}> get${name?cap_first}() {
    return getValue(${name?index});
  }
  </#if>

  </#list>

  @Override
  public String toString() {
    return String.format("${tagTypeName} = " +
    <#list complexVars?keys as name>
    "%s"<#if (name_has_next)>+</#if>
    </#list>
    ,
    <#list complexVars?keys as name>
    get${name?cap_first}()<#if (name_has_next)>,</#if>
    </#list>
     );
  }

  public static class ${tagTypeName}Kind extends TagKind {
    public static final ${tagTypeName}Kind INSTANCE = new ${tagTypeName}Kind();

    protected ${tagTypeName}Kind() {
    }
  }
}