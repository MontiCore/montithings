package components.body.subcomponents;

import components.body.subcomponents._subcomponents.HasPortWithImportedType;
import components.body.subcomponents._subcomponents.package1.*;
import components.body.subcomponents._subcomponents.AtomicComponent;

/*
 * Invalid component.
 *
 * Referenced components must exist in the denoted package. If component is
 * referenced unqualified, the component must exist in the same package, must
 * be imported full qualified or through a package import.
 *
 * Formerly named "R2" in MontiArc3.
 */
component ValidAndInvalidSubcomponents {
  
  component AtomicComponent; //Correct
  component HasPortWithImportedType; //Correct
  component ValidComponentInPackage1; //Correct
  component components.body.subcomponents._subcomponents.AtomicComponent; //Correct
  component components.body.subcomponents.UndefinedReferenceFQ;
      //ERROR: No component named "UndefinedReferenceFQ" in package
      //        'components.body.subcomponents'
  component UndefinedReferenceInSamePackage;
      //ERROR: No component named "UndefinedReferenceInSamePackage" in package
      //        'components.body.subcomponents'
}