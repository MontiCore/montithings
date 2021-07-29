#!/bin/sh
# (c) https://github.com/MontiCore/monticore

# dont forget to source OpenDDS/setenv.sh first
tao_idl --idl-version 4 DDSMessage.idl
tao_idl --idl-version 4 DDSRecorderMessage.idl
opendds_idl DDSMessage.idl
opendds_idl DDSRecorderMessage.idl

# TODO FIX: xyzTypeSupport.idl wont be generated
tao_idl --idl-version 4 DDSRecorderMessageTypeSupport.idl

# Workaround: temporarily add the following to the cmakelists and copy files from build directory
#OPENDDS_TARGET_SOURCES(MontiThingsRTE "dds/message-types/DDSMessage.idl")
#OPENDDS_TARGET_SOURCES(MontiThingsRTE "dds/message-types/DDSRecorderMessage.idl")