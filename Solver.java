import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

public class Solver {
	private static int max_node;
	private Set<String> memory;

	/**
	 * This constructor is kept private to prevent instantiation from outside classes.
	 */
	private Solver() {
		// intentionally left blank.
	}

	/**
	 * This method solves the puzzle from its current state using A-star search using heuristic h1.
	 * Specifically, h1 is the number of misplaced tiles.
	 * 
	 * @param board The initial board.
	 * @param ancestors The list of parents on that board.
	 * 
	 * @return The number of moves taken for this search.
	 * @throws Exception 
	 */
	void astar_h1(Board board) throws Exception {
		System.out.println("STARTING A* WITH H1");
		memory = new HashSet<String>();
		memory.add(board.get_state_arr());
		List<Board> possible_successors = new LinkedList<Board>();
		possible_successors.add(board);
		int total_nodes = 1;

		long start = System.nanoTime();
		while (!board.isGoal()) {
			List<String> allowed_moves = board.find_allowed_moves();
			List<Board> new_state_list = new LinkedList<Board>();
			for (String move : allowed_moves) {
				// copy board into new temporary
				Board temp = board.move(move);
				if (!memory.contains(temp.get_state_arr())) {
					temp.find_h1();
					temp.set_f();
					new_state_list.add(temp);
					memory.add(temp.get_state_arr());
					total_nodes++;
					if (total_nodes > max_node) {
						System.out.println("NODE NUMBER OVERFLOW.");
						throw new Exception();
					}
				}
			}
			for (Board child : new_state_list) {
				possible_successors.add(child);
			}
			possible_successors.sort(new SortByFn());
			board = possible_successors.get(0);
			possible_successors.remove(0);
		}
		long end = System.nanoTime();
		System.out.println("DONE WITH A* H1");
		System.out.println("NUMBER OF NODES: " + total_nodes);
		System.out.println("TIME TAKEN: " + (end - start) + " NS");
		System.out.println("SIZE: " + board.get_path().size() + ", STEPS: " + board.get_path().toString());
	}

	/**
	 * This method solves the puzzle from its current state using A-star search using heuristic h2.
	 * Specifically, h2 is the sum of the distances of the tiles from their goal positions, known as the Manhattan distance.
	 * 
	 * @param board The initial board.
	 * @param ancestors The list of parents on that board.
	 * 
	 * @return The number of moves taken for this search.
	 * @throws Exception 
	 */
	void astar_h2(Board board) throws Exception {
		System.out.println("STARTING A* WITH H2");
		memory = new HashSet<String>();
		memory.add(board.get_state_arr());
		List<Board> possible_successors = new LinkedList<Board>();
		possible_successors.add(board);
		int total_nodes = 1;

		long start = System.nanoTime();
		while (!board.isGoal() && total_nodes <= max_node) {
			List<String> allowed_moves = board.find_allowed_moves();
			List<Board> new_state_list = new LinkedList<Board>();
			for (String move : allowed_moves) {
				// copy board into new temporary
				Board temp = board.move(move);
				if (!memory.contains(temp.get_state_arr())) {
					temp.find_h2();
					temp.set_f();
					new_state_list.add(temp);
					memory.add(temp.get_state_arr());
					total_nodes++;
					if (total_nodes > max_node) {
						System.out.println("NODE NUMBER OVERFLOW.");
						throw new Exception();
					}
				}
			}
			for (Board child : new_state_list) {
				possible_successors.add(child);
			}
			possible_successors.sort(new SortByFn());
			board = possible_successors.get(0);
			possible_successors.remove(0);
		}
		long end = System.nanoTime();
		System.out.println("DONE WITH A* H2");
		if (total_nodes > max_node) {
			System.out.println("NODE NUMBER OVERFLOW.");
			throw new Exception();
		} else {
			System.out.println("NUMBER OF NODES: " + total_nodes);
			System.out.println("TIME TAKEN: " + (end - start) + " NS");
			System.out.println("SIZE: " + board.get_path().size() + ", STEPS: " + board.get_path().toString());
		}
	}

	/**
	 * This method solves the puzzle from its current state by adap8ng local beam search with k states.
	 * 
	 * @param k The number states allowed for local beam search.
	 * @throws Exception 
	 */
	void beam(Board board, int k) throws Exception {
		System.out.println("STARTING LOCAL BEAM WITH K=" + k);
		memory = new HashSet<String>();
		memory.add(board.get_state_arr());
		List<Board> successors = new LinkedList<Board>();
		successors.add(board);
		int total_nodes = 1;
		long start = System.nanoTime();
		outer:
			while (total_nodes <= max_node) {
				List<Board> childs = new LinkedList<Board>();
				List<Board> old_successors = new LinkedList<Board>();
				for (Board prev : successors) {
					old_successors.add(prev);
				}
				for (Board k_board : old_successors) {
					List<String> allowed_moves = k_board.find_allowed_moves();
					for (String move : allowed_moves) {
						Board temp = k_board.move(move);
						if (temp.isGoal()) {
							board = temp;
							break outer;
						}
						if (!memory.contains(temp.get_state_arr())) {
							temp.find_h2();
							temp.set_f();
							childs.add(temp);
							memory.add(temp.get_state_arr());
							total_nodes++;
							if (total_nodes > max_node) {
								System.out.println("NODE NUMBER OVERFLOW.");
								throw new Exception();
							}
						}
					}
				}
				childs.sort(new SortByFn());
				if (childs.size() > k) {
					childs = childs.subList(0, k + 1);
				}
				successors.clear();
				for (Board child : childs) {
					successors.add(child);
				}
			}
		long end = System.nanoTime();
		System.out.println("DONE WITH LOCAL BEAM WITH K=" + k);
		if (total_nodes > max_node) {
			System.out.println("NODE NUMBER OVERFLOW.");
			throw new Exception();
		} else {
			System.out.println("NUMBER OF NODES: " + total_nodes);
			System.out.println("TIME TAKEN: " + (end - start) + " NS");
			System.out.println("SIZE: " + board.get_path().size() + ", STEPS: " + board.get_path().toString());
		}
	}

	/**
	 * This method sets the maximum number of nodes to be considered during a search.
	 * 
	 * @param n The maximum number of nodes.
	 */
	void max_nodes(int n) {
		max_node = n;
		System.out.println("MAX NODE SET TO " + n);
	}

	/**
	 * Runs the experiment for part 3 of write up.
	 */
	@SuppressWarnings("unused")
	private void run_experiment() {
		try {
			FileWriter writer = new FileWriter("output_statistics.txt");
			List<Integer> max_node_list = new ArrayList<Integer>();
			max_node_list.add(500);
			max_node_list.add(1000);
			max_node_list.add(3000);
			max_node_list.add(5000);
			max_node_list.add(7000);
			max_node_list.add(10000);
			max_node_list.add(15000);
			for (Integer max_nodes : max_node_list) {
				int h1_failed = 0;
				int h2_failed = 0;
				int b_failed = 0;
				for (int i = 0; i < 100; i++) {
					// randomize with 200
					Board board = new Board("b12 345 678");
					System.out.print("Given: ");
					board.printState();
					board = board.randomizeState(5000);
					System.out.print("Randomized: ");
					board.printState();
					System.out.println();
					try {
						// A* with h1
						Solver solver = new Solver();
						solver.max_nodes(max_nodes);
						solver.astar_h1(new Board(board.get_state_arr()));
					} catch (Exception e) {
						h1_failed++;
					}
					System.out.println();
					// A* with h2
					try {
						Solver solver = new Solver();
						solver.max_nodes(max_nodes);
						solver.astar_h2(new Board(board.get_state_arr()));
					} catch (Exception e) {
						h2_failed++;
					}	
					System.out.println();
					// local beam with k=5
					try {
						Solver solver = new Solver();
						solver.max_nodes(max_nodes);
						solver.beam(new Board(board.get_state_arr()), 5);
					} catch (Exception e) {
						b_failed++;
					}
					System.out.println();
				}
				writer.write("USING MAX NODES = " + max_nodes);
				writer.write(System.getProperty("line.separator"));
				writer.write("H1 FAILED " + h1_failed + ", FAILURE RATE = " + h1_failed + "%");
				writer.write(System.getProperty("line.separator"));
				writer.write("H2 FAILED " + h2_failed + ", FAILURE RATE = " + h2_failed + "%");
				writer.write(System.getProperty("line.separator"));
				writer.write("BEAM FAILED " + b_failed + ", FAILURE RATE = " + b_failed + "%");
				writer.write(System.getProperty("line.separator"));
				writer.write(System.getProperty("line.separator"));
			}
			writer.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			System.out.println(e);
		}
	}

	public static void main(String[] args) throws Exception {
		Solver solver = new Solver();
		solver.run_experiment();
		try {
			//keeping goal state here to prevent errors in instantiation, will setState from file.
			Solver solve = new Solver();
			Board board = new Board("b12 345 678");
			BufferedReader reader = new BufferedReader(new FileReader("input.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#")) {
					// lines with # are comments
				} else {
					System.out.println("READING command: " + line);
					String[] inputs = line.split(" ");
					switch(inputs[0]) {
					case "maxNodes":
						String value = inputs[1];
						value = value.strip();
						int nodes = Integer.parseInt(value);
						System.out.println("\t" + "SETTING MAX NODE = " + nodes);
						solve.max_nodes(nodes);
						break;
					case "move":
						String dir = inputs[1];
						dir = dir.strip();
						try {
							System.out.println("\t" + "MOVING BLANK TILE " + dir.toUpperCase());
							board = board.move(dir);
						} catch (Exception e) {
							System.out.println(e);
						}
						break;
					case "setState":
						String state = "";
						try {
							String row_one = inputs[1];
							state += row_one;
							String row_two = inputs[2];
							state += row_two;
							String row_three = inputs[3];
							state += row_three;
							state = state.strip();
						} catch (Exception e) {
							System.out.println("\t" + "ERROR READING SETSTATE COMMAND");
						}
						if (state.isBlank() || state.equals("b12345678") || inputs.length > 4) {
							System.out.println("\t" + "CAN NOT SETSTATE = " + state);
							System.exit(0);
						} else {
							String[][] state_array = new String[3][3];
							int index = 0;
							for (int i = 0; i < 3; i++) {
								for (int j = 0; j < 3; j++) {
									while (state.charAt(index) == ' ') {
										index++;
									}
									state_array[i][j] = Character.toString(state.charAt(index));
									index++;
								}
							}
							System.out.println("\t" + "SETTING STATE = " + Arrays.deepToString(state_array));
							board = new Board(board.toString(state_array));
							board.setState(state_array);
						}
						break;
					case "printState":
						System.out.print("\t" + "PRINTING STATE = ");
						board.printState();
						break;
					case "randomizeState":
						String n_moves = inputs[1];
						n_moves = n_moves.strip();
						int n = Integer.parseInt(n_moves);
						System.out.println("\t" + "RANDOMIZING STATE WITH N = " + n);
						board = board.randomizeState(n);
						break;
					case "solve":
						String method = inputs[1];
						if ("A-star".equals(method)) {
							String heu = inputs[2];
							if (heu.contains("h1")) {
								try {
									solve.astar_h1(board);
								} catch (Exception e) {
									System.out.println("\t" + e);
								}
							} else if (heu.contains("h2")) {
								try {
									solve.astar_h2(board);
								} catch (Exception e) {
									System.out.println("\t" + e);
								}
							}
						} else if ("beam".equals(method)) {
							String k_size = inputs[2];
							k_size = k_size.strip();
							int k = Integer.parseInt(k_size);
							try {
								solve.beam(board, k);
							} catch (Exception e) {
								System.out.println("\t" + e);
							}
						}
						break;
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("an error occurred.");
			System.out.println(e);
		}
	}
}