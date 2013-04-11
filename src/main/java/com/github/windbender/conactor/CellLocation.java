package com.github.windbender.conactor;

import java.io.Serializable;

import akka.actor.ActorRef;

@SuppressWarnings("serial")
public class CellLocation implements Serializable {

	ActorRef e;
	int x;
	int y;
	public CellLocation(ActorRef e, int x, int y) {
		this.e=e;
		this.x=x;
		this.y=y;
	}
	public ActorRef getE() {
		return e;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	

}
