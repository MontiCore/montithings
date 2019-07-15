/**
 *
 *  ******************************************************************************
 *  MontiCAR Modeling Family, www.se-rwth.de
 *  Copyright (c) 2017, Software Engineering Group at RWTH Aachen,
 *  All rights reserved.
 *
 *  This project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3.0 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *******************************************************************************
 */
package de.monticore.lang.tagging.helper;

import de.monticore.literals.literals._ast.ASTDoubleLiteral;
import de.monticore.literals.literals._ast.ASTFloatLiteral;
import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLongLiteral;
import de.monticore.literals.literals._ast.ASTNumericLiteral;
import de.monticore.literals.literals._ast.ASTSignedDoubleLiteral;
import de.monticore.literals.literals._ast.ASTSignedFloatLiteral;
import de.monticore.literals.literals._ast.ASTSignedIntLiteral;
import de.monticore.literals.literals._ast.ASTSignedLongLiteral;

/**
 * Created by Michael von Wenckstern on 17.06.2016.
 */
public class NumericLiteral {
  // TODO ASTNumericLiteral should have a getValue() method
  public static Number getValue(ASTNumericLiteral numericLiteral) {
    if (numericLiteral instanceof ASTDoubleLiteral) {
      return ((ASTDoubleLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedDoubleLiteral) {
      return ((ASTSignedDoubleLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTIntLiteral) {
      return ((ASTIntLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedIntLiteral) {
      return ((ASTSignedIntLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTFloatLiteral) {
      return ((ASTFloatLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedFloatLiteral) {
      return ((ASTSignedFloatLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTLongLiteral) {
      return ((ASTLongLiteral) numericLiteral).getValue();
    }
    else if (numericLiteral instanceof ASTSignedLongLiteral) {
      return ((ASTSignedLongLiteral) numericLiteral).getValue();
    }
    else {
      throw new Error("unexpected ASTNumericLiteral: " + numericLiteral.getClass());
    }
  }
}
