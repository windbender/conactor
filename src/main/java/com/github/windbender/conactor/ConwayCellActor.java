package com.github.windbender.conactor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msg.AddListenerMsg;
import msg.CellStateUpdate;
import msg.NeighborRegistration;
import msg.SendStateToNeighbors;
import msg.SendYourState;
import msg.SetState;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ConwayCellActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	Map<ActorRef, CellStateUpdate> latestNeighborUpdate = new HashMap<ActorRef,CellStateUpdate>();
	Map<Direction,ActorRef> neighbors = new HashMap<Direction,ActorRef>();
		
	private boolean cellAlive = false;
	private long cellIteration =0;
	
	private List<ActorRef> listeners = new ArrayList<ActorRef>();
	private int msgCount =0;
	
	

	public void onReceive(Object message) throws Exception {
		//if(this.getSelf().path().toString().contains("g1-1")) {
		//	log.info("received "+message+" from " +this.getSender());
		//}
		if(message instanceof SendStateToNeighbors) {
			sendUpdate();
		} else  if (message instanceof CellStateUpdate) {
			CellStateUpdate csu = (CellStateUpdate) message;
			if( cellIteration == csu.getIteration() ) {
				// valid update
				latestNeighborUpdate.put(this.getSender(), csu);
				msgCount++;
				if(msgCount ==8) {
					doUpdate();
				}
			} else {
				//log.error("invalid update! " +csu+" was expecting iteration "+(cellIteration)+" will resend");
				this.getSelf().tell(message,this.getSender());
			}
		} else if (message instanceof NeighborRegistration ) {
			NeighborRegistration nr = (NeighborRegistration) message;
			neighbors.put(nr.getiAmToYour(), nr.getWho());
		} else if (message instanceof SendYourState ) {			
			getSender().tell(new Boolean(this.cellAlive), getSelf());
		} else if (message instanceof SetState ) {			
			SetState ss = (SetState) message;
			this.cellAlive = ss.getNewState();
		} else if (message instanceof AddListenerMsg ) {			
			AddListenerMsg alm = (AddListenerMsg) message;
			listeners.add(alm.getStateReporter());
		} else {
		      unhandled(message);
		}
	}

	private void doUpdate() {
		int count =0;
		for(ActorRef neighbor: neighbors.values()) {
			CellStateUpdate csu = latestNeighborUpdate.get(neighbor);
			if(csu == null) return;
			if(csu.isSet()) count++;
		}

		boolean newCellAlive = cellAlive;
		if(cellAlive) {
			// alive
			if(count < 2) newCellAlive = false;
			if(count > 3) newCellAlive = false;
		} else {
			// dead
			if(count == 3) 
				newCellAlive = true;
		}

		//		if(cellAlive != newCellAlive) {
//			log.info("cell value changed from "+cellAlive+" to "+newCellAlive);
//		}
		cellAlive = newCellAlive;
		cellIteration++;
		msgCount =0;
		
		latestNeighborUpdate.clear();
		sendUpdate();
	}

	private void sendUpdate() {
		for(ActorRef listener: this.listeners) {
			listener.tell(new CellStateUpdate(this.cellAlive, this.cellIteration), this.getSelf());
		}
		for(ActorRef neighbor: neighbors.values()) {
			neighbor.tell(new CellStateUpdate(this.cellAlive, this.cellIteration),this.getSelf());
		}
	}
//	private void sendUpdate() {
//		for(Entry<ActorRef,CellStateUpdate> entry: latestNeighborUpdate.entrySet()) {
//			
//			entry.getKey().tell(new CellStateUpdate(this.cellAlive, this.cellIteration),this.getSelf());
//		}	
//	}

	@Override
	public String toString() {
		return "ConwayCellActor [neighbors=" + neighbors + ", cellAlive="
				+ cellAlive + ", cellIteration=" + cellIteration + "]";
	}
	
	
	
}
