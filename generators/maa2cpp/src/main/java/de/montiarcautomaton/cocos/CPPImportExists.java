package de.montiarcautomaton.cocos;

import java.io.File;
import java.nio.file.Paths;

import org.antlr.v4.parse.ANTLRParser.id_return;

import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTCPPImportStatement;
import montiarc._cocos.MontiArcASTCPPImportStatementCoCo;

public class CPPImportExists implements MontiArcASTCPPImportStatementCoCo {
	File hwcPath = null;

	public CPPImportExists(File hwcPath) {
		this.hwcPath = hwcPath;
	}

	@Override
	public void check(ASTCPPImportStatement node) {
		if (hwcPath == null) {
			Log.warn("0xMA302 No hwcPath was given to the generator. Skipping CPPImportExists CoCo");
			return;
		}
		if (!hwcPath.isDirectory()) {
			Log.error("0xMA303 hwcPath " + hwcPath.toString() + "does not exist!");
			return;
		}
		try {
			File importFile = Paths.get(hwcPath.getAbsolutePath(), "/include/" + node.getCppImport()).toFile();
			if (!importFile.isFile()) {
				Log.error("0xMA304 Import " + node.getCppImport() + " does not exist in include in "
						+ importFile.getAbsolutePath(), node.get_SourcePositionStart());
			}
		} catch (Exception e) {
		}

	}

}
