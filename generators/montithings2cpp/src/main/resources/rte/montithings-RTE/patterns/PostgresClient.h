#pragma once

#include <iostream>
#include <pqxx/pqxx>

class PostgresClient
{
public:
  void exec(std::string query, std::string connectionStr);
};