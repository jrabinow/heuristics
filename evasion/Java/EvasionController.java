import java.io.IOException;
import javax.swing.SwingWorker;
import javax.swing.UnsupportedLookAndFeelException;

public class EvasionController
{
	public static void main(String[] paramArrayOfString)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException
	{
		int i = Integer.parseInt(paramArrayOfString[0]);
		int j = Integer.parseInt(paramArrayOfString[1]);
		String str = paramArrayOfString[2];
		long l1 = Long.parseLong(paramArrayOfString[(paramArrayOfString.length - 1)]);
		final EvasionModel localEvasionModel = new EvasionModel(i, j, l1);
		EvasionView localEvasionView = new EvasionView(localEvasionModel, i, j);
		localEvasionModel.register(localEvasionView, 'v');
		long l2;
		long l3;
		int k;
		int m;
		Hunter localHunter;
		Prey localPrey;
		RandomPrey localRandomPrey;
		RandomHunter localRandomHunter;
		switch (str.charAt(0))
		{
			case 'B':
				l2 = Integer.parseInt(paramArrayOfString[3]);
				l3 = Integer.parseInt(paramArrayOfString[4]);
				k = Integer.parseInt(paramArrayOfString[5]);
				m = Integer.parseInt(paramArrayOfString[6]);
				localHunter = new Hunter(localEvasionModel, i, j, l2 * 1000000000L, k);
				localPrey = new Prey(localEvasionModel, i, j, l3 * 1000000000L, m);
				localEvasionModel.register(localHunter, 'h');
				localEvasionModel.register(localPrey, 'p');
				new SwingWorker()
				{
					protected Integer doInBackground()
						throws Exception
					{
						this.val$hunter.init_connection();
						localEvasionModel.hunterReady();
						return Integer.valueOf(0);
					}
				}.execute();
				new SwingWorker()
				{
					protected Integer doInBackground()
						throws Exception
					{
						this.val$prey.init_connection();
						localEvasionModel.preyReady();
						return Integer.valueOf(0);
					}
				}.execute();
				break;
			case 'H':
				l2 = Integer.parseInt(paramArrayOfString[3]);
				k = Integer.parseInt(paramArrayOfString[4]);
				localHunter = new Hunter(localEvasionModel, i, j, l2 * 1000000000L, k);
				localRandomPrey = new RandomPrey(localEvasionModel, i, j);
				localEvasionModel.register(localHunter, 'h');
				localEvasionModel.register(localRandomPrey, 'p');
				localEvasionModel.preyReady();
				localHunter.init_connection();
				localEvasionModel.hunterReady();
				break;
			case 'P':
				l3 = Integer.parseInt(paramArrayOfString[3]);
				m = Integer.parseInt(paramArrayOfString[4]);
				localRandomHunter = new RandomHunter(localEvasionModel, i, j);
				localPrey = new Prey(localEvasionModel, i, j, l3 * 1000000000L, m);
				localEvasionModel.register(localRandomHunter, 'h');
				localEvasionModel.register(localPrey, 'p');
				localEvasionModel.hunterReady();
				localPrey.init_connection();
				localEvasionModel.preyReady();
				break;
			case 'N':
			default:
				localRandomHunter = new RandomHunter(localEvasionModel, i, j);
				localRandomPrey = new RandomPrey(localEvasionModel, i, j);
				localEvasionModel.register(localRandomHunter, 'h');
				localEvasionModel.register(localRandomPrey, 'p');
				localEvasionModel.hunterReady();
				localEvasionModel.preyReady();
		}
	}
}
