

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Supershaq_nm {

	static Socket client;
	static PrintWriter out;
	static BufferedReader in;

	private static final String[] programs = { "dlru", "dlur", "drlu", "drul",
			"dulr", "durl", "ldru", "ldur", "lrdu", "lrud", "ludr", "lurd",
			"rdlu", "rdul", "rldu", "rlud", "rudl", "ruld", "udlr", "udrl",
			"uldr", "ulrd", "urdl", "urld" };

	public List<Node> remainingNodesList = new ArrayList<Node>();
	public boolean[][] edgeMatrix; 
	
	private List<Nanomuncher> myNanomunchers;
	private List<Nanomuncher> otherNanomunchers;

	private int myScore;
	private int opponentScore;

	private int myRemainingMunchers;
	private int hisRemainingMunchers;
	private long remainingTime;

	public Supershaq_nm(int port) throws UnknownHostException, IOException {

		client = new Socket("127.0.0.1", port);
		out = new PrintWriter(client.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(client.getInputStream()));

		send("SuperShaq");
		parseData(receive());
	}

	private void parseData(String data) {
		String[] specs = data.split("\n");
		boolean startNodes = false;
		boolean startEdges = false;
		edgeMatrix = null;
		
		for (String line : specs) {
			String content = line.trim().toLowerCase();
			if (content.equals(""))
				continue;
			
			if (content.contains("xloc")) {
				startNodes = true;
			
			} else if (content.contains("nodeid1")) {
				startEdges = true;
				edgeMatrix = new boolean[remainingNodesList.size()][remainingNodesList.size()];
				//Arrays.fill(edgeMatrix, Boolean.FALSE);
			
			} else if (startEdges) {
				String[] edgeSpecs = line.split(",");
				
				int nodeId1 = Integer.parseInt(edgeSpecs[0]);
				int nodeId2 = Integer.parseInt(edgeSpecs[1]);
				
				edgeMatrix[nodeId1][nodeId2] = true;
				edgeMatrix[nodeId2][nodeId1] = true;
				
				Node a = remainingNodesList.get(nodeId1);
				Node b = remainingNodesList.get(nodeId2);
				
				if (a.x == b.x) {
					if (a.y - b.y == 1) {
						a.up = b;
						b.down = a;				
					} else {
						a.down = b;
						b.down = a;
					}
				} else {
					if (a.x - b.x == 1) {
						a.left = b;
						b.right  = a;
					} else {
						a.right = b;
						b.left  = a;
					}
				}
			} else if (startNodes) {
				String[] nodeSpecs = line.split(",");
				int id = Integer.parseInt(nodeSpecs[0]);
				int x = Integer.parseInt(nodeSpecs[1]);
				int y = Integer.parseInt(nodeSpecs[2]);
				
				Node n = new Node(id,x,y);
				
				remainingNodesList.add(n);
				
				Board.nodes[x][y] = new Node(id,x,y);
				
			}
		}
	}

	public boolean parseStat(String str) {
		if (str.equals("0")) {
			return false;
		}
		String[] stats = str.split("\n");
		String[] munched = stats[0].split(":");
		if (Integer.parseInt(munched[0]) > 0) {
			String[] nodes = munched[1].split(",|/");
			for (int i = 0; i < Integer.parseInt(munched[0]); i++) {
				remainingNodesList.get(Integer.parseInt(nodes[i])).eaten = true;
			}
		}
		
		myNanomunchers = new ArrayList<Nanomuncher>();
		String[] myMunchers = stats[1].split(":");
		if (Integer.parseInt(myMunchers[0]) > 0) {
			String[] myMuncherDetails = myMunchers[1].split(",");
			for (int i = 0; i < Integer.parseInt(myMunchers[0]); i++) {
				String[] muncher = myMuncherDetails[i].split("/");
				myNanomunchers.add(new Nanomuncher(muncher[1],
				remainingNodesList.get(Integer.parseInt(muncher[0])),
				Integer.parseInt(muncher[2])));
			}
		}
		otherNanomunchers = new ArrayList<Nanomuncher>();
		String[] otherMunchers = stats[2].split(":");
		if (Integer.parseInt(otherMunchers[0]) > 0) {
			String[] otherMuncherDetails = otherMunchers[1].split(",");
			for (int i = 0; i < Integer.parseInt(otherMunchers[0]); i++) {
				otherNanomunchers.add(new Nanomuncher(
				remainingNodesList.get(Integer.parseInt(otherMuncherDetails[i]))));
			}
		}
		
		String[] scores = stats[3].split(",");
		myScore = Integer.parseInt(scores[0]);
		opponentScore = Integer.parseInt(scores[1]);
		String[] remainingInfo = stats[4].split(",");
		myRemainingMunchers = Integer.parseInt(remainingInfo[0]);
		hisRemainingMunchers = Integer.parseInt(remainingInfo[1]);
		remainingTime = Long.parseLong(remainingInfo[2]);
		return true;
	}

	public String receive() throws IOException {
		StringBuffer sb = new StringBuffer();
		String temp;
		while (!(temp = in.readLine()).equalsIgnoreCase("<EOM>")) {
			sb.append(temp + "\n");
		}
		sb.deleteCharAt(sb.length() - 1);
		System.out.println("receive:");
		System.out.println(sb.toString());
		return sb.toString();
	}

	public void send(String str) {
		System.out.println("send:");
		out.println(str);
		System.out.println(str);
		out.println("<EOM>");
		System.out.println("<EOM>");
	}

	public void startGame() throws IOException, InterruptedException {
		while (parseStat(receive())) {
			System.out.println("remaining munchers: " + myRemainingMunchers);
			Thread.sleep(500);
			strategy1();
		}
	}
	
	
	
	//write your strategies here as functions. 
	public void strategy1() {
	
		for( int i = 0; i<remainingNodesList.size();i++){
			if(!remainingNodesList.get(i).eaten){
				send("1:"+i+"/"+"rlud");
			}
		}
		
	}
	
	
	
	public void strategy2() {
	
		
		
	}

	public void strategy3() {
	
		
		
	}
	


	public static void main(String[] args) throws UnknownHostException,
			IOException, InterruptedException {

		if (args.length != 1) {
			System.out.println("java RandomPlayer <port>");
			System.exit(0);
		}

		int port = Integer.parseInt(args[0]);
		Supershaq_nm player = new Supershaq_nm(port);
		player.startGame();
	}
}
