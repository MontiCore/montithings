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
package de.monticore.lang.tagschema._ast;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by MichaelvonWenckstern on 15.06.2016.
 */
public class ASTEnumeratedTagType extends ASTEnumeratedTagTypeTOP {
    protected ASTEnumeratedTagType() {
        super();
    }

    protected Set<String> enumValues = new LinkedHashSet<>();

    protected ASTEnumeratedTagType(String name, String enumText, Optional<ASTScope> scope) {
        super(name, enumText, scope);
        this.enumValues = enumValues;
    }

    public void setEnumText(String enumText) {
        if (enumText != null) {
            if (enumText.startsWith("[")) {
                enumText = enumText.substring(1);
            }
            if (enumText.endsWith("]")) {
                enumText = enumText.substring(0, enumText.length() - 1);
            }
            enumText = enumText.trim();
            // TODO fix: now it also splits BC and D in A | "BC|D" | X
            String vs[] = enumText.split("\\|");
            for (String v : vs) {
                enumValues.add(v.trim());
            }
        }
        super.setEnumText(enumText);
    }

    public Set<String> getEnumValues() {
        return enumValues;
    }
}
