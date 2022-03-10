// (c) https://github.com/MontiCore/monticore
package de.monticore.lang.sd4componenttesting.generator.script

info("--------------------------------")
info("SD4C Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Test path      : " + testPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("--------------------------------")
generate(modelPath, testPath, out)
info("--------------------------------")
info("SD4C Generator END")
info("--------------------------------")