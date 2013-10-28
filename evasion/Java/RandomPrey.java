import java.awt.geom.Point2D;
import java.io.IOException;
import javax.swing.SwingWorker;

public class RandomPrey
	extends RandomPlayer
	implements EvasionListener
{
	public RandomPrey(EvasionModel paramEvasionModel, int paramInt1, int paramInt2)
	{
		super(paramEvasionModel, paramInt1, paramInt2);
	}

	public void hunter_moved(HunterMove paramHunterMove, String paramString)
		throws IOException
	{
		this.moveCount += 1L;
		if (this.moveCount % 2L == 0L)
		{
			final PreyMoves localPreyMoves = PreyMoves.values()[this.randGen.nextInt(PreyMoves.values().length)];
			new SwingWorker()
			{
				protected Integer doInBackground()
					throws Exception
				{
					RandomPrey.this.model.preyMove(localPreyMoves);
					return Integer.valueOf(0);
				}
			}.execute();
		}
	}

	public void prey_moved(Point2D paramPoint2D)
		throws IOException
		{}

	public void prey_caught(long paramLong)
		throws IOException
		{}

	public void game_started(String paramString1, String paramString2)
		throws IOException
		{}

	public void game_reset() {}

	public void time_over(long paramLong, char paramChar)
		throws IOException
		{}

	public String getName()
	{
		return "RandomPrey";
	}
}

