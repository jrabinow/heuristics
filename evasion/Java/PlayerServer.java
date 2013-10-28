import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PlayerServer
{
	final EvasionModel model;
	String name;
	int move_counter;
	long time_counter;
	final long totalTime;
	final int N;
	final int W;
	int moves_to_next_wall;
	Point2D preyPosition;
	Point2D hunterPosition;
	HunterMoves hunterDirection;
	ArrayList<Line2D> walls;
	final int portNumber;
	PrintWriter out;
	BufferedReader in;
	ServerSocket serverSocket;
	Socket clientSocket;

	public PlayerServer(EvasionModel paramEvasionModel, int paramInt1, int paramInt2, long paramLong, int paramInt3)
	{
		this.model = paramEvasionModel;
		this.N = paramInt1;
		this.W = paramInt2;
		this.totalTime = paramLong;
		this.preyPosition = new Point(330, 200);
		this.hunterPosition = new Point(0, 0);
		this.moves_to_next_wall = paramInt1;
		this.time_counter = 0L;
		this.move_counter = 0;
		this.portNumber = paramInt3;
		this.hunterDirection = HunterMoves.SE;
		this.walls = new ArrayList();
	}

	public void init_connection()
		throws IOException
	{
		this.serverSocket = new ServerSocket(this.portNumber);
		this.clientSocket = this.serverSocket.accept();
		this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		this.out.println("Team Name?");
		while (!this.in.ready()) {}
		this.name = this.in.readLine();
		this.out.println(this.N + " " + this.W);
	}

	public String get_move()
		throws IOException
	{
		this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		int i = 0;
		this.out.println((this.totalTime - this.time_counter) / 1.0E9D);
		long l1 = System.nanoTime();
		while (!this.in.ready())
		{
			long l2 = System.nanoTime();
			this.time_counter += l2 - l1;
			l1 = l2;
			if (this.time_counter > this.totalTime)
			{
				i = 1;
				break;
			}
		}
		String str;
		if (i != 0)
		{
			str = "T";
		}
		else
		{
			while (!this.in.ready()) {}
			str = this.in.readLine();
		}
		return str;
	}

	public void send_details()
	{
		this.out.println("Walls");
		this.out.println(this.walls.size());
		int i = 1;
		for (Line2D localLine2D : this.walls) {
			this.out.println(i++ + " (" + (int)localLine2D.getX1() + "," + (int)localLine2D.getY1() + "),(" + (int)localLine2D.getX2() + "," + (int)localLine2D.getY2() + ")");
		}
		this.out.println("Moves to Next Wall Build");
		this.out.println(this.moves_to_next_wall);
		this.out.println("H " + this.hunterDirection + " " + "(" + (int)this.hunterPosition.getX() + "," + (int)this.hunterPosition.getY() + ")");
		this.out.println("P (" + (int)this.preyPosition.getX() + "," + (int)this.preyPosition.getY() + ")");
	}

	public void close_conn_game_over(String paramString)
		throws IOException
	{
		this.out.println(paramString);
		this.out.println(this.move_counter);
		this.out.close();
		this.in.close();
		this.clientSocket.close();
		this.serverSocket.close();
	}
}

