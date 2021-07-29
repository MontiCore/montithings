// (c) https://github.com/MontiCore/monticore
package cdlangextension._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.Optional;

/**
 * AST for names in different forms.
 * E.g. '<someName>' or someQualifiedName::simpleName.
 */
public  class ASTCDEImportName extends ASTCDEImportNameTOP {

  public ASTCDEImportName() {
  }

  public ASTCDEImportName(Optional<String> string, Optional<String> angledString,
    Optional<ASTMCQualifiedName> qualifiedName,
    Optional<ASTCDEQualifiedColonName> cDEQualifiedColonName,
    Optional<ASTCDEQualifiedDoubleColonName> cDEQualifiedDoubleColonName) {
    this.string = string;
    this.angledString = angledString;
    this.mCQualifiedName = qualifiedName;
    this.cDEQualifiedColonName = cDEQualifiedColonName;
    this.cDEQualifiedDoubleColonName = cDEQualifiedDoubleColonName;
  }

  @Override
  public String toString() {
    if(string.isPresent()){
      return '"' + string.get() + '"';
    }
    else if(angledString.isPresent()){
      return '<'+angledString.get()+'>';
    }
    else if(mCQualifiedName.isPresent()){
      return mCQualifiedName.get().toString();
    }
    else if(cDEQualifiedColonName.isPresent()){
      return cDEQualifiedColonName.get().toString();
    }
    else if(cDEQualifiedDoubleColonName.isPresent()){
      return cDEQualifiedDoubleColonName.get().toString();
    }
    else {
      return "ASTCDEImportName.empty";
    }
  }
}
