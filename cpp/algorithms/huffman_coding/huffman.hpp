#ifndef HUFFMAN_HPP
#define HUFFMAN_HPP

#include <vector>
#include <cstddef>
#include <string>
#include <iostream>

std::vector<size_t> getFrequencies(const std::string& input);
std::vector<size_t> getFrequencies(std::istream& in);

void compress(const std::string& input);
void compress(std::istream& in);

#endif
