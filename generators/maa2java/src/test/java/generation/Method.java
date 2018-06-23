/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package generation;

import de.monticore.java.javadsl._ast.ASTDeclaratorId;
import de.monticore.java.javadsl._ast.ASTFormalParameter;
import de.monticore.java.javadsl._ast.ASTFormalParameterListing;
import de.monticore.java.javadsl._ast.ASTFormalParameters;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTVoidType;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author (last commit) Michael Mutert
 * @version , 2018-06-22
 * @since TODO
 */
public class Method{
  private final ASTReturnType returnType;
  private final String name;
  private final ASTFormalParameters params;
  private List<String> bodyElements;

  public Method(ASTReturnType returnType, String name,
                ASTFormalParameters params, List<String> bodyElements) {
    this.returnType = returnType;
    this.name = name;
    this.params = params;
    this.bodyElements = bodyElements;
  }

  static Builder getBuilder(){
    return new Builder();
  }

  public ASTReturnType getReturnType() {
    return returnType;
  }

  public String getName() {
    return name;
  }

  public List<String> getBodyElements() {
    return bodyElements;
  }

  public ASTFormalParameters getParams() {
    return params;
  }

  static class Builder{

    private ASTReturnType returnType;
    private String name;
    private List<ASTFormalParameter> parameters;
    private List<String> bodyElements;

    public Builder() {
      this.bodyElements = new ArrayList<>();
      this.parameters = new ArrayList<>();
    }

    public Builder setReturnType(ASTReturnType returnType) {
      this.returnType = returnType;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder addParameter(String identifier, ASTType type){
      final ASTDeclaratorId id = ASTDeclaratorId.getBuilder()
                                     .name(identifier).build();
      final ASTFormalParameter param = ASTFormalParameter
                                           .getBuilder()
                                           .type(type)
                                           .declaratorId(id)
                                           .build();
      this.parameters.add(param);
      return this;
    }

    public Builder addParameters(List<ASTFormalParameter> parameters){
      this.parameters.addAll(parameters);
      return this;
    }

    public Builder addBodyElements(List<String> bodyElements) {
      this.bodyElements.addAll(bodyElements);
      return this;
    }

    public Builder addBodyElement(String element){
      this.bodyElements.add(element);
      return this;
    }

    public Method build() {
      ASTFormalParameters formalParameters;
      if(this.parameters.isEmpty()){
        formalParameters = ASTFormalParameters.getBuilder().build();
      } else{
        ASTFormalParameterListing listing =
            ASTFormalParameterListing
                .getBuilder()
                .formalParameters(this.parameters)
                .build();
        formalParameters = ASTFormalParameters
                               .getBuilder()
                               .formalParameterListing(listing)
                               .build();
      }
      if(returnType == null){
        returnType = ASTVoidType.getBuilder().build();
      }
      return new Method(returnType, name, formalParameters, bodyElements);
    }
  }
}
