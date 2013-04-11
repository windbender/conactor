package msg;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PrintYourself implements Serializable {
	
	public enum And {
		DIE,
		CONTINUE
	};
	int number;
	And andDie;
	public PrintYourself(int x, And andDie) {
		super();
		this.number = x;
		this.andDie = andDie;
	}
	public int getNumber() {
		return number;
	}
	public And getAndDie() {
		return andDie;
	}

}
