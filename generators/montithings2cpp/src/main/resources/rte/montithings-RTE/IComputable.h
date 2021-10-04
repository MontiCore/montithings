/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

// There is a header in Windows, called combaseapi.h that 
// claims the name "interface" via #define as "struct". 
// This prevents MontiThings from using "interface" as a
// variable name. Hence we disable this #define here for
// Windows machines
#if defined(_WIN32) || defined(_WIN64) || defined(__CYGWIN__)
#undef interface
#endif

template<typename T, typename Y>
class IComputable
{
  public:
  virtual Y getInitialValues () = 0;
  virtual Y compute (T input) = 0;
};

