// (c) https://github.com/MontiCore/monticore
//TODO alle benutzen Komponenten
#include "Example.h"
#include "Sink.h"
#include "Source.h"

#include "easyloggingpp/easylogging++.h"
#include "gtest/gtest.h"
#include <chrono>
#include <thread>

INITIALIZE_EASYLOGGINGPP


struct ExampleTest : testing::Test
{
  //TODO Speicher aller Componenten reservieren
  montithings::hierarchy::Example *cmp;
  montithings::hierarchy::Source *source;
  montithings::hierarchy::Sink *sink;
  montithings::hierarchy::SourceImpl *sourceImpl;
  montithings::hierarchy::SinkImpl *sinkImpl;
  montithings::hierarchy::SourceState *sourceState;
  montithings::hierarchy::SinkState *sinkState;

  ExampleTest ()
  {
    //TODO alle Componenten instanziieren
    cmp = new montithings::hierarchy::Example ("example");
    source = cmp->getSubcomp__Source();
    sink = cmp->getSubcomp__Sink();
    sourceImpl = source->getImpl();
    sinkImpl = sink->getImpl();
    sourceState = source->getState();
    sinkState = sink->getState();
  }

  ~ExampleTest ()
  {
    delete cmp;
  }
};
