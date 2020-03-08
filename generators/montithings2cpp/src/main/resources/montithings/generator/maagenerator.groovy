// (c) https://github.com/MontiCore/monticore
package montithings.generator

info("--------------------------------")
info("MAA Generator")
info("--------------------------------")
debug("Model path     : " + modelPath)
debug("Output dir     : " + out.getAbsolutePath())
debug("HWC dir        : " + handwrittenCode)
debug("Platform       : " + platform)
debug("--------------------------------")
generate(modelPath, out, handwrittenCode, platform)
info("--------------------------------")
info("MAA Generator END")
info("--------------------------------")