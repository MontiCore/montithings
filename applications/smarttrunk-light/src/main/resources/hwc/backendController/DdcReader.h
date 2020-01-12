// (c) https://github.com/MontiCore/monticore
/* -*- Mode:C++; c-file-style:"gnu"; indent-tabs-mode:nil; -*- */
/*
 * Copyright (c) RWTH Aachen. All rights reserved.
 *
 * Created by Jörg Christian Kirchhof on 03.05.19.
 *
 * http://www.se-rwth.de/
 */

#ifndef SMARTTRUNK_DDC_READER_H
#define SMARTTRUNK_DDC_READER_H

#include <algorithm>
#include <array>
#include <cstdint>
#include <cstdio>
#include <iomanip>
#include <iostream>
#include <memory>
#include <sstream>
#include <stdexcept>
#include <string>

template <typename T> class Reader {
public:
  virtual ~Reader() {}
  virtual double readDouble(uint32_t identifier) = 0;
  virtual bool readBool(uint32_t identifier) = 0;
  virtual int readInt(uint32_t identifier) = 0;
  virtual long readLong(uint32_t identifier) = 0;
  virtual std::string readString(uint32_t identifier) = 0;
  virtual void write(uint32_t identifier, T value) = 0;
};

template <typename T> class DdcReader : public Reader<T> {
public:
  /**
   * Read a single double value from the DDC
   * \param identifier ID of the DDC value to be read
   * \return true if successful, false otherwise
   */
  double readDouble(uint32_t identifier);

  /**
   * Read a single bool value from the DDC
   * \param identifier ID of the DDC value to be read
   * \return true if successful, false otherwise
   */
  bool readBool(uint32_t identifier);

  /**
   * Read a single int value from the DDC
   * \param identifier ID of the DDC value to be read
   * \return true if successful, false otherwise
   */
  int readInt(uint32_t identifier);

  /**
   * Read a single long value from the DDC
   * \param identifier ID of the DDC value to be read
   * \return true if successful, false otherwise
   */
  long readLong(uint32_t identifier);

  /**
   * Read a single string value from the DDC
   * \param identifier ID of the DDC value to be read
   * \return true if successful, false otherwise
   */
  std::string readString(uint32_t identifier);

  /**
   * Write a value to the DDC. It is not checked whether the type matches the
   * type in the DDC. \tparam T type of the parameter to be written \param
   * identifier ID of the DDC value to be written \param value value that should
   * be written
   */
  void write(uint32_t identifier, T value);

private:
  /**
   * Execute a system call and return the output
   *
   * From https://stackoverflow.com/a/478960/
   *
   * \param cmd the command to be executed
   * \return the output produced by the system call
   */
  static std::string exec(const char *cmd);

  static std::string getReadCmd(uint32_t identifier);
};

template <typename T> std::string DdcReader<T>::exec(const char *cmd) {
  std::array<char, 128> buffer;
  std::string result;
  std::unique_ptr<FILE, decltype(&pclose)> pipe(popen(cmd, "r"), pclose);
  if (!pipe) {
    throw std::runtime_error("popen() failed!");
  }
  while (fgets(buffer.data(), buffer.size(), pipe.get()) != nullptr) {
    result += buffer.data();
  }
  return result;
}

template <typename T> double DdcReader<T>::readDouble(uint32_t identifier) {
  std::string command = getReadCmd(identifier);
  std::string valueStr = exec(command.c_str());
  return std::stod(valueStr);
}

template <typename T> bool DdcReader<T>::readBool(uint32_t identifier) {
  return readInt(identifier) == 1;
}

template <typename T> int DdcReader<T>::readInt(uint32_t identifier) {
  std::string command = getReadCmd(identifier);
  std::string valueStr = exec(command.c_str());
  return std::stoi(valueStr);
}

template <typename T> long DdcReader<T>::readLong(uint32_t identifier) {
  std::string command = getReadCmd(identifier);
  std::string valueStr = exec(command.c_str());
  return std::stol(valueStr);
}

template <typename T>
std::string DdcReader<T>::readString(uint32_t identifier) {
  std::string command = getReadCmd(identifier);
  std::string result = exec(command.c_str());

  if (result.compare(result.size() - 1, 1, "\n") == 0) {
    result.pop_back();
  }
  return result;
}

template <typename T>
std::string DdcReader<T>::getReadCmd(uint32_t identifier) {
  std::ostringstream oss;
  oss << "ddctool -r -u " << std::setfill('0') << std::setw(4) << std::hex
      << identifier << " -q";
  return oss.str();
}

template <typename T> void DdcReader<T>::write(uint32_t identifier, T value) {
  std::ostringstream oss;
  oss << "ddctool -w -u " << std::setfill('0') << std::setw(4) << std::hex
      << identifier << " -v " << value;
  exec(oss.str().c_str());
}

#endif // SMARTTRUNK_DDC_READER_H
