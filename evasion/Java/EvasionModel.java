import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class EvasionModel
{
	EvasionListener hunter;
	EvasionListener prey;
	EvasionListener view;
	final int boardSize = 500;
	final double epsilon = 1.0E-6D;
	HunterMoves hunterDirection;
	Point2D hunterPosition;
	Point2D preyPosition;
	ArrayList<Line2D> walls;
	final int N;
	final int W;
	String hunterName;
	String preyName;
	volatile long moveUnitCounter;
	int nextWallTimeLeft;
	boolean hunterReady;
	boolean preyReady;
	boolean gameStarted;
	long waitMillis;
	Object lock;

	public EvasionModel(int paramInt1, int paramInt2, long paramLong)
	{
		this.N = paramInt1;
		this.W = paramInt2;
		this.waitMillis = paramLong;
		this.nextWallTimeLeft = paramInt1;
		this.hunterPosition = new Point(0, 0);
		this.preyPosition = new Point(330, 200);
		this.hunterDirection = HunterMoves.SE;
		this.walls = new ArrayList();
		this.lock = new Object();
		this.preyReady = (this.hunterReady = this.gameStarted = 0);
	}

	public void sleepForSomeTime()
	{
		try
		{
			Thread.sleep(this.waitMillis);
		}
		catch (InterruptedException localInterruptedException) {}
	}

	public void hunterReady()
		throws IOException
		{
			synchronized (this.lock)
			{
				this.hunterReady = true;
				if (this.preyReady) {
					startGame();
				}
			}
		}

	public void preyReady()
		throws IOException
		{
			synchronized (this.lock)
			{
				this.preyReady = true;
				if (this.hunterReady) {
					startGame();
				}
			}
		}

	public void hunterMove(HunterMove paramHunterMove)
		throws IOException
		{
			incrMoveUnitCounter();
			sleepForSomeTime();
			makeHunterMove(paramHunterMove);
			if (this.nextWallTimeLeft > 0) {
				this.nextWallTimeLeft -= 1;
			}
		}

	public void preyTimeOver()
		throws IOException
		{
			fireTimeOverEvent('p');
		}

	private void fireTimeOverEvent(char paramChar)
		throws IOException
		{
			this.view.time_over(getMoveUnitCounter(), paramChar);
			this.hunter.time_over(getMoveUnitCounter(), paramChar);
			this.prey.time_over(getMoveUnitCounter(), paramChar);
			switch (paramChar)
			{
				case 'p':
					System.out.println("Prey Time over in " + getMoveUnitCounter() + " moves.");
					break;
				case 'h':
					System.out.println("Hunter Time over in " + getMoveUnitCounter() + " moves.");
					break;
			}
		}

	public void hunterTimeOver()
		throws IOException
		{
			fireTimeOverEvent('h');
		}

	private void firePreyCaughtEvent()
		throws IOException
		{
			this.view.prey_caught(getMoveUnitCounter());
			this.hunter.prey_caught(getMoveUnitCounter());
			this.prey.prey_caught(getMoveUnitCounter());
			System.out.println("Prey Caught in " + getMoveUnitCounter() + " moves.");
		}

	public void preyMove(PreyMoves paramPreyMoves)
		throws IOException
		{
			sleepForSomeTime();
			this.preyPosition = makePreyMove(paramPreyMoves);
			if (hasWon()) {
				firePreyCaughtEvent();
			} else {
				firePreyMovedEvent(this.preyPosition);
			}
		}

	private void firePreyMovedEvent(Point2D paramPoint2D)
		throws IOException
		{
			this.view.prey_moved(paramPoint2D);
			this.hunter.prey_moved(paramPoint2D);
			this.prey.prey_moved(paramPoint2D);
		}

	public void register(EvasionListener paramEvasionListener, char paramChar)
	{
		switch (paramChar)
		{
			case 'h':
				this.hunter = paramEvasionListener;
				break;
			case 'p':
				this.prey = paramEvasionListener;
				break;
			case 'v':
				this.view = paramEvasionListener;
				break;
			case 'l':
				break;
		}
	}

	public void startGame()
		throws IOException
		{
			if (this.gameStarted) {
				return;
			}
			this.gameStarted = true;

			this.hunterName = this.hunter.getName();
			this.preyName = this.prey.getName();
			fireGameStartedEvent();
		}

	private void fireGameStartedEvent()
		throws IOException
		{
			this.view.game_started(this.hunterName, this.preyName);
			this.hunter.game_started(this.hunterName, this.preyName);
			this.prey.game_started(this.hunterName, this.preyName);
		}

	public synchronized void makeHunterMove(HunterMove paramHunterMove)
		throws IOException
		{
			HunterMove localHunterMove = new HunterMove();
			localHunterMove.move = this.hunterDirection;
			String str = "";
			switch (EvasionModel.1.$SwitchMap$HunterMoves[paramHunterMove.move.ordinal()])
			{
				case 1:
				case 2:
				case 3:
				case 4:
					if ((paramHunterMove.wallNumber > 0) && (this.walls.size() >= paramHunterMove.wallNumber))
					{
						this.walls.remove(paramHunterMove.wallNumber - 1);
						localHunterMove.deletedWall = true;
						localHunterMove.wallNumber = paramHunterMove.wallNumber;
					}
					else
					{
						localHunterMove.deletedWall = false;
						str = "Tried deleting Wall " + paramHunterMove.wallNumber + " which does not exist";
					}
					break;
			}
			Point localPoint = new Point((Point)this.hunterPosition);
			this.hunterPosition = updateHunterPosAndDir();
			localHunterMove.move = this.hunterDirection;
			localHunterMove.position = this.hunterPosition;
			switch (EvasionModel.1.$SwitchMap$HunterMoves[paramHunterMove.move.ordinal()])
			{
				case 5:
				case 6:
				case 7:
				case 8:
					localHunterMove.madeWall = false;
					localHunterMove.deletedWall = false;
					break;
				case 9:
				case 10:
				case 11:
				case 12:
					localHunterMove.madeWall = true;
					if (this.walls.size() == this.W)
					{
						localHunterMove.madeWall = false;
					}
					else
					{
						Line2D.Float localFloat = new Line2D.Float(paramHunterMove.start, paramHunterMove.end);
						if ((Math.abs(paramHunterMove.start.getX() - paramHunterMove.end.getX()) > 1.0E-6D) && (Math.abs(paramHunterMove.start.getY() - paramHunterMove.end.getY()) > 1.0E-6D))
						{
							localHunterMove.madeWall = false;
							str = "Wall not built as it was Diagonal";
						}
						else if (localFloat.ptSegDist(this.hunterPosition) <= 1.0E-6D)
						{
							localHunterMove.madeWall = false;
							str = "Wall not built to prevent squishing";
						}
						else if (localFloat.ptSegDist(this.preyPosition) <= 1.0E-6D)
						{
							localHunterMove.madeWall = false;
							str = "Wall not built as it passes through prey";
						}
						else if (localFloat.ptSegDist(localPoint) > 1.0E-6D)
						{
							localHunterMove.madeWall = false;
							str = "Wall not built as it was not at hunter position";
						}
						else
						{
							int i = 0;
							double[] arrayOfDouble = new double[4];
							arrayOfDouble[0] = paramHunterMove.start.getX();
							arrayOfDouble[1] = paramHunterMove.start.getY();
							arrayOfDouble[2] = paramHunterMove.end.getX();
							arrayOfDouble[3] = paramHunterMove.end.getY();
							for (double d : arrayOfDouble) {
								if ((0.0D - d > 1.0E-6D) || (d - 499.0D > 1.0E-6D))
								{
									i = 1;
									break;
								}
							}
							if (i != 0)
							{
								localHunterMove.madeWall = false;
								str = "Wall not built as it was out of bounds";
							}
							else
							{
								for (??? = this.walls.iterator(); ((Iterator)???).hasNext();)
								{
									Line2D localLine2D = (Line2D)((Iterator)???).next();
									if (localLine2D.intersectsLine(localFloat))
									{
										localHunterMove.madeWall = false;
										str = "Wall not built as it itersects with other walls";
										break;
									}
								}
							}
						}
						if (localHunterMove.madeWall)
						{
							this.walls.add(localFloat);
							localHunterMove.start = paramHunterMove.start;
							localHunterMove.end = paramHunterMove.end;
							this.nextWallTimeLeft = this.N;
						}
					}
					break;
			}
			if (hasWon()) {
				firePreyCaughtEvent();
			} else {
				fireHunterMovedEvent(localHunterMove, str);
			}
		}

	private Point2D updateHunterPosAndDir()
	{
		try
		{
			this.walls.add(new Line2D.Float(-1.0F, -1.0F, 500.0F, -1.0F));
			this.walls.add(new Line2D.Float(-1.0F, -1.0F, -1.0F, 500.0F));
			this.walls.add(new Line2D.Float(-1.0F, 500.0F, 500.0F, 500.0F));
			this.walls.add(new Line2D.Float(500.0F, -1.0F, 500.0F, 500.0F));
			Point localPoint1;
			Object localObject1;
			Line2D localLine2D1;
			Point localPoint2;
			Point localPoint3;
			Point localPoint4;
			Point localPoint5;
			Object localObject2;
			Line2D localLine2D2;
			Object localObject3;
			Line2D localLine2D3;
			Point2D localPoint2D;
			switch (EvasionModel.1.$SwitchMap$HunterMoves[this.hunterDirection.ordinal()])
			{
				case 1:
				case 5:
				case 9:
					localPoint1 = new Point((int)this.hunterPosition.getX() + 1, (int)this.hunterPosition.getY() - 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D)
						{
							localPoint2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() - 1);
							localPoint3 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() + 1);
							localPoint4 = new Point((int)localPoint1.getX() + 1, (int)localPoint1.getY());
							localPoint5 = new Point((int)localPoint1.getX() - 1, (int)localPoint1.getY());
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint3) <= 1.0E-6D)
								{
									for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
									{
										localLine2D3 = (Line2D)((Iterator)localObject3).next();
										if (localLine2D3.ptSegDist(localPoint5) <= 1.0E-6D)
										{
											this.hunterDirection = HunterMoves.SW;
											return this.hunterPosition;
										}
									}
									this.hunterDirection = HunterMoves.NW;
									return localPoint5;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint5) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.SE;
									return localPoint3;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint2) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.NW;
									return localPoint5;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint4) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.SE;
									return localPoint3;
								}
							}
							this.hunterDirection = HunterMoves.SE;
							return localPoint3;
						}
					}
					return localPoint1;
				case 2:
				case 6:
				case 10:
					localPoint1 = new Point((int)this.hunterPosition.getX() - 1, (int)this.hunterPosition.getY() - 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D)
						{
							localPoint2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() - 1);
							localPoint3 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() + 1);
							localPoint4 = new Point((int)localPoint1.getX() + 1, (int)localPoint1.getY());
							localPoint5 = new Point((int)localPoint1.getX() - 1, (int)localPoint1.getY());
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint3) <= 1.0E-6D)
								{
									for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
									{
										localLine2D3 = (Line2D)((Iterator)localObject3).next();
										if (localLine2D3.ptSegDist(localPoint4) <= 1.0E-6D)
										{
											this.hunterDirection = HunterMoves.SE;
											return this.hunterPosition;
										}
									}
									this.hunterDirection = HunterMoves.NE;
									return localPoint4;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint5) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.SW;
									return localPoint3;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint2) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.NE;
									return localPoint4;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint4) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.SW;
									return localPoint3;
								}
							}
							this.hunterDirection = HunterMoves.SW;
							return localPoint3;
						}
					}
					return localPoint1;
				case 3:
				case 7:
				case 11:
					localPoint1 = new Point((int)this.hunterPosition.getX() + 1, (int)this.hunterPosition.getY() + 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D)
						{
							localPoint2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() - 1);
							localPoint3 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() + 1);
							localPoint4 = new Point((int)localPoint1.getX() + 1, (int)localPoint1.getY());
							localPoint5 = new Point((int)localPoint1.getX() - 1, (int)localPoint1.getY());
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint2) <= 1.0E-6D)
								{
									for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
									{
										localLine2D3 = (Line2D)((Iterator)localObject3).next();
										if (localLine2D3.ptSegDist(localPoint5) <= 1.0E-6D)
										{
											this.hunterDirection = HunterMoves.NW;
											return this.hunterPosition;
										}
									}
									this.hunterDirection = HunterMoves.SW;
									return localPoint5;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint5) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.NE;
									return localPoint2;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint3) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.SW;
									return localPoint5;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint4) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.NE;
									return localPoint2;
								}
							}
							this.hunterDirection = HunterMoves.NE;
							return localPoint2;
						}
					}
					return localPoint1;
				case 4:
				case 8:
				case 12:
					localPoint1 = new Point((int)this.hunterPosition.getX() - 1, (int)this.hunterPosition.getY() + 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D)
						{
							localPoint2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() - 1);
							localPoint3 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() + 1);
							localPoint4 = new Point((int)localPoint1.getX() + 1, (int)localPoint1.getY());
							localPoint5 = new Point((int)localPoint1.getX() - 1, (int)localPoint1.getY());
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint2) <= 1.0E-6D)
								{
									for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
									{
										localLine2D3 = (Line2D)((Iterator)localObject3).next();
										if (localLine2D3.ptSegDist(localPoint4) <= 1.0E-6D)
										{
											this.hunterDirection = HunterMoves.NE;
											return this.hunterPosition;
										}
									}
									this.hunterDirection = HunterMoves.SE;
									return localPoint4;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint5) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.NW;
									return localPoint2;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint3) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.SE;
									return localPoint4;
								}
							}
							for (localObject2 = this.walls.iterator(); ((Iterator)localObject2).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject2).next();
								if (localLine2D2.ptSegDist(localPoint4) <= 1.0E-6D)
								{
									this.hunterDirection = HunterMoves.NW;
									return localPoint2;
								}
							}
							this.hunterDirection = HunterMoves.NW;
							return localPoint2;
						}
					}
					return localPoint1;
			}
			return this.hunterPosition;
		}
		finally
		{
			this.walls.remove(this.walls.size() - 1);
			this.walls.remove(this.walls.size() - 1);
			this.walls.remove(this.walls.size() - 1);
			this.walls.remove(this.walls.size() - 1);
		}
	}

	public long getMoveUnitCounter()
	{
		synchronized (this.lock)
		{
			return this.moveUnitCounter;
		}
	}

	public void setMoveUnitCounter(long paramLong)
	{
		synchronized (this.lock)
		{
			this.moveUnitCounter = paramLong;
		}
	}

	public void incrMoveUnitCounter()
	{
		synchronized (this.lock)
		{
			this.moveUnitCounter += 1L;
		}
	}

	private void fireHunterMovedEvent(HunterMove paramHunterMove, String paramString)
		throws IOException
		{
			this.view.hunter_moved(paramHunterMove, paramString);
			this.hunter.hunter_moved(paramHunterMove, paramString);
			this.prey.hunter_moved(paramHunterMove, paramString);
		}

	public synchronized Point2D makePreyMove(PreyMoves paramPreyMoves)
	{
		try
		{
			this.walls.add(new Line2D.Float(-1.0F, -1.0F, 500.0F, -1.0F));
			this.walls.add(new Line2D.Float(-1.0F, -1.0F, -1.0F, 500.0F));
			this.walls.add(new Line2D.Float(-1.0F, 500.0F, 500.0F, 500.0F));
			this.walls.add(new Line2D.Float(500.0F, -1.0F, 500.0F, 500.0F));
			Object localObject1;
			Point localPoint1;
			Line2D localLine2D1;
			Object localObject2;
			Point localPoint2;
			Point localPoint3;
			Point localPoint4;
			Object localObject3;
			Line2D localLine2D2;
			Object localObject4;
			Line2D localLine2D3;
			Point2D localPoint2D;
			switch (EvasionModel.1.$SwitchMap$PreyMoves[paramPreyMoves.ordinal()])
			{
				case 1:
					return this.preyPosition;
				case 2:
					localPoint1 = new Point((int)this.preyPosition.getX(), (int)this.preyPosition.getY() - 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D) {
							return this.preyPosition;
						}
					}
					return localPoint1;
				case 3:
					localPoint1 = new Point((int)this.preyPosition.getX(), (int)this.preyPosition.getY() + 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D) {
							return this.preyPosition;
						}
					}
					return localPoint1;
				case 4:
					localPoint1 = new Point((int)this.preyPosition.getX() + 1, (int)this.preyPosition.getY());
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D) {
							return this.preyPosition;
						}
					}
					return localPoint1;
				case 5:
					localPoint1 = new Point((int)this.preyPosition.getX() - 1, (int)this.preyPosition.getY());
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D) {
							return this.preyPosition;
						}
					}
					return localPoint1;
				case 6:
					localPoint1 = new Point((int)this.preyPosition.getX() + 1, (int)this.preyPosition.getY() - 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D)
						{
							localObject2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() - 1);
							localPoint2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() + 1);
							localPoint3 = new Point((int)localPoint1.getX() + 1, (int)localPoint1.getY());
							localPoint4 = new Point((int)localPoint1.getX() - 1, (int)localPoint1.getY());
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint2) <= 1.0E-6D)
								{
									for (localObject4 = this.walls.iterator(); ((Iterator)localObject4).hasNext();)
									{
										localLine2D3 = (Line2D)((Iterator)localObject4).next();
										if (localLine2D3.ptSegDist(localPoint4) <= 1.0E-6D) {
											return this.preyPosition;
										}
									}
									return localPoint4;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint4) <= 1.0E-6D) {
									return localPoint2;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist((Point2D)localObject2) <= 1.0E-6D) {
									return localPoint4;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint3) <= 1.0E-6D) {
									return localPoint2;
								}
							}
							return localPoint2;
						}
					}
					return localPoint1;
				case 7:
					localPoint1 = new Point((int)this.preyPosition.getX() - 1, (int)this.preyPosition.getY() - 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D)
						{
							localObject2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() - 1);
							localPoint2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() + 1);
							localPoint3 = new Point((int)localPoint1.getX() + 1, (int)localPoint1.getY());
							localPoint4 = new Point((int)localPoint1.getX() - 1, (int)localPoint1.getY());
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint2) <= 1.0E-6D)
								{
									for (localObject4 = this.walls.iterator(); ((Iterator)localObject4).hasNext();)
									{
										localLine2D3 = (Line2D)((Iterator)localObject4).next();
										if (localLine2D3.ptSegDist(localPoint3) <= 1.0E-6D) {
											return this.preyPosition;
										}
									}
									return localPoint3;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint4) <= 1.0E-6D) {
									return localPoint2;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist((Point2D)localObject2) <= 1.0E-6D) {
									return localPoint3;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint3) <= 1.0E-6D) {
									return localPoint2;
								}
							}
							return localPoint2;
						}
					}
					return localPoint1;
				case 8:
					localPoint1 = new Point((int)this.preyPosition.getX() + 1, (int)this.preyPosition.getY() + 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D)
						{
							localObject2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() - 1);
							localPoint2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() + 1);
							localPoint3 = new Point((int)localPoint1.getX() + 1, (int)localPoint1.getY());
							localPoint4 = new Point((int)localPoint1.getX() - 1, (int)localPoint1.getY());
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist((Point2D)localObject2) <= 1.0E-6D)
								{
									for (localObject4 = this.walls.iterator(); ((Iterator)localObject4).hasNext();)
									{
										localLine2D3 = (Line2D)((Iterator)localObject4).next();
										if (localLine2D3.ptSegDist(localPoint4) <= 1.0E-6D) {
											return this.preyPosition;
										}
									}
									return localPoint4;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint4) <= 1.0E-6D) {
									return localObject2;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint2) <= 1.0E-6D) {
									return localPoint4;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint3) <= 1.0E-6D) {
									return localObject2;
								}
							}
							return localObject2;
						}
					}
					return localPoint1;
				case 9:
					localPoint1 = new Point((int)this.preyPosition.getX() - 1, (int)this.preyPosition.getY() + 1);
					for (localObject1 = this.walls.iterator(); ((Iterator)localObject1).hasNext();)
					{
						localLine2D1 = (Line2D)((Iterator)localObject1).next();
						if (localLine2D1.ptSegDist(localPoint1) <= 1.0E-6D)
						{
							localObject2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() - 1);
							localPoint2 = new Point((int)localPoint1.getX(), (int)localPoint1.getY() + 1);
							localPoint3 = new Point((int)localPoint1.getX() + 1, (int)localPoint1.getY());
							localPoint4 = new Point((int)localPoint1.getX() - 1, (int)localPoint1.getY());
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist((Point2D)localObject2) <= 1.0E-6D)
								{
									for (localObject4 = this.walls.iterator(); ((Iterator)localObject4).hasNext();)
									{
										localLine2D3 = (Line2D)((Iterator)localObject4).next();
										if (localLine2D3.ptSegDist(localPoint3) <= 1.0E-6D) {
											return this.preyPosition;
										}
									}
									return localPoint3;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint4) <= 1.0E-6D) {
									return localObject2;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint2) <= 1.0E-6D) {
									return localPoint3;
								}
							}
							for (localObject3 = this.walls.iterator(); ((Iterator)localObject3).hasNext();)
							{
								localLine2D2 = (Line2D)((Iterator)localObject3).next();
								if (localLine2D2.ptSegDist(localPoint3) <= 1.0E-6D) {
									return localObject2;
								}
							}
							return localObject2;
						}
					}
					return localPoint1;
			}
			return this.preyPosition;
		}
		finally
		{
			this.walls.remove(this.walls.size() - 1);
			this.walls.remove(this.walls.size() - 1);
			this.walls.remove(this.walls.size() - 1);
			this.walls.remove(this.walls.size() - 1);
		}
	}

	public boolean hasWon()
	{
		Line2D.Float localFloat = new Line2D.Float(this.hunterPosition, this.preyPosition);
		for (Line2D localLine2D : this.walls) {
			if (localFloat.intersectsLine(localLine2D)) {
				return false;
			}
		}
		return 4.0D - this.hunterPosition.distance(this.preyPosition) > 1.0E-6D;
	}
}
