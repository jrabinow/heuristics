import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.SwingWorker;

public class RandomHunter
	extends RandomPlayer
	implements EvasionListener
{
	int movesToWall;
	HunterMoves direction;
	Point2D loc;
	ArrayList<Line2D> walls;

	public RandomHunter(EvasionModel paramEvasionModel, int paramInt1, int paramInt2)
	{
		super(paramEvasionModel, paramInt1, paramInt2);
		this.movesToWall = paramInt1;
		this.walls = new ArrayList();
		this.direction = HunterMoves.SE;
		this.loc = new Point(0, 0);
	}

	public void hunter_moved(HunterMove paramHunterMove, String paramString)
		throws IOException
	{
		this.moveCount += 1L;
		this.direction = paramHunterMove.move;
		this.loc = paramHunterMove.position;
		if (this.movesToWall > 0) {
			this.movesToWall -= 1;
		}
		if (paramHunterMove.madeWall)
		{
			this.movesToWall = this.N;
			this.walls.add(new Line2D.Float(paramHunterMove.start, paramHunterMove.end));
		}
		if (paramHunterMove.deletedWall) {
			this.walls.remove(paramHunterMove.wallNumber - 1);
		}
		if (this.moveCount % 2L == 1L) {
			new SwingWorker()
			{
				protected Integer doInBackground()
					throws Exception
				{
					RandomHunter.this.model.hunterMove(RandomHunter.this.makeMove());
					return Integer.valueOf(0);
				}
			}.execute();
		}
	}

	public void prey_moved(Point2D paramPoint2D)
		throws IOException
	{
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				RandomHunter.this.model.hunterMove(RandomHunter.this.makeMove());
				return Integer.valueOf(0);
			}
		}.execute();
	}

	private HunterMove makeMove()
	{
		HunterMove localHunterMove = new HunterMove();
		boolean bool1 = this.randGen.nextBoolean();
		if ((this.movesToWall == 0) && (bool1) && (this.walls.size() < this.W - this.W / 8))
		{
			switch (RandomHunter.3.$SwitchMap$HunterMoves[this.direction.ordinal()])
			{
				case 1:
					localHunterMove.move = HunterMoves.NEw;
					break;
				case 2:
					localHunterMove.move = HunterMoves.NWw;
					break;
				case 3:
					localHunterMove.move = HunterMoves.SEw;
					break;
				case 4:
					localHunterMove.move = HunterMoves.SWw;
					break;
			}
			boolean bool2 = this.randGen.nextBoolean();
			boolean bool3 = this.randGen.nextBoolean();
			Point2D.Float localFloat;
			if (bool2)
			{
				if (bool3) {
					localFloat = new Point2D.Float(0.0F, (int)this.loc.getY());
				} else {
					localFloat = new Point2D.Float(499.0F, (int)this.loc.getY());
				}
			}
			else if (bool3) {
				localFloat = new Point2D.Float((int)this.loc.getX(), 0.0F);
			} else {
				localFloat = new Point2D.Float((int)this.loc.getX(), 499.0F);
			}
			localHunterMove.start = this.loc;
			localHunterMove.end = localFloat;
			return localHunterMove;
		}
		if ((this.walls.size() > this.W - 1 * this.W / 4) && (this.randGen.nextBoolean()) &&
				(this.randGen.nextBoolean()))
		{
			int i = this.randGen.nextInt(this.walls.size()) + 1;
			switch (RandomHunter.3.$SwitchMap$HunterMoves[this.direction.ordinal()])
			{
				case 1:
					localHunterMove.move = HunterMoves.NEwx;
					break;
				case 2:
					localHunterMove.move = HunterMoves.NWwx;
					break;
				case 3:
					localHunterMove.move = HunterMoves.SEwx;
					break;
				case 4:
					localHunterMove.move = HunterMoves.SWwx;
					break;
			}
			localHunterMove.wallNumber = i;
			return localHunterMove;
		}
		localHunterMove.move = this.direction;
		return localHunterMove;
	}

	public void prey_caught(long paramLong)
		throws IOException
		{}

	public void game_started(String paramString1, String paramString2)
		throws IOException
	{
		this.model.hunterMove(makeMove());
	}

	public void game_reset() {}

	public void time_over(long paramLong, char paramChar)
		throws IOException
		{}

	public String getName()
	{
		return "RandomHunter";
	}
}

