package com.github.windbender.conactor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import msg.CellStateUpdate;
import msg.PrintYourself;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class StateReportActor extends UntypedActor {
	
	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private Map<Long,IterStore> map = new HashMap<Long,IterStore>();
	private Map<ActorRef,Location> locMap = new HashMap<ActorRef,Location>();
	
	int reportState = 0;
	private boolean andDie;
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof CellStateUpdate) {
			CellStateUpdate csu = (CellStateUpdate) msg;
			Long iterKey = new Long(csu.getIteration());
			IterStore is = map.get(iterKey);
			if(is == null) {
				is = new IterStore();
				map.put(iterKey,is);
			}

			is.addState(this.getSender(),csu);
			if(is.getMap().size() >= locMap.size()) {
				reportState(iterKey);
			}
		} else if(msg instanceof PrintYourself) {
			reportState = ((PrintYourself)msg).getNumber();
			if(((PrintYourself)msg).getAndDie().equals(PrintYourself.And.DIE) ) {
				andDie = true;
			} else {
				andDie = false;
			}
			
		} else if(msg instanceof CellLocation) {
		
			CellLocation cl = (CellLocation) msg;
			Location l = new Location();
			l.x = cl.getX();
			l.y = cl.getY();
			locMap.put(cl.getE(), l);
		} else {
		      unhandled(msg);
		}
	}

	private void reportState(Long iterKey) {
		Boolean[][] state = new Boolean[Main.maxx][Main.maxy];
		IterStore is = map.get(iterKey);
		
		for(Entry<ActorRef,CellStateUpdate> entry : is.getMap().entrySet()) {
			Location l = locMap.get(entry.getKey());
			state[l.x][l.y] = entry.getValue().isSet();
		}
		if(reportState >0) {
			printState(iterKey, state);
			reportState --;
			if(andDie && (reportState <=0)) {
				this.getContext().system().shutdown();

			}
		}
		map.remove(iterKey);
	}

	private void printState(Long iterKey,Boolean[][] state) {
			System.out.println("------------- for iteration "+iterKey);
			for(int x=0; x< Main.maxx; x++) {
	    		for(int y=0; y < Main.maxy; y++) {
	    			Boolean st = state[x][y];
	    			if(st) {
						System.out.print("X");
					} else {
						System.out.print(".");
					}
	    		}
	    		System.out.println();
	    	}
	}

}
