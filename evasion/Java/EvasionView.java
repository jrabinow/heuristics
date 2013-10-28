import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Float;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class EvasionView
	implements EvasionListener
{
	Object lock = new Object();
	final EvasionModel model;
	String name;
	long move_counter;
	long time_counter;
	final int N;
	final int W;
	int moves_to_next_wall;
	Point2D preyPosition;
	Point2D hunterPosition;
	HunterMoves hunterDirection;
	ArrayList<Line2D> walls;
	String displayString;
	JFrame frame;
	EvasionView.GPanel mainPanel;
	JTextArea gameDescription;
	JButton hunterButton;
	Graphics2D graphics;

	class GPanel
			extends JPanel
		{
			private static final long serialVersionUID = 4626419896777520312L;
			Graphics2D g2d;

			public GPanel()
			{
				setPreferredSize(new Dimension(500, 500));
			}

			public void paintComponent(Graphics paramGraphics)
			{
				super.paintComponent(paramGraphics);
				this.g2d = ((Graphics2D)paramGraphics);
				this.g2d.setBackground(Color.WHITE);
				this.g2d.clearRect(0, 0, 500, 500);
				this.g2d.setColor(Color.GREEN);
				this.g2d.setStroke(new BasicStroke(1.0F));
				synchronized (EvasionView.this.lock)
				{
					for (Line2D localLine2D : EvasionView.this.walls) {
						this.g2d.draw(localLine2D);
					}
				}
				this.g2d.setColor(Color.BLACK);
				this.g2d.draw(new Line2D.Float(0.0F, 500.0F, 500.0F, 500.0F));
				this.g2d.draw(new Line2D.Float(0.0F, 0.0F, 500.0F, 0.0F));
				this.g2d.draw(new Line2D.Float(0.0F, 0.0F, 0.0F, 500.0F));
				this.g2d.draw(new Line2D.Float(500.0F, 0.0F, 500.0F, 500.0F));
				this.g2d.setColor(Color.BLUE);
				this.g2d.setStroke(new BasicStroke(3.0F, 1, 1));
				this.g2d.draw(new Line2D.Float(EvasionView.this.preyPosition, EvasionView.this.preyPosition));
				this.g2d.setColor(Color.RED);
				this.g2d.setStroke(new BasicStroke(5.0F));
				this.g2d.draw(new Line2D.Float(EvasionView.this.hunterPosition, EvasionView.this.hunterPosition));
			}
		}

	public EvasionView(EvasionModel paramEvasionModel, int paramInt1, int paramInt2)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		this.model = paramEvasionModel;
		this.N = paramInt1;
		this.W = paramInt2;
		this.preyPosition = new Point(330, 200);
		this.hunterPosition = new Point(0, 0);
		this.moves_to_next_wall = paramInt1;
		this.time_counter = 0L;
		this.move_counter = 0L;
		this.hunterDirection = HunterMoves.SE;
		this.walls = new ArrayList();

		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		this.frame = new JFrame("Evasion");
		this.gameDescription = new JTextArea();
		this.mainPanel = new EvasionView.GPanel();
		this.mainPanel.setSize(500, 500);
		this.mainPanel.setBackground(Color.WHITE);

		this.mainPanel.setVisible(true);
		Container localContainer = this.frame.getContentPane();
		localContainer.add(this.mainPanel, "Center");
		localContainer.add(this.gameDescription, "North");
		this.frame.setSize(510, 540);
		this.frame.setMaximumSize(new Dimension(520, 520));
		this.frame.setLocation(200, 200);

		this.frame.setVisible(true);
		this.frame.setResizable(false);
		this.frame.setDefaultCloseOperation(3);
		this.frame.setVisible(true);
	}

	public void hunter_moved(HunterMove paramHunterMove, final String paramString)
	{
		this.move_counter += 1L;
		this.hunterPosition = paramHunterMove.position;
		if (paramHunterMove.madeWall) {
			synchronized (this.lock)
			{
				this.walls.add(new Line2D.Float(paramHunterMove.start, paramHunterMove.end));
			}
		} else if (paramHunterMove.deletedWall) {
			synchronized (this.lock)
			{
				this.walls.remove(paramHunterMove.wallNumber - 1);
			}
		}
		this.hunterDirection = paramHunterMove.move;
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				EvasionView.this.frame.getContentPane().validate();EvasionView.this.frame.getContentPane().repaint();
				EvasionView.this.gameDescription.setText(EvasionView.this.displayString + EvasionView.this.move_counter + paramString);
				return Integer.valueOf(0);
			}
		}.execute();
	}

	public void prey_moved(Point2D paramPoint2D)
	{
		this.preyPosition = paramPoint2D;
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				EvasionView.this.mainPanel.repaint();
				return Integer.valueOf(0);
			}
		}.execute();
	}

	public void prey_caught(long paramLong)
	{
		this.gameDescription.setText(this.displayString + ++this.move_counter + "Hunter Won!!!");
	}

	public void game_started(String paramString1, String paramString2)
	{
		this.displayString = ("Hunter " + paramString1 + " vs Prey " + paramString2 + ". Moves:");
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				EvasionView.this.mainPanel.repaint();
				EvasionView.this.frame.repaint();
				EvasionView.this.gameDescription.setText(EvasionView.this.displayString + EvasionView.this.move_counter);
				return Integer.valueOf(0);
			}
		}.execute();
	}

	public void game_reset() {}

	public void time_over(long paramLong, final char paramChar)
	{
		this.move_counter = paramLong;
		new SwingWorker()
		{
			protected Integer doInBackground()
				throws Exception
			{
				switch (paramChar)
				{
					case 'h':
						EvasionView.this.gameDescription.setText(EvasionView.this.displayString + EvasionView.this.move_counter + "Hunter Timed Out!!!");
						break;
					case 'p':
						EvasionView.this.gameDescription.setText(EvasionView.this.displayString + EvasionView.this.move_counter + "Prey Timed Out!!!");
						break;
				}
				EvasionView.this.frame.repaint();
				return Integer.valueOf(0);
			}
		}.execute();
	}

	public String getName()
	{
		return null;
	}
}

