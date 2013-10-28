import java.awt.geom.Point2D;
import java.io.IOException;

public abstract interface EvasionListener
{
	public abstract void hunter_moved(HunterMove paramHunterMove, String paramString)
		throws IOException;

	public abstract void prey_moved(Point2D paramPoint2D)
		throws IOException;

	public abstract void prey_caught(long paramLong)
		throws IOException;

	public abstract void game_started(String paramString1, String paramString2)
		throws IOException;

	public abstract void game_reset();

	public abstract void time_over(long paramLong, char paramChar)
		throws IOException;

	public abstract String getName();
}
