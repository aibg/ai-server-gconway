#!/usr/bin/env python3
import fileinput
import random
from conway.utility import State

def distance(x1, y1, x2, y2):
        return max(abs(x1 - x2), abs(y1 - y2))

if __name__ == '__main__':
    for line in fileinput.input(bufsize=1):
        stanje = State(line)

        mycells = list()
        for i, row in enumerate(stanje.field):
            for j, char in enumerate(row):
                if char == "#":
                    mycells.append([i, j])

        possiblemoves = list()
        for i, row in enumerate(stanje.field):
            for j, char in enumerate(row):
                for k in mycells:
                    if distance(i, j, k[0], k[1]) <= stanje.maxColonisationDistance and stanje.is_empty(i, j):
                        possiblemoves.append([i, j])

        if stanje.cellsRemaining < len(possiblemoves):
            ukljuci = random.sample(possiblemoves, stanje.cellsRemaining)
        else:
            ukljuci = possiblemoves
        stanje.submit_move(ukljuci)
