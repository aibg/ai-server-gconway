#include <string>
#include <vector>
#include <utility>
#include <iostream>
#ifndef UTILITY_H
#define UTILITY_H

/**
* ova klasa enkapsulira stanje
*/
class State {
    public:
        State(std::string json_line);
        std::ostream& commitAction(std::ostream& stream, const std::vector<std::pair<int,int>>& cells);

        int cellsRemaining;
        int maxGameIterations;
        int maxCellCapacity;
        int currIteration;
        int maxColonisationDistance;
        int cellGainPerTurn;
        int rows;
        int cols;
        std::vector<std::pair<int, int> > myCells;
        std::vector<std::string> field;

        const bool isMineCell(int row, int col) const;
        const bool isEnemyCell(int row, int col) const;
        const bool isEmptyCell(int row, int col) const;
        const bool inField(int row, int col) const;
};
#endif
