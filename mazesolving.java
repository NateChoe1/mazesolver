import java.lang.*;
import java.util.*;

public class mazesolving {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter in the maze dimensions");
		int w = sc.nextInt();
		int h = sc.nextInt();
		System.out.println("Enter in the maze to solve (the solution starts at the top left and ends at the bottom right, X = filled, ' ' = empty)");
		byte[][] maze = new byte[h][w];
		final byte EMPTY = 0;
		final byte FILLED = 1;
		final byte SOLUTION = 2;
		String[] mazeStrings = new String[h];
		sc.nextLine();
		HashSet<Integer> beenTo = new HashSet<>();
		for (int i = 0; i < h; i++) {
			String row = sc.nextLine();
			for (int j = 0; j < w; j++) {
				char c = row.charAt(j);
				switch (c) {
					case ' ': default:
						maze[i][j] = EMPTY;
						break;
					case 'X':
						maze[i][j] = FILLED;
						beenTo.add(i * 10000 + j);
						//I'm too lazy to make this work in the A* loop
						break;
				}
			}
		}
		sc.close();

		PriorityQueue<Path> pq = new PriorityQueue<>();
		pq.add(new Path(0, 0, w-1, h-1));
		while (pq.size() > 0) {
			Path p = pq.poll();
			if (p.getCurrentX() < 0 || p.getCurrentX() >= h ||
			    p.getCurrentY() < 0 || p.getCurrentY() >= w ||
			    beenTo.contains(p.getCurrentX() * 10000 + p.getCurrentY()))
				continue;
			if (beenTo.contains(p.getCurrentX() * 10000 + p.getCurrentY()))
				continue;
			beenTo.add(p.getCurrentX() * 10000 + p.getCurrentY());
			if (p.getCurrentX() == w-1 && p.getCurrentY() == h-1) {
				System.out.println("Solution found!");
				for (int[] solutionPoint: p.path)
					maze[solutionPoint[0]][solutionPoint[1]] = SOLUTION;

				for (int i = 0; i < h; i++) {
					for (int j = 0; j < w; j++) {
						byte c = maze[i][j];
						switch (c) {
							case EMPTY:
								System.out.print(" ");
								break;
							case FILLED:
								System.out.print("X");
								break;
							case SOLUTION:
								System.out.print("\033[30;47mO\033[0m");
								break;

						}
					}
					System.out.println();
				}
				return;
			}
			Path[] moves = new Path[4];
			int[][] offsets = new int[][] {
				{ 1,  0},
				{-1,  0},
				{ 0,  1},
				{ 0, -1}
			};
			for (int i = 0; i < offsets.length; i++) {
				Path clone = p.clonePath();
				clone.path.add(new int[] {
					p.getCurrentX() + offsets[i][0],
					p.getCurrentY() + offsets[i][1],
				});
				pq.add(clone);
			}
		}
		System.out.println("The maze doesn't seem to have a solution.");
	}

	static class Path implements Comparable<Path> {
		ArrayList<int[]> path = new ArrayList<>();
		int destinationX;
		int destinationY;
		Path(int x, int y, int dx, int dy) {
			path.add(new int[] {x, y});
			destinationX = dx;
			destinationY = dy;
			//This really looks like it would be calculus.
		}

		Path(int dx, int dy) {
			destinationX = dx;
			destinationY = dy;
		}

		Path clonePath() {
			Path newPath = new Path(destinationX, destinationY);
			newPath.path = (ArrayList) path.clone();
			return newPath;
		}

		int getCurrentX() {
			return path.get(path.size() - 1)[0];
		}

		int getCurrentY() {
			return path.get(path.size() - 1)[1];
		}

		double getPriority() {
			double priority = path.size();
			priority += Math.hypot(destinationX - getCurrentX(), destinationY - getCurrentY());
			return priority;
		}

		int[] currentPosition() {
			return path.get(path.size() - 1);
		}

		@Override
		public int compareTo(Path other) {
			int total = path.size();
			double diff = getPriority() - other.getPriority();
			if (diff == 0)
				return 0;
			if (diff < 0)
				return -1;
			return 1;
		}
	}
}
