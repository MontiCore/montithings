/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/* (c) https://github.com/MontiCore/monticore */

#pragma once
#include "tl/optional.hpp"
#include "sole/sole.hpp"
#include "UniqueElement.h"

template<typename T>
class MessageAcceptor
{
  public:
  /* ============================================================ */
  /* ========================= Set Values ======================= */
  /* ============================================================ */

  /**
   * Set the next value to be accepted
   * \param nextVal the value to be accepted
   */
  virtual void setNextValue (T nextVal) = 0;

  /**
   * Set the next value to be accepted
   * \param nextVal the value to be accepted; may be empty
   */
  virtual void setNextValue (tl::optional<T> nextVal) = 0;

  MessageAcceptor ()
  {
  }
};