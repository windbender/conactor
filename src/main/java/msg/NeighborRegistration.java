package msg;

import java.io.Serializable;

import akka.actor.ActorRef;

import com.github.windbender.conactor.Direction;

@SuppressWarnings("serial")
public class NeighborRegistration implements Serializable {

	ActorRef who;
	public NeighborRegistration(ActorRef who, Direction iAmToYour) {
		super();
		this.who = who;
		this.iAmToYour = iAmToYour;
	}

	Direction iAmToYour;
	
	public Direction getiAmToYour() {
		return iAmToYour;
	}

	public ActorRef getWho() {
		return who;
	}

}
