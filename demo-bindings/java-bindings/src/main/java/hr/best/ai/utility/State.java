package hr.best.ai.utility;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class State {
	private final int cellsRemaining;
	private final int cellGainPerTurn;
	private final int maxCellCapacity;
	private final int maxColonisationDistance;
	private final int currIteration;
	private final int maxGameIterations;
	private final int height;
	private final int width;
	private final char[][] field;
	private final List<Cell> legalCells;

	public int getCellsRemaining() {
		return cellsRemaining;
	}

	public int getCellGainPerTurn() {
		return cellGainPerTurn;
	}

	public int getCurrIteration() {
		return currIteration;
	}

	public int getMaxCellCapacity() {
		return maxCellCapacity;
	}

	public int getMaxColonisationDistance() {
		return maxColonisationDistance;
	}

	public int getMaxGameIterations() {
		return maxGameIterations;
	}

	public int getCell(int row, int col) {
		return field[row][col];
	}

	public List<Cell> getLegalCells() {
		return legalCells;
	}

	public State(String jsonString) {

		JsonObject state = new JsonParser().parse(jsonString).getAsJsonObject();

		JsonArray jsonField = state.get("field").getAsJsonArray();
		height = jsonField.size();
		width = jsonField.get(0).getAsString().length();
		field = new char[height][width];
		for (int i = 0; i < height; i++) {
			String line = jsonField.get(i).getAsString();
			for (int j = 0; j < width; j++) {
				field[i][j] = line.charAt(j);
			}
		}

		cellsRemaining = state.get("cellsRemaining").getAsInt();
		cellGainPerTurn = state.get("cellGainPerTurn").getAsInt();
		maxCellCapacity = state.get("maxCellCapacity").getAsInt();
		maxColonisationDistance = state.get("maxColonisationDistance")
				.getAsInt();
		currIteration = state.get("currIteration").getAsInt();
		maxGameIterations = state.get("maxGameIterations").getAsInt();

		legalCells = new ArrayList<Cell>();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (field[i][j] == '.'
						&& manhattanDistanceToFriendlyCell(i, j) < maxColonisationDistance) {
					legalCells.add(new Cell(i, j));
				}
			}
		}
	}

	/**
	 * calculates distance to nearby friendly cell
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	private int manhattanDistanceToFriendlyCell(int row, int col) {
		int distance = Integer.MAX_VALUE;
		for (int r = row - maxColonisationDistance; r <= row
				+ maxColonisationDistance; ++r) {
			int verticalDistance = maxColonisationDistance - Math.abs(r - row);
			for (int c = col - verticalDistance; c <= col + verticalDistance; ++c) {
				if (outOfField(r, c))
					continue;
				if (field[r][c] == '#')
					distance = Math.min(distance,
							Math.abs(r - row) + Math.abs(c - col));

			}
		}
		return distance;
	}

	/**
	 * Checks if coordinate is out of field bounds
	 */
	private boolean outOfField(int r, int c) {
		if (r < 0 || r >= height || c < 0 || c >= width)
			return true;
		return false;
	}

}
