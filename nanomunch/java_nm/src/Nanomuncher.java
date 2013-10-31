package nanomunchers;

public class Nanomuncher {
	
	int startTime;
	
	String program;
	int programCounter;
	Node presentPosition;
	
	public Nanomuncher(String program, Node position, int programCounter) {
		this.program = program;
		this.presentPosition = position;
		this.programCounter = programCounter;
	}

}
