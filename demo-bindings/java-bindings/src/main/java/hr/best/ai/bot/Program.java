package hr.best.ai.bot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import hr.best.ai.utility.Cell;
import hr.best.ai.utility.State;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Program {

	public static void main(String[] args) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in, StandardCharsets.UTF_8))) {
            while (true) {
                State state = new State(reader.readLine());
                List<Cell> output = algorithm(state);
                send(output);
            }
        }
    }

	/**
	 * Handles converting list to json and sending
	 * 
	 * @param list
	 *            list of cells
	 */
	private static void send(List<Cell> list) {

		JsonObject output = new JsonObject();
		JsonArray cells = new JsonArray();
		for (int i = 0; i < list.size(); i++)
			cells.add(list.get(i).toJSON());

		output.add("cells", cells);
		System.out.println(output.toString());
	}

	/**
	 * Most of the player code goes here
	 * @param state
	 * @return
	 */
	private static List<Cell> algorithm(State state) {
		List<Cell> legal = state.getLegalCells();
		Collections.shuffle(legal);
		return legal.subList(0,
				Math.min(state.getCellsRemaining(), legal.size()));
	}
}
