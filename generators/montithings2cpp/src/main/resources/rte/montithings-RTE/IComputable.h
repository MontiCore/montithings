/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once

template<typename T, typename Y>
class IComputable
{
  public:
  virtual Y getInitialValues () = 0;
  virtual Y compute (T input) = 0;
};

