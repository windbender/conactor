package msg;

import java.io.Serializable;

import akka.actor.ActorRef;

@SuppressWarnings("serial")
public class AddListenerMsg implements Serializable {

	ActorRef stateReporter;
	
	public AddListenerMsg(ActorRef stateReporter) {
		this.stateReporter = stateReporter;
	}

	public ActorRef getStateReporter() {
		return stateReporter;
	}

}
