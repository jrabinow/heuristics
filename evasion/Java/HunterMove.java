import java.awt.geom.Point2D;

public class HunterMove
{
	public HunterMoves move;
	public Point2D position;
	public Point2D start = null;
	public Point2D end = null;
	public int wallNumber = -1;
	boolean madeWall = false;
	boolean deletedWall = false;

	public String toString()
	{
		String str = "";
		str = str + this.move.toString();
		if (this.madeWall) {
			str = str + "(" + (int)this.start.getX() + "," + (int)this.start.getY() + "),(" + (int)this.end.getX() + "," + (int)this.end.getY() + ")";
		} else if (this.deletedWall) {
			str = str + this.wallNumber;
		}
		return str;
	}
}

