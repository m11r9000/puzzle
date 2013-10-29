import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Puzzle {

	final static int BLANK = 0;
	final static int BOARDWIDTH = 3;

	public Puzzle() {
	}

	private class Node implements Comparable<Node> {
		Node parent;
		String dir;
		int[][] board;
		float f, g, h;

		@Override
		public int compareTo(Node o) {
			// TODO Auto-generated method stub
			if (this.f > o.f)
				return 1;
			else if (this.f < o.f)
				return -1;
			return 0;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			Node curr = this;
			int numMoves = 0;
			LinkedList<Node> q = new LinkedList<Node>();
			// Reverse the order of nodes
			while (curr != null) {
				// curr = curr.parent;
				q.addLast(curr);
				curr = curr.parent;
				numMoves++;
			}
			// Print every node and the direction taken
			while (!q.isEmpty()) {
				Node temp = q.removeLast();
				if (temp.dir != null)
					sb.append(temp.dir);
				sb.append("\n");
				sb.append(printBoard(temp));
				sb.append("\n");
			}
			// Number of moves
			sb.append("Number of moves: " + numMoves);
			return sb.toString();
		}

		/**
		 * Used to print the layout of board in 3x3 grid
		 * 
		 * @param curr
		 * @return
		 */
		private String printBoard(Node curr) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < BOARDWIDTH; i++) {
				for (int j = 0; j < BOARDWIDTH; j++) {
					sb.append(curr.board[i][j]);
				}
				sb.append("\n");
			}

			return sb.toString();
		}
	}

	/**
	 * Method used for finding path to goal position from starting position.
	 * 
	 * Method uses A* search algorithm to find goal and the heuristic in place
	 * is the manhattan distance which is defined at
	 * 
	 * http://heuristicswiki.wikispaces.com/Manhattan+Distance
	 * 
	 * The A* algorithm was implemented with help from Wikipedia's article on
	 * said algorithm and from http://web.mit.edu/eranki/www/tutorials/search/
	 * 
	 * 
	 * @param start
	 * @param goal
	 * @return
	 */
	public Node aStar(int[][] start, int[][] goal) {
		Node n = new Node();
		n.board = start;
		n.f = 0;
		n.g = 0;
		n.h = cost(n.board, goal);
		n.f = n.g + n.h;
		LinkedList<Node> closedSet = new LinkedList<Node>(); // The set of nodes
																// already
																// evaluated.

		PriorityQueue<Node> openSet = new PriorityQueue<Node>(); // The set of
		// tentative nodes
		// to be evaluated,
		// initially
		// containing the
		// start node

		// add start node to open set
		openSet.offer(n);
		while (!openSet.isEmpty()) {
			// take node with lowest f score
			Node q = openSet.poll();
			// if same board layout, return q
			if (same(q.board, goal))
				return q;
			// add to closed set since finished evaluating after this iteration
			closedSet.add(q);
			// find the successors for this board
			LinkedList<Node> successors = successors(q);
			for (Node s : successors) {
				// if any of successors is goal layout, return that successor
				if (same(s.board, goal)) {
					return s;
				}
				// calculate tentative g-score and f-score
				float tentG = q.g + cost(q.board, s.board);
				float tentF = tentG + cost(s.board, goal);

				// if any of elements in closedSet are same as current successor
				// and has lower f-score than current successor, skip the
				// successor
				boolean closedSkip = false;
				for (Node c : closedSet) {
					if (same(s.board, c.board) && c.f <= tentF) {
						closedSkip = true;
						break;
					}
				}
				if (closedSkip)
					continue;
				boolean notInOpen = true;
				boolean smaller = true;

				for (Node o : openSet) {
					if (same(s.board, o.board)) {
						notInOpen = false;
						break;
					}
				}
				for (Node o : openSet) {
					if (s.f < o.f) {
						smaller = true;
						break;
					}
				}
				// if successor is smaller than any of the elements in openSet
				if (notInOpen || smaller) {
					// fix fields accordingly
					s.parent = q;
					s.g = tentG;
					s.f = tentF;
					// and if it is not existing in openSet, add it to OpenSet
					if (notInOpen)
						openSet.offer(s);

				}
			}
		}
		return null;
	}

	/**
	 * Check if the boards have the same layout according to position of values
	 * 
	 * @param board
	 * @param goal2
	 * @return
	 */
	private boolean same(int[][] board, int[][] goal2) {
		// TODO Auto-generated method stub
		for (int i = 0; i < BOARDWIDTH; i++) {
			for (int j = 0; j < BOARDWIDTH; j++) {
				if (board[i][j] != goal2[i][j]) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Generate successors for the current board layout given by node q
	 * 
	 * @param q
	 * @return
	 */
	private LinkedList<Node> successors(Node q) {
		// TODO Auto-generated method stub
		int x = 0;
		int y = 0;

		// find index of blank tile
		int[] indices = indexOf(q.board, BLANK);
		x = indices[0];
		y = indices[1];

		// find the successors for current state
		LinkedList<Node> successors = new LinkedList<Node>();
		// there are a maximum of four successors. check how many of them are
		// valid moves

		int[][] s_up = new int[BOARDWIDTH][BOARDWIDTH];
		int[][] s_down = new int[BOARDWIDTH][BOARDWIDTH];
		int[][] s_left = new int[BOARDWIDTH][BOARDWIDTH];
		int[][] s_right = new int[BOARDWIDTH][BOARDWIDTH];

		// initialise all four matrices
		for (int i = 0; i < BOARDWIDTH; i++) {
			for (int j = 0; j < BOARDWIDTH; j++) {
				s_up[i][j] = q.board[i][j];
			}
		}

		for (int i = 0; i < BOARDWIDTH; i++) {
			for (int j = 0; j < BOARDWIDTH; j++) {
				s_down[i][j] = q.board[i][j];
			}
		}

		for (int i = 0; i < BOARDWIDTH; i++) {
			for (int j = 0; j < BOARDWIDTH; j++) {
				s_left[i][j] = q.board[i][j];
			}
		}

		for (int i = 0; i < BOARDWIDTH; i++) {
			for (int j = 0; j < BOARDWIDTH; j++) {
				s_right[i][j] = q.board[i][j];
			}
		}

		// calculate valid moves

		// up
		int temp = 0;
		if (x > BLANK) {
			temp = s_up[x][y];
			s_up[x][y] = s_up[x - 1][y];
			s_up[x - 1][y] = temp;
			Node n = new Node();
			n.board = s_up;
			n.parent = q;
			n.dir = "UP";
			successors.add(n);
		}
		// down
		if (x < BOARDWIDTH - 1) {
			temp = s_down[x][y];
			s_down[x][y] = s_down[x + 1][y];
			s_down[x + 1][y] = temp;
			Node n = new Node();
			n.board = s_down;
			n.parent = q;
			n.dir = "DOWN";
			successors.add(n);
		}
		// left
		if (y > BLANK) {
			temp = s_left[x][y];
			s_left[x][y] = s_left[x][y - 1];
			s_left[x][y - 1] = temp;
			Node n = new Node();
			n.board = s_left;
			n.parent = q;
			n.dir = "LEFT";
			successors.add(n);
		}
		// right
		if (y < BOARDWIDTH - 1) {
			temp = s_right[x][y];
			s_right[x][y] = s_right[x][y + 1];
			s_right[x][y + 1] = temp;
			Node n = new Node();
			n.board = s_right;
			n.parent = q;
			n.dir = "RIGHT";
			successors.add(n);
		}
		return successors;
	}

	/**
	 * Cost is based on manhattan distance between the two boards fed as
	 * parameters to method
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	private int cost(int[][] from, int[][] to) {
		int sum = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (from[i][j] == BLANK)
					continue;
				int[] indexFrom = indexOf(from, from[i][j]);
				int[] indexTo = indexOf(to, from[i][j]);
				sum += Math.abs(indexFrom[0] - indexTo[0])
						+ Math.abs(indexFrom[1] - indexTo[1]);
			}
		}
		return sum;
	}

	/**
	 * Calculate the index of the given tile in given board
	 * 
	 * @param board
	 * @param tile
	 * @return
	 */
	private int[] indexOf(int[][] board, int tile) {
		int[] index = new int[2];
		for (int i = 0; i < BOARDWIDTH; i++) {
			for (int j = 0; j < BOARDWIDTH; j++) {
				if (board[i][j] == tile) {
					index[0] = i;
					index[1] = j;
					return index;
				}
			}
		}
		return null;
	}

	/**
	 * Calculate whether board is solvable or not
	 * 
	 * The conditions that are to be checked come from
	 * 
	 * http://www.cs.bham.ac.uk/~mdr/teaching/modules04/java2/TilesSolvability.
	 * html
	 * 
	 * @param puzzle
	 * @return
	 */
	private boolean solvable(int[][] puzzle) {
		int[] oneDim = new int[BOARDWIDTH * BOARDWIDTH];
		int k = 0;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				oneDim[k] = puzzle[i][j];
				k++;
			}
		}

		// count number of inversions in board
		int inversions = 0;
		for (int i = 0; i < oneDim.length; i++) {
			for (int j = i; j < oneDim.length; j++) {
				if (oneDim[i] == 0)
					break;
				if (oneDim[i] < oneDim[j])
					inversions++;
			}
		}

		// 1. if grid width odd -> inversions must be even
		if (puzzle[0].length % 2 != 0)
			return (inversions % 2 == 0);

		// 2. if grid width even && blank is on even row
		// counting from bottom -> inversions must be odd

		// 3. if grid width even && blank is on odd row
		// counting from bottom -> inversions must be even
		return true;
	}

	public static void main(String[] args) {
		Puzzle p = new Puzzle();

		// Simply change to the layout you want.
		// Note that it is important that 0 - 8 occurr exactly once
		// in each board. Some layouts are unsolvable, but you will be
		// notified if that is the case.
		int[][] puzzle = { { 8, 0, 6 }, { 5, 4, 7 }, { 2, 3, 1 } };
		int[][] goal = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 } };

		// check if solvable
		if (!p.solvable(puzzle)) {
			System.err.println("Board is not solvable!");
		} else {
			System.out.println(p.aStar(puzzle, goal));
		}
	}

}
