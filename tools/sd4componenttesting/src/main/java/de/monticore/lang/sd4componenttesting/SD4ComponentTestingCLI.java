/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.sd4componenttesting;

import de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact;
import de.monticore.lang.sd4componenttesting._cocos.SD4ComponentTestingCoCos;
import de.monticore.lang.sd4componenttesting._symboltable.ISD4ComponentTestingArtifactScope;
import de.monticore.lang.sd4componenttesting._symboltable.SD4ComponentTestingScopesGenitorDelegator;
import de.monticore.lang.sd4componenttesting._visitor.SD4ComponentTestingFullPrettyPrinter;
import de.monticore.lang.sd4componenttesting.generator.SD4ComponentTestingGenerator;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * CLI tool providing functionality for processing SD4ComponentTesting
 * artifacts.
 */
public class SD4ComponentTestingCLI {

  public static void main(String[] args) {
    SD4ComponentTestingCLI cli = new SD4ComponentTestingCLI();
    cli.run(args);
  }

  public void run(String[] args) {
    Options options = initOptions();

    CommandLineParser cliparser = new DefaultParser();
    SD4ComponentTestingTool sd4ctTool = new SD4ComponentTestingTool();
    try {
      CommandLine cmd = cliparser.parse(options, args);

      // help or no input
      if (cmd.hasOption("h") || !cmd.hasOption("i")) {
        printHelp(options);
        return;
      }

      // disable debug messages
      Log.initWARN();

      // disable fail quick to log as much errors as possible
      Log.enableFailQuick(false);

      // Parse input SDs
      List<ASTSD4Artifact> inputSD4CTs = new ArrayList<>();
      for (String inputFileName : cmd.getOptionValues("i")) {
        ASTSD4Artifact ast = parse(inputFileName);
        inputSD4CTs.add(ast);
      }
      if (Log.getErrorCount() > 0) {
        return;
      }

      // pretty print
      if (cmd.hasOption("pp")) {
        if (cmd.getOptionValues("pp") == null || cmd.getOptionValues("pp").length == 0) {
          for (ASTSD4Artifact sd4ct : inputSD4CTs) {
            prettyPrint(sd4ct, "");
            System.out.println();
          }
        } else if (cmd.getOptionValues("pp").length != inputSD4CTs.size()) {
          Log.error(String.format(
              "Received '%s' output files for the prettyprint option. "
                  + "Expected that '%s' many output files are specified. "
                  + "If output files for the prettyprint option are specified, then the number "
                  + " of specified output files must be equal to the number of specified input files.",
              cmd.getOptionValues("pp").length, inputSD4CTs.size()));
          return;
        } else {
          for (int i = 0; i < inputSD4CTs.size(); i++) {
            ASTSD4Artifact sd4ct_i = inputSD4CTs.get(i);
            prettyPrint(sd4ct_i, cmd.getOptionValues("pp")[i]);
            System.out.println("Pretty Printer: Saved " + cmd.getOptionValues("pp")[i]);
          }
        }
      }

      if (cmd.hasOption("path")) {
        sd4ctTool.initSymbolTable(Arrays.stream(cmd.getOptionValues("path")).map(x -> new File(x)).toArray(File[]::new));
      } else {
        sd4ctTool.initSymbolTable(new File("./"));
      }

      // handle CoCos and symbol storage: build symbol table as far as needed
      if (cmd.hasOption("c") || cmd.hasOption("g")) {
        for (ASTSD4Artifact sd4ct : inputSD4CTs) {
          createSymbolTable(sd4ct);
        }
        if (Log.getErrorCount() > 0) {
          return;
        }
      }

      // cocos
      if (cmd.hasOption("c")) {
        for (ASTSD4Artifact sd4ct : inputSD4CTs) {
          checkAllCoCos(sd4ct);
        }
        System.out.println("CoCos: All Checked!");
      }

      if (Log.getErrorCount() > 0) {
        // if the model is not well-formed, then stop before generating anything
        return;
      }

      if (cmd.hasOption("g")) {
        if (cmd.getOptionValues("g") == null || cmd.getOptionValues("g").length == 0) {
          for (ASTSD4Artifact sd4ct : inputSD4CTs) {
            SD4ComponentTestingGenerator.generate(sd4ct, sd4ct.getTestDiagram().getName() + ".cpp");
            System.out.println("Generator: Generated " + sd4ct.getTestDiagram().getName() + ".cpp");
          }
        } else if (cmd.getOptionValues("g").length != inputSD4CTs.size()) {
          Log.error(String.format(
              "Received '%s' output files for the generator option. "
                  + "Expected that '%s' many output files are specified. "
                  + "If output files for the generator option are specified, then the number "
                  + " of specified output files must be equal to the number of specified input files.",
              cmd.getOptionValues("g").length, inputSD4CTs.size()));
          return;
        } else {
          for (int i = 0; i < inputSD4CTs.size(); i++) {
            ASTSD4Artifact sd4ct_i = inputSD4CTs.get(i);
            SD4ComponentTestingGenerator.generate(sd4ct_i, cmd.getOptionValues("g")[i]);
            System.out.println("Generator: Generated " + cmd.getOptionValues("g")[i]);
          }
        }
      }

      // fail quick in case of symbol storing
      Log.enableFailQuick(true);
    } catch (ParseException e) {
      // unexpected error from apache CLI parser
      Log.error("0xA7101 Could not process CLI parameters: " + e.getMessage());
    }

  }

  /**
   * Pretty prints the ast and returns the result as String.
   *
   * @param ast The ast to be printed
   * @return Pretty-printed ast.
   */
  public void prettyPrint(ASTSD4Artifact ast, String file) {
    SD4ComponentTestingFullPrettyPrinter prettyPrinter = new SD4ComponentTestingFullPrettyPrinter(new IndentPrinter());
    print(prettyPrinter.prettyprint(ast), file);
  }

  /**
   * Derives symbols for ast and adds them to the globalScope.
   *
   * @param ast The ast of the SD4CT.
   */
  public ISD4ComponentTestingArtifactScope createSymbolTable(ASTSD4Artifact ast) {
    SD4ComponentTestingScopesGenitorDelegator genitor = SD4ComponentTestingMill.scopesGenitorDelegator();
    return genitor.createFromAST(ast);
  }

  /**
   * Checks whether ast satisfies all CoCos.
   *
   * @param ast The ast of the SD4CT.
   */
  public void checkAllCoCos(ASTSD4Artifact ast) {
    SD4ComponentTestingCoCos.createChecker().checkAll(ast);
  }

  public Options addStandardOptions(Options options) {
    // help info
    options.addOption(Option.builder("h").longOpt("help").desc("Prints this help informations.").build());

    // inputs
    options.addOption(Option.builder("i").longOpt("input").hasArgs().desc("Processes the list of SD4CT input artifacts. " + "Argument list is space separated. " + "CoCos are not checked automatically (see -c).").build());

    // pretty print
    options.addOption(Option.builder("pp").longOpt("prettyprint").argName("file").optionalArg(true).hasArgs().desc("Prints the input SD4CTs to stdout or to the specified file (optional).").build());
  
    // generator
    options.addOption(Option.builder("g").longOpt("generate").argName("file").optionalArg(true).hasArgs().desc("Generate c++ tests for each SD4CT and save them in specified files (optional) or based on the sd4c file name.").build());

    // model paths
    options.addOption(Option.builder("path").hasArgs().desc("Sets the artifact path for imported symbols, space separated.").build());

    return options;
  }

  public Options addAdditionalOptions(Options options) {
    // cocos
    options.addOption(Option.builder("c").longOpt("coco").desc("Checks all CoCos for the input.").build());

    return options;
  }

  public void printHelp(org.apache.commons.cli.Options options) {
    org.apache.commons.cli.HelpFormatter formatter = new org.apache.commons.cli.HelpFormatter();
    formatter.setWidth(80);
    formatter.printHelp("SD4ComponentTestingCLI", options);
  }

  public org.apache.commons.cli.Options initOptions() {
    org.apache.commons.cli.Options options = new org.apache.commons.cli.Options();
    options = addStandardOptions(options);
    options = addAdditionalOptions(options);
    return options;
  }

  public de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact parse(String model) {
    try {
      de.monticore.lang.sd4componenttesting._parser.SD4ComponentTestingParser parser = de.monticore.lang.sd4componenttesting.SD4ComponentTestingMill.parser();
      Optional<de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact> optAst = parser.parse(model);

      if (!parser.hasErrors() && optAst.isPresent()) {
        return optAst.get();
      }
      Log.error("0xA1050x59701 Model could not be parsed.");
    } catch (NullPointerException | java.io.IOException e) {
      Log.error("0xA1051x75994 Failed to parse " + model, e);
    }
    // should never be reached (unless failquick is off)
    return null;
  }

  public void print(String content, String path) {
    // print to stdout or file
    if (path == null || path.isEmpty()) {
      System.out.println(content);
    } else {
      File f = new File(path);
      // create directories (logs error otherwise)
      f.getAbsoluteFile().getParentFile().mkdirs();

      FileWriter writer;
      try {
        writer = new FileWriter(f);
        writer.write(content);
        writer.close();
      } catch (IOException e) {
        Log.error("0xA7105 Could not write to file " + f.getAbsolutePath());
      }
    }
  }
}
