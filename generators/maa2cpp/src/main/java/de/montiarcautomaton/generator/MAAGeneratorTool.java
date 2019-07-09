/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarcautomaton.generator;

import de.montiarcautomaton.cocos.CPPImportExists;
import de.montiarcautomaton.cocos.NoAJavaBehaviourInComponents;
import de.montiarcautomaton.cocos.NoJavaImportsForCPPGenerator;
import de.montiarcautomaton.generator.codegen.xtend.MAAGenerator;
import de.monticore.ast.ASTNode;
import de.monticore.cd2pojo.Modelfinder;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTMontiArcNode;
import montiarc._cocos.MontiArcCoCoChecker;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.MontiArcLanguage;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * TODO
 *
 * @author (last commit) JFuerste
 */
public class MAAGeneratorTool extends MontiArcTool {

	public static final String LIBRARY_MODELS_FOLDER = "target/librarymodels/";

	public void generate(File modelPath, File target, File hwcPath) {
		// 1. Add CPP specific cocos to CoCoChecker
		checker.addCoCo(new NoAJavaBehaviourInComponents());
		checker.addCoCo(new NoJavaImportsForCPPGenerator());
		checker.addCoCo(new CPPImportExists(hwcPath));

		List<String> foundModels = Modelfinder.getModelsInModelPath(modelPath, MontiArcLanguage.FILE_ENDING);
		// 2. Initialize SymbolTable
		Log.info("Initializing symboltable", "MontiArcGeneratorTool");
		String basedir = getBasedirFromModelAndTargetPath(modelPath.getAbsolutePath(), target.getAbsolutePath());
		Scope symTab = initSymbolTable(true, modelPath, Paths.get(basedir + LIBRARY_MODELS_FOLDER).toFile(), hwcPath);

		for (String model : foundModels) {
			String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);

			// 3. parse + resolve model
			Log.info("Parsing model:" + qualifiedModelName, "MontiArcGeneratorTool");
			ComponentSymbol comp = symTab.<ComponentSymbol>resolve(qualifiedModelName, ComponentSymbol.KIND).get();

			// 4. check cocos
			Log.info("Check model: " + qualifiedModelName, "MontiArcGeneratorTool");
			checkCoCos((ASTMontiArcNode) comp.getAstNode().get());

			// 5. generate
			Log.info("Generate model: " + qualifiedModelName, "MontiArcGeneratorTool");
			MAAGenerator.generateAll(
					Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName())).toFile(),
					hwcPath, comp, foundModels);

		}

		for (String model : foundModels) {
			String qualifiedModelName = Names.getQualifier(model) + "." + Names.getSimpleName(model);
			ComponentSymbol comp = symTab.<ComponentSymbol>resolve(qualifiedModelName, ComponentSymbol.KIND).get();

			if (comp.getStereotype().containsKey("deploy")) {
				File libraryPath = Paths.get(target.getAbsolutePath(), "libraries").toFile();
				// 5 generate libs
				MAAGenerator.generateLibs(libraryPath);
				// 6 generate make file
				Log.info("Generate CMake file", "MontiArcGeneratorTool");
				MAAGenerator.generateMakeFile(
						Paths.get(target.getAbsolutePath(), Names.getPathFromPackage(comp.getPackageName())).toFile(),
						comp, hwcPath, libraryPath);
			}

		}

	}

	/**
	 * Compares the two paths and returns the common path. The common path is the
	 * basedir.
	 * 
	 * @param modelPath
	 * @param targetPath
	 * @return
	 */
	private String getBasedirFromModelAndTargetPath(String modelPath, String targetPath) {
		String basedir = "";

		for (int i = 0; i < modelPath.length(); i++) {
			if (modelPath.charAt(i) == targetPath.charAt(i)) {
				basedir += modelPath.charAt(i);
			} else {
				break;
			}

		}
		return basedir;
	}

}
