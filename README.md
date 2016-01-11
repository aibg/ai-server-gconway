# AI Battleground - the variation of Conway's Game of Life

AI Battleground is a multiplayer artificial intelligence game that uses Conway's
Game of Life rules and some additional restrictions for multiplayer game.

# Installation

* make sure you've run ```mvn install``` on ```ai-server-core``` since it is a dependency
* clone this repository
* ```mvn package```
* in target directory is ```ai-server-gconway.jar```
* Start ordinary game server with ```java -jar ai-server-gconway.jar [config file]```
* Start test grid with ```java -cp ai-server-gconway.jar hr.best.ai.games.conway.TestGrid [config file]```
* Start historic run game with ```java -cp ai-server-gconway.jar hr.best.ai.games.conway.HistoricRunGame [config file]```

## Explanation of different game running modes

There are three classes which you can use:

* RunGame==> standard way of executing the game

* HistoricRunGame ==> similar but it first simulates the game without visualization and then you can browse through the iterations back and forth using keyboard keys 'J' for backward and 'K' for forward.

* TestGrid ==> This enables you to try out the game yourself. You can select player 1 action cells with left click, player 2 action cells with right click. Middle click is used to undo the selected cells.  Press ENTER for next state.

## Config file details

Observe supplied config.json. After reading this whole document it should be clear what each parameter means, (especially last JSON serialization/deserializatoin sections). One last thing of interest is player types:

* dummy -> does nothing
* process -> wraps process IO run by command into player. STDERR is logged to output. ```command``` specified shell command, and optionally ```workingDirectory``` specifies working directory for command.
* tcp -> accepts connection from client player on config designated port

# Gconway game mechanic

Classic Game of Life ruleset specifies:

	1) any live cell with fewer than two live neighbours dies, as if caused
	by under-population

	2) any live cell with two or three live neighbours lives on to the next
	generation

	3) any live cell with more than three live neighbours dies, as if by
	over-population

	4) any dead cell with exactly three live neighbours becomes a live
	cell, as if by reproduction

More info about the classic Game of life can be found on [wikipedia](https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life)

The game depends only on the initial pattern of the system, on the other hand,
in AI Battleground it is possible (and is the main problem) to migrate, e.g.
to colonize cells.

The winner of the game is the one that covers greater number of cells after
game HALTs (for example, HALT can be reasonable time limit or, in our case,
number of iterations).

## Game iteration phases

Game mechanic can be described in following phases.

### Phase 0

Current game state is sent to both players.

### Phase 1

Players asynchronously and concurrently compute their action for current iteration
(dead cells they want to activate before next state is calculated). To better understand
what an action is consider the following:
 * In chess action could be "move king to E4" and it is realized while the other player waits for its turn.
 * In our ```gconway```, players select grid field they want to colonize, that is transform currently dead cell to their own live cell. There are some limitations which fields can be colonized, such as limited distance from friendly cells, maximum number of colonizations per turn, cell must be dead, etc. At the same time other player is doing the same.

### Phase 2

Player input is collected and executed. In case of collisions, that is both players
colonizing same cell they cancel each other out, leaving the cell dead. At the end of
Phase 2 we get intermediary state.

### Phase 3

In this phase we iterate one ```gconway``` iteration. Before proceeding please make sure
original Conways game of life iteration is clear. Our generalization consist of simply
calculating the differences between each player cell count to determine what happens to given cell.

For example, we have a dead cell. This cell has 8 neighboring cells (field is implemented
as torus or an american donut, that is, it wraps around, both vertically and horizontally).
Out of those 8 cells, there's ```p1``` player 1 cells and ```p2``` player 2 cells. In our
model they "annihilate" and we're only interested in their difference, that is: ```p1 - p2```.
If ```p1``` is bigger and ```p1 - p2 == 3``` targeted cell becomes player 1 cell in
next state. Notice when ```p2 == 0``` ```gconway``` reduces to original game. Same logic
applies to player 2 due to symmetry. Analogous generalization is valid regarding live cells.  

After this phase game iterates back to phase 0.

## Determining winner

Winner is the player occupying maximum cells at the last move. This can be changed to, for
example, sum of individual player cells through all iterations.

# AI Battleground graphics engine

The current AI Battleground implementation provides a simple GUI for battle
simulations (PvP). The GUI includes:

	- a finite number of cells representing battleground
	- colored rectangles representing each player's cells
	- players' names in the upper left and right corners of the screen
	- players' overall points below the names
	- illustrative domination chart at the top of the screen
	- AI Battleground background :)


# System-player communication details

In the current version of AI Battleground, the user communicates with the
system using JSON (1*). All of the JSON attributes must be provided as an
answer to avoid exceptions or illegal states.

The answer is commited via stream (stdout). For C++, Python and Java
languages, additional utility function are provided for parsing JSON and
automatically committing actions.


(1*) Details about JSON attributes can be found in src/main/java/hr/best/...
/ai/games/conway/ConwayGameState.java, ConwayGameState class. Also see next section about JSON sent to players.


## JSON sent to players

Each player gets his own personalized JSON message. For example "field" element has symbol
"#" for friendly cell, that is player 1 cells for player 1 and player 2 cells for player 2.
JSON is sent to players on each iteration and has the following elements (we will use
configuration file finalConfig for exact number like the field size):

"field":

JSON array consisting of 24 elements. Each one is a 24 character string(the elements
are game field rows). '#' represents friendly live cell, 'O' enemy live cell and '.' dead cell.

"cellsRemaining":

Number of cells that can be activated by player in this turn (cannot go over maxCellCapacity)

"cellGainPerTurn":

Number of cells gained for activation each turn

"maxCellCapacity":

Maximum number of cells which can be stored for use. You could never have more cells
than maxCellCapacity to activate in a single turn.

"maxColonisationDistance":

Furthest distance from a friendly cell that a cell can be activated. In finalConfig
it is 2, which means a player can activate any cell in a 5x5 square around any of his
live cells (provided it doesn't conflict with other limitations)

"currIteration":

Current iteration. Starts from 0.

"maxGameIterations":

The name says it all. We went with 500 which we later concluded is too much when you
need to watch 10+ teams and have enough battles for the competition to be fair.

"timeGainPerTurn":

Number of milliseconds gained per turn. Players have limited time for their calculations
(although it's a great project we don't want to be stuck there for an eternity). we went with 300.

"timeLeftForMove":

Number of milliseconds player has this turn. It is implemented to be a bit larger at the start.
We did this so the JVM has time to "warm up".

....

## JSON which players send (player action):

One action is composed of list of cells player wishes to activate this turn. For example, player may wish to send something like this:
{"cells": [[10, 20], [10, 18], [12, 15], [10, 16], [12, 20]]}
