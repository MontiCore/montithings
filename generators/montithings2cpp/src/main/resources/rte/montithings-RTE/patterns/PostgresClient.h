#pragma once

#include <iostream>
#include "libpq-fe.h"

class PostgresClient
{
public:
  void exec(std::string query, std::string connectionStr);
};