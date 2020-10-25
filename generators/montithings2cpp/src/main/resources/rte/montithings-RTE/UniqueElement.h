/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
// (c) https://github.com/MontiCore/monticore

#pragma once

#include "sole/sole.hpp"

class UniqueElement {
  protected:
  sole::uuid uuid = sole::uuid4 ();

  /* ============================================================ */
  /* ======================= GENERATED CODE ===================== */
  /* ============================================================ */

  public:
  const sole::uuid &getUuid () const
  {
    return uuid;
  }
};
