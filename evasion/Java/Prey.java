import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.SwingWorker;

public class Prey
	extends PlayerServer
	implements EvasionListener
{
	public Prey(EvasionModel paramEvasionModel, int paramInt1, int paramInt2, long paramLong, int paramInt3)
	{
		super(paramEvasionModel, paramInt1, paramInt2, paramLong, paramInt3);
	}

	public void make_move()
		throws IOException
	{
		send_details();
		char[] arrayOfChar = get_move().toCharArray();
		if (arrayOfChar[0] == 'T') {
			this.model.preyTimeOver();
		} else {
			this.model.preyMove(convertToMove(arrayOfChar));
		}
	}

	public void hunter_moved(HunterMove paramHunterMove, String paramString)
		throws IOException
	{
		this.move_counter += 1;
		if (this.moves_to_next_wall > 0) {
			this.moves_to_next_wall -= 1;
		}
		this.hunterPosition = paramHunterMove.position;
		if (paramHunterMove.madeWall)
		{
			this.moves_to_next_wall = this.N;
			this.walls.add(new Line2D.Float(paramHunterMove.start, paramHunterMove.end));
		}
		else if (paramHunterMove.deletedWall)
		{
			this.walls.remove(paramHunterMove.wallNumber - 1);
		}
		this.hunterDirection = paramHunterMove.move;
		if (this.move_counter % 2 == 0) {
			new SwingWorker()
			{
				protected Integer doInBackground()
					throws Exception
				{
					Prey.this.make_move();
					return Integer.valueOf(0);
				}
			}.execute();
		}
	}

	public void prey_moved(Point2D paramPoint2D)
	{
		this.preyPosition = paramPoint2D;
	}

	public void prey_caught(long paramLong)
	{
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				Prey.this.close_conn_game_over("You Lost!!!");
				return Integer.valueOf(0);
			}
		}.execute();
	}

	public void game_started(String paramString1, String paramString2) {}

	public void game_reset() {}

	public void time_over(long paramLong, final char paramChar)
	{
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				switch (paramChar)
				{
					case 'h':
						Prey.this.close_conn_game_over("Hunter Timed Out!!!");
						break;
					case 'p':
						Prey.this.close_conn_game_over("You Timed Out!!!");
						break;
				}
				return Integer.valueOf(0);
			}
		}.execute();
	}

	public String getName()
	{
		return this.name;
	}

	PreyMoves convertToMove(char[] paramArrayOfChar)
	{
		if (paramArrayOfChar.length < 2) {
			return PreyMoves.ZZ;
		}
		switch (paramArrayOfChar[0] + paramArrayOfChar[1])
		{
			case 156:
				return PreyMoves.NN;
			case 166:
				return PreyMoves.SS;
			case 174:
				return PreyMoves.WW;
			case 138:
				return PreyMoves.EE;
			case 147:
				return PreyMoves.NE;
			case 165:
				return PreyMoves.NW;
			case 152:
				return PreyMoves.SE;
			case 170:
				return PreyMoves.SW;
			case 180:
				return PreyMoves.ZZ;
		}
		return PreyMoves.ZZ;
	}
}

