/*!
  utility.cpp provides a user with useful methods for JSON communication.
*/

#include <string>
#include <iostream>
#include <cmath>

#include "json/src/json.hpp"
#include "utility.hpp"
using nlohmann::json;

State::State(std::string json_line) {
    auto state = json::parse(json_line);

    maxColonisationDistance = state["maxColonisationDistance"];
    cellGainPerTurn = state["cellGainPerTurn"];
    cellsRemaining = state["cellsRemaining"];
    maxGameIterations = state["maxGameIterations"];
    maxCellCapacity = state["maxCellCapacity"];
    currIteration = state["currIteration"];
    field = state["field"].get<std::vector<std::string>>();
    rows = field.size();
    cols = field[0].size();
}

const bool State::inField(int row, int col) const {
  return row >= 0 && col >= 0 && row < rows && col < cols;
}

const bool State::isMineCell(int row, int col) const {
  return inField(row, col) && field[row][col] == '#';
}

const bool State::isEnemyCell(int row, int col) const {
  return  inField(row, col) && field[row][col] == 'O';
}

const bool State::isEmptyCell(int row, int col) const {
  return  inField(row, col) && field[row][col] == '.';
}

std::ostream& State::commitAction(std::ostream& stream, const std::vector<std::pair<int, int > >& cells) {
    json j = json::object();
    j["cells"] = json::array();

    for (auto c : cells) {
        j["cells"].push_back({c.first, c.second});
    }

    stream << j << std::endl;
    return stream;
}
