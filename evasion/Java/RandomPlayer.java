import java.util.Random;

public class RandomPlayer
{
	long moveCount;
	Random randGen;
	EvasionModel model;
	int N;
	int W;

	public RandomPlayer(EvasionModel paramEvasionModel, int paramInt1, int paramInt2)
	{
		this.randGen = new Random(new Random().nextInt());
		this.model = paramEvasionModel;
		this.moveCount = 0L;
		this.N = paramInt1;
		this.W = paramInt2;
	}
}
