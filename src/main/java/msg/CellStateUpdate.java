package msg;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CellStateUpdate implements Serializable {
	public CellStateUpdate(boolean cellAlive, long cellIteration) {
		setIteration(cellIteration);
		setSet(cellAlive);
	}
	public long getIteration() {
		return iteration;
	}
	public void setIteration(long iteration) {
		this.iteration = iteration;
	}
	public boolean isSet() {
		return isSet;
	}
	public void setSet(boolean isSet) {
		this.isSet = isSet;
	}
	private long iteration;
	private boolean isSet;
	@Override
	public String toString() {
		return "CellStateUpdate [it=" + iteration + ", alive=" + isSet+ "]";
	}
	
}
