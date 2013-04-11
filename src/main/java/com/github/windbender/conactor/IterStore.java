package com.github.windbender.conactor;

import java.util.HashMap;
import java.util.Map;

import msg.CellStateUpdate;
import akka.actor.ActorRef;

public class IterStore {

	Map<ActorRef,CellStateUpdate> map = new HashMap<ActorRef,CellStateUpdate>();
	
	public void addState(ActorRef sender, CellStateUpdate csu) {
		map.put(sender, csu);		
	}

	public Map<ActorRef, CellStateUpdate> getMap() {
		return map;
	}
	
	

}
