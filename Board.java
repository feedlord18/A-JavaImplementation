import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Board {
	private static final char blank_tile = 'b';
	private static final int dim = 3;
	private static final String goal = "b12 345 678";
	private static final Character[][] goal_arr = { {'b', '1', '2'}, {'3', '4', '5'}, {'6', '7', '8'} };
	public static final Random rand = new Random(12345);
	
	private String[][] curr_state;
	private String curr_state_str;
	private int f = 0;
	private int g = 0;
	private int h = 0;
	private List<String> path;


	/**
	 * This constructor takes in a string representation of the state and creates a Board object.
	 * 
	 * @param state_str The state in string format.
	 */
	public Board(String state_str) {
		if (state_str.isBlank() || state_str.length() < 9) {
			throw new IllegalArgumentException("THE INPUT CAN NOT BE EMPTY OR LESS THAN THE TILES");
		}
		this.curr_state_str = state_str;
		this.curr_state = set_state_arr(state_str);
		find_h2();
		path = new LinkedList<String>();
	}
	
	/**
	 * This method gets the ancestor path of the state.
	 * 
	 * @return A list of ancestors.
	 */
	List<String> get_path() {
		return path;
	}
	
	/**
	 * This method sets the ancestor path of the state.
	 */
	void set_path(List<String> old_path) {
		for (String x : old_path) {
			path.add(x);
		}
	}
	
	/**
	 * This method adds a direction to the ancestor path of the state.
	 */
	void add_dir(String dir) {
		path.add(dir);
	}

	/**
	 * This method sets the state array according to the input string.
	 * 
	 * @param state_str The input state string.
	 * 
	 * @return The state as a two-dimensional array.
	 */
	private String[][] set_state_arr(String state_str) {
		String[][] temp = new String[dim][dim];
		int ctr = 0;
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				while (state_str.charAt(ctr) == ' ') {
					ctr++;
				}
				temp[i][j] = Character.toString(state_str.charAt(ctr));
				ctr++;
			}
		}
		return temp;
	}
	
	/**
	 * This method gets the state string.
	 * 
	 * @return The string of the current state.
	 */
	String get_state_arr() {
		return curr_state_str;
	}

	/**
	 * sets g(n) given the cost input
	 * 
	 * @param cost The cost given
	 */
	void set_g(int cost) {
		this.g = cost;
	}

	/**
	 * This method gets the state's g(n).
	 * 
	 * @return The current state's g(n).
	 */
	int get_g() {
		return this.g;
	}

	/**
	 * sets h(n) given the cost input
	 * 
	 * @param cost The cost given
	 */
	void set_h(int cost) {
		this.h = cost;
	}
	
	/**
	 * This method gets the state's h(n).
	 * 
	 * @return The current state's h(n).
	 */
	int get_h() {
		return this.h;
	}

	/**
	 * The function cost is: f(n) = g(n) + h(n)
	 */
	void set_f() {
		this.f = this.g + this.h;
	}
	
	/**
	 * This method gets the state's f(n).
	 * 
	 * @return The current state's f(n).
	 */
	int get_f() {
		return this.f;
	}

	/**
	 * This method calculates the heuristic cost based on h1 and sets it accordingly.
	 * This heuristic is based on the number of misplaced tiles.
	 */
	void find_h1() {
		int h1 = 0;
		String curr_state_str = toString(this.curr_state);
		for (int i = 0; i < curr_state_str.length(); i++) {
			if (curr_state_str.charAt(i) != goal.charAt(i)) {
				h1++;
			}
		}
		set_h(h1);
	}

	/**
	 * This method calculates the heuristic cost based on h2 and sets it accordingly.
	 * This heuristic is the sum of the Manhattan distance of each tile.
	 */
	void find_h2() {
		int h2 = 0;
		for (int i = 0; i < this.curr_state.length; i++) {
			for (int j = 0; j < this.curr_state[i].length; j++) {
				h2 += find_manhattan(this.curr_state[i][j].charAt(0), new int[] {i, j});
			}
		}
		set_h(h2);
	}

	/**
	 * This method calculates the manhattan distance for a single given tile, and it's coordinates in the current board.
	 * 
	 * @param tile The given tile from set {b, 1, 2, 3, 4, 5, 6, 7, 8}.
	 * @param coordinates The coordinates of the given tile.
	 * 
	 * @return Manhattan distance
	 */
	private int find_manhattan(char tile, int[] coordinates) {
		List<List<Character>> goal_list = twoDArrToList(goal_arr);
		int[] goal_coordinate = new int[2];
		// error checking after iterations
		goal_coordinate[0] = -1;
		goal_coordinate[1] = -1;
		// finds the goal coordinate of the given tile
		for (int x = 0; x < goal_list.size(); x++) {
			for (int y = 0; y < goal_list.get(x).size(); y++) {
				if (goal_list.get(x).get(y) == tile) {
					goal_coordinate[0] = x;
					goal_coordinate[1] = y;
				}
			}
		}
		if (goal_coordinate[0] == -1 || goal_coordinate[1] == -1) {
			System.out.println("ERROR FOR SEARCH GOAL COORDINATE.");
			throw new NullPointerException("pointer is -1.");
		}
		int manhattan = 0;
		manhattan += Math.abs(coordinates[0] - goal_coordinate[0]);
		manhattan += Math.abs(coordinates[1] - goal_coordinate[1]);
		return manhattan;
	}

	/**
	 * This method sets the current state of the board, given the input state.
	 * 
	 * @param state The given state.
	 */
	void setState(String[][] state) {
		this.curr_state_str = toString(state);
		this.curr_state = state;
	}

	/**
	 * This method makes a copy of the current state and moves the blank tile into the specified direction in the new state.
	 * This creates a child node and assigns the new state, increases g(n) and sets the parent to this.
	 * 
	 * @param direction The specified direction
	 * 
	 * @return The child node.
	 */
	Board move(String direction) {
		List<String> moves = this.find_allowed_moves();
		if (moves.contains(direction)) {
			// find blank location
			int[] blank_location = this.find_blank();
			// create new State
			String[][] new_state = new String[dim][dim];
			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					new_state[i][j] = curr_state[i][j];
				}
			}
			switch (direction) {
			case "up":
				new_state[blank_location[0]][blank_location[1]] = new_state[blank_location[0]-1][blank_location[1]];
				new_state[blank_location[0]-1][blank_location[1]] = Character.toString(blank_tile);
				break;
			case "down":
				new_state[blank_location[0]][blank_location[1]] = new_state[blank_location[0]+1][blank_location[1]];
				new_state[blank_location[0]+1][blank_location[1]] = Character.toString(blank_tile);
				break;
			case "left":
				new_state[blank_location[0]][blank_location[1]] = new_state[blank_location[0]][blank_location[1]-1];
				new_state[blank_location[0]][blank_location[1]-1] = Character.toString(blank_tile);
				break;
			case "right":
				new_state[blank_location[0]][blank_location[1]] = new_state[blank_location[0]][blank_location[1]+1];
				new_state[blank_location[0]][blank_location[1]+1] = Character.toString(blank_tile);
				break;
			}
			// create new node
			Board child = new Board(toString(new_state));
			child.setState(new_state);
			child.set_g(this.get_g() + 1);
			child.set_path(path);
			child.add_dir(direction);
			return child;
		} else {
			throw new IllegalArgumentException("INVALID DIRECTION");
		}
	}

	/**
	 * This method gets the possible moves in {up, down, left, right} given the blank tile location
	 * 
	 * @return List of possible moves.
	 */
	List<String> find_allowed_moves() {
		int[] blank_location = this.find_blank();
		List<String> allowed_moves = new ArrayList<String>();
		if (blank_location[0] != 0) {
			allowed_moves.add("up");
		}
		if (blank_location[0] != dim - 1) {
			allowed_moves.add("down");
		}
		if (blank_location[1] != 0) {
			allowed_moves.add("left");
		}
		if (blank_location[1] != dim - 1) {
			allowed_moves.add("right");
		}
		return allowed_moves;
	}

	/**
	 * This method finds the location of the blank tile, and throws exception if not found.
	 * 
	 * @return Location of the blank tile.
	 */
	int[] find_blank() {
		for (int i = 0; i < this.curr_state.length; i++) {
			for (int j = 0; j < this.curr_state[i].length; j++) {
				if (this.curr_state[i][j].charAt(0) == blank_tile) return new int[] {i, j};
			}
		}
		throw new NullPointerException("blank tile not found");
	}

	/**
	 * This method prints the current state as an two-dimensional array.
	 */
	void printState() {
		System.out.println(Arrays.deepToString(this.curr_state));
	}
	
	/**
	 * This method prints the current state as an two-dimensional array.
	 */
	void printStateArr() {
		System.out.println(this.curr_state_str);
	}

	/**
	 * This method checks if the goal state has been reached and returns the decision.
	 * 
	 * @return Either the goal has been reached or not.
	 */
	boolean isGoal() {
		if (toString(this.curr_state).equals(goal)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method adopts the current board and makes n random moves from the goal state.
	 * 
	 * @param The number of random moves to scramble the puzzle.
	 */
	Board randomizeState(int n) {
		Board board = this;
		int iteration = 0;
		while (iteration < n) {
			try {
				List<String> moves = board.find_allowed_moves();
				String move = moves.get(rand.nextInt(moves.size()));
				board = board.move(move);
				iteration++;
			} catch (Exception e) {
				System.out.println("HAD INVALID DIRECTION, RETRYING");
			}
		}
		return board;
	}

	/**
	 * This method returns the state as a String.
	 * 
	 * @param state The given state.
	 * 
	 * @return The state in String format.
	 */
	String toString(String[][] state) {
		String str = "";
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				str += state[i][j];
			}
			str += " ";
		}
		str = str.stripTrailing();
		return str;
	}
	
	/**
	 * This method compares the state value of two boards.
	 * 
	 * @param board The other board to be compared to.
	 * 
	 * @return A boolean of equality.
	 */
	boolean equal(Board board) {
		if (this.get_state_arr().equals(board.get_state_arr())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Utility method to convert two-dimensional arrays to List<List<T>>.
	 * 
	 * @param arr The given two-dimensional array
	 * 
	 * @return The array as a list.
	 */
	private List<List<Character>> twoDArrToList(Character[][] arr) {
		List<List<Character>> list = new ArrayList<List<Character>>();
		for (Character[] array : arr) {
			list.add(Arrays.asList(array));
		}
		return list;
	}
}
