#include <iostream>
#include <string>
#include <vector>
#include <random>
#include <algorithm>

#include "utility.hpp"

int distance(int x1, int y1, int x2, int y2) {
    return std::max(abs(x1 - x2), abs(y1 - y2));
}

bool friendlyWithinDistance(const State& st, int r, int s) {
    int d = st.maxColonisationDistance;
    for (int i = r - d; i <= r + d; ++i)
        for (int j = s - d; j <= s + d; ++j) {
            if (st.isMineCell(i, j) && distance(i, j, r, s) <= d)
                return true;
    }
    return false;
}

std::vector<std::pair<int, int>> dummyActions (const State& st) {
  static std::random_device rd;
  static std::mt19937 g(rd());

  std::vector<std::pair<int, int>> possibleMoves;
  for (int i = 0; i < st.rows; i++) {
      for (int j = 0; j < st.cols; j++) {
          if (st.isEmptyCell(i, j) && friendlyWithinDistance(st, i, j))
              possibleMoves.push_back({i,j});
      }
  }
  std::shuffle(possibleMoves.begin(), possibleMoves.end(), g);

  if (possibleMoves.size() > st.cellsRemaining)
    possibleMoves.resize(st.cellsRemaining);

  return possibleMoves;
}

int main() {
    for( std::string line; getline( std::cin, line );){
        auto state = State(line);
        state.commitAction(std::cout, dummyActions(state));
    }
}
