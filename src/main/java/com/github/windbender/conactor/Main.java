package com.github.windbender.conactor;

import msg.AddListenerMsg;
import msg.NeighborRegistration;
import msg.PrintYourself;
import msg.SendStateToNeighbors;
import msg.SetState;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {

	public static int translateX(int max, int x, Direction dir) {
		if ((Direction.EAST.equals(dir)) || (Direction.SOUTHEAST.equals(dir))
				|| (Direction.NORTHEAST.equals(dir))) {
			x = x + 1;
			if (x >= max)
				x = x - max;
			return x;
		} else if ((Direction.WEST.equals(dir))
				|| (Direction.SOUTHWEST.equals(dir))
				|| (Direction.NORTHWEST.equals(dir))) {
			x = x - 1;
			if (x < 0)
				x = x + max;
			return x;
		} else {
			return x;
		}
	}

	public static int translateY(int max, int y, Direction dir) {
		if ((Direction.NORTH.equals(dir)) || (Direction.NORTHWEST.equals(dir))
				|| (Direction.NORTHEAST.equals(dir))) {
			y = y + 1;
			if (y >= max)
				y = y - max;
			return y;
		} else if ((Direction.SOUTH.equals(dir))
				|| (Direction.SOUTHWEST.equals(dir))
				|| (Direction.SOUTHEAST.equals(dir))) {
			y = y - 1;
			if (y < 0)
				y = y + max;
			return y;
		} else {
			return y;
		}
	}

	public static int size = 22;
	public static int maxx = size;
	public static int maxy = size;

	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("ConwaySystem");
		ActorRef stateReporter = system.actorOf(new Props(
				StateReportActor.class), "stateReporter");

		// hook the cells to the state reporter as a listener
		ActorRef[][] grid = new ActorRef[maxx][maxy];
		for (int x = 0; x < maxx; x++) {
			for (int y = 0; y < maxy; y++) {
				ActorRef e = system.actorOf(new Props(ConwayCellActor.class),
						"g" + x + "-" + y + "");
				grid[x][y] = e;
				e.tell(new AddListenerMsg(stateReporter), null);
				stateReporter.tell(new CellLocation(e, x, y), null);
			}
		}

		// now setup the neighbors between the cells
		for (int x = 0; x < maxx; x++) {
			for (int y = 0; y < maxy; y++) {
				ActorRef target = grid[x][y];
				for (Direction dir : Direction.values()) {
					int nx = translateX(maxx, x, dir);
					int ny = translateY(maxy, y, dir);
					target.tell(new NeighborRegistration(grid[nx][ny], dir),
							null);

				}
			}
		}

		// now setup a vertical /horizontal oscilator
		int middle = size / 2;
		for (int y = 0; y < 3; y++) {
			int ny = middle + y - 1;
			int nx = middle;
			grid[nx][ny].tell(new SetState(true), null);

		}
		// no set a random number of cells
		double threshold = 0.15;
		for (int x = 0; x < maxx; x++) {
			for (int y = 0; y < maxy; y++) {
				double d = Math.random();
				if (d < threshold) {
					ActorRef target = grid[x][y];
					target.tell(new SetState(true), null);
				}
			}
		}

		// and start the simulation, but telling the cells to send their current
		// state.
		for (int x = 0; x < maxx; x++) {
			for (int y = 0; y < maxy; y++) {
				ActorRef target = grid[x][y];
				target.tell(new SendStateToNeighbors(), null);
			}
		}

		// print two completed states and continue
		stateReporter.tell(new PrintYourself(2, PrintYourself.And.CONTINUE),
				null);

		// wait 5 seconds
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}

		// print 5 completed states and terminate the overall system.
		stateReporter.tell(new PrintYourself(5, PrintYourself.And.DIE), null);

	}

}
