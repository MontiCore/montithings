// (c) https://github.com/MontiCore/monticore
package montithings.generator

info("--------------------------------")
info("MT Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("HWC dir        : " + handwrittenCode)
debug("Platform       : " + _configuration.configParams.getTargetPlatform())
debug("--------------------------------")
generate(modelPath, out, handwrittenCode, _configuration.configParams)
info("--------------------------------")
info("MT Generator END")
info("--------------------------------")