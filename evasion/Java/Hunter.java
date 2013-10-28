import java.awt.Point;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.SwingWorker;

public class Hunter
	extends PlayerServer
	implements EvasionListener
{
	public Hunter(EvasionModel paramEvasionModel, int paramInt1, int paramInt2, long paramLong, int paramInt3)
	{
		super(paramEvasionModel, paramInt1, paramInt2, paramLong, paramInt3);
	}

	public void make_move()
		throws IOException
	{
		send_details();
		char[] arrayOfChar = get_move().toCharArray();
		if (arrayOfChar[0] == 'T') {
			this.model.hunterTimeOver();
		} else {
			this.model.hunterMove(convertToMove(arrayOfChar));
		}
	}

	private HunterMove convertToMove(char[] paramArrayOfChar)
	{
		HunterMove localHunterMove = new HunterMove();
		if (paramArrayOfChar.length < 2)
		{
			localHunterMove.move = this.hunterDirection;
			return localHunterMove;
		}
		switch (paramArrayOfChar[0] + paramArrayOfChar[1])
		{
			case 147:
				localHunterMove.move = HunterMoves.NE;
				break;
			case 165:
				localHunterMove.move = HunterMoves.NW;
				break;
			case 152:
				localHunterMove.move = HunterMoves.SE;
				break;
			case 170:
				localHunterMove.move = HunterMoves.SW;
				break;
			default:
				localHunterMove.move = this.hunterDirection;
		}
		if (paramArrayOfChar.length <= 2) {
			return localHunterMove;
		}
		if (paramArrayOfChar[2] == 'w')
		{
			int i;
			if (paramArrayOfChar[3] == 'x')
			{
				switch (Hunter.6.$SwitchMap$HunterMoves[localHunterMove.move.ordinal()])
				{
					case 1:
						localHunterMove.move = HunterMoves.NWwx;
						break;
					case 2:
						localHunterMove.move = HunterMoves.NEwx;
						break;
					case 3:
						localHunterMove.move = HunterMoves.SWwx;
						break;
					case 4:
						localHunterMove.move = HunterMoves.SEwx;
						break;
				}
				i = 0;
				if ((paramArrayOfChar.length > 4) && (isNum(paramArrayOfChar[4])))
				{
					i = paramArrayOfChar[4] - '0';
					if ((paramArrayOfChar.length > 5) && (isNum(paramArrayOfChar[5])))
					{
						i *= 10;
						i += paramArrayOfChar[5] - '0';
					}
					localHunterMove.wallNumber = i;
				}
			}
			else
			{
				switch (Hunter.6.$SwitchMap$HunterMoves[localHunterMove.move.ordinal()])
				{
					case 1:
						localHunterMove.move = HunterMoves.NWw;
						break;
					case 2:
						localHunterMove.move = HunterMoves.NEw;
						break;
					case 3:
						localHunterMove.move = HunterMoves.SWw;
						break;
					case 4:
						localHunterMove.move = HunterMoves.SEw;
						break;
				}
				i = 0;
				int j = 0;
				int k = 0;
				int m = 0;
				int n = 5;
				i += paramArrayOfChar[4] - '0';
				while (isNum(paramArrayOfChar[n]))
				{
					i *= 10;
					i += paramArrayOfChar[(n++)] - '0';
				}
				n++;
				while (isNum(paramArrayOfChar[n]))
				{
					j *= 10;
					j += paramArrayOfChar[(n++)] - '0';
				}
				n += 3;
				while (isNum(paramArrayOfChar[n]))
				{
					k *= 10;
					k += paramArrayOfChar[(n++)] - '0';
				}
				n++;
				while (isNum(paramArrayOfChar[n]))
				{
					m *= 10;
					m += paramArrayOfChar[(n++)] - '0';
				}
				localHunterMove.start = new Point(i, j);
				localHunterMove.end = new Point(k, m);
			}
		}
		return localHunterMove;
	}

	boolean isNum(char paramChar)
	{
		return (paramChar >= '0') && (paramChar <= '9');
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
			this.walls.add(new Line2D.Float(paramHunterMove.start, paramHunterMove.end));
			this.moves_to_next_wall = this.N;
		}
		else if (paramHunterMove.deletedWall)
		{
			this.walls.remove(paramHunterMove.wallNumber - 1);
		}
		this.hunterDirection = paramHunterMove.move;
		if (this.move_counter % 2 == 1) {
			new SwingWorker()
			{
				protected Integer doInBackground()
					throws Exception
				{
					Hunter.this.make_move();
					return Integer.valueOf(0);
				}
			}.execute();
		}
	}

	public void prey_moved(Point2D paramPoint2D)
		throws IOException
	{
		this.preyPosition = paramPoint2D;
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				Hunter.this.make_move();
				return Integer.valueOf(0);
			}
		}.execute();
	}

	public void prey_caught(long paramLong)
	{
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				Hunter.this.close_conn_game_over("You Won!!!");
				return Integer.valueOf(0);
			}
		}.execute();
	}

	public void game_started(String paramString1, String paramString2)
	{
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				Hunter.this.make_move();
				return Integer.valueOf(0);
			}
		}.execute();
	}

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
						Hunter.this.close_conn_game_over("You Timed Out!!!");
						break;
					case 'p':
						Hunter.this.close_conn_game_over("Prey Timed Out!!!");
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
}
