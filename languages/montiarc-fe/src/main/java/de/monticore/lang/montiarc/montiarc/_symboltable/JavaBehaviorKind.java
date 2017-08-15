/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.lang.montiarc.montiarc._symboltable;

import de.monticore.symboltable.SymbolKind;

/**
 * Symbol Kind of AJavaDefinitions.
 *
 * @author Andreas Wortmann
 *
 */
public class JavaBehaviorKind implements SymbolKind {
	
  private static final String NAME = JavaBehaviorKind.class.getName();

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || SymbolKind.super.isKindOf(kind);
  }
}