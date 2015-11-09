import java.awt.*;

import javax.swing.*;

public class CACanvas extends JPanel implements DoubleBufferedComponent
{
	protected ReversibleCA rCA;	
	protected DoubleBufferHandler dbHandler;
	protected CARule caRule = CARule.getCARule();

	public CACanvas(ReversibleCA rCA)
	{
		this.rCA = rCA;
		reverseRule = rCA.DEFAULT_REVERSIBILITY;
	}	
	
	public void initCanvas()
	{
		cellSize = rCA.cellSize;

		d = getSize();
		System.out.println(d);
		width = d.width;
		height = d.height;
		//width = 500;
		//height = 500;
		columnSize = (int)(width / cellSize + 2);
		midColumn = (int)(columnSize / 2);
		
		seed1 = new byte[columnSize];
		seed2 = new byte[columnSize];
		seed3 = new byte[columnSize];
		seed4 = new byte[columnSize];				

		pastLine = new byte[columnSize];
		prevLine = new byte[columnSize];
		currentLine = new byte[columnSize];
		newLine = new byte[columnSize];	
		patternArr = new int[columnSize];
		
		setSeed(rCA.seedType);		

		dbHandler = new DoubleBufferHandler(this);
	}
	
	public synchronized void paintFrame(Graphics g)
	{
		if(!skipGrow)
		{
			grow();
		}
		else { skipGrow = false;}

		if(clearFlag)
		{
			g.clearRect(0, 0, width, height);
			clearFlag = false;
		}

		if(y + cellSize > height)
		{
			g.copyArea(0, 0, width, height, 0, -cellSize);
			y -= cellSize;
		}

		for(int i = 2; i < columnSize - 1; i++)
		{
			
			/*Comment for border viewing (1 of 4)*/
			g.setColor(colorArr[prevLine[i]][patternArr[i]]);
			g.fillRect(x, y, cellSize, cellSize);
			
			/*Uncomment for border viewing (2 of 4)*/
			/*
			if (partArr[prevLine[i-1]][patternArr[i-1]].equals(white) && partArr[prevLine[i]][patternArr[i]].equals(black)) {
				g.setColor(black);
				//g.fillRect(x - cellSize / 4, y + cellSize / 4, cellSize / 2, cellSize);
				g.fillRect(x, y, cellSize, cellSize);
			}
			else if (partArr[prevLine[i-1]][patternArr[i-1]].equals(black) && partArr[prevLine[i]][patternArr[i]].equals(white)) {
				g.setColor(white);
				//g.fillRect(x - cellSize / 4, y + cellSize / 4, cellSize / 2, cellSize);
				g.fillRect(x, y, cellSize, cellSize);
			}
			else {
				g.setColor(gray);
				g.fillRect(x, y, cellSize, cellSize);
			}
			*/
			
			x += cellSize;	
		}
				
		x = 0;
		y += cellSize;
		
		setCounter();
	}
	

	public synchronized void grow()
	{
		newLine = new byte[columnSize];
			
		prevPattern = 0;
		
		for(int i = 1; i < columnSize - 1; i++)
		{
			prevPattern = 4 * currentLine[i - 1] + 2 * currentLine[i] 
								+ currentLine[i + 1];
			patternArr[i] = prevPattern;
			if((caRule.getRule())[prevPattern] == 1)	{newLine[i] = 1;}
			else {newLine[i] = 0;}
				
			if(reverseRule)
			{
				if(pastLine[i] == 1)	{newLine[i] = (byte)(newLine[i] ^ pastLine[i]);}
			}
		}				
		
		newLine[0] = newLine[columnSize - 2];
		newLine[columnSize -1] = newLine[1];
			
		prevLine = pastLine;
		pastLine = currentLine;
		currentLine = newLine;
	}

   synchronized void setRule(int rule)
   {
   	caRule.setRule(rule);
	}
	
	synchronized void setCounter()
   {
		count += toggleCount;		
		rCA.counter.setText("" + count);
   }
   
   synchronized void resetCounter(int c)
   {
   	toggleCount = 1;
    	count = c;
		rCA.counter.setText("" + count);  	
   }
   
	public synchronized void goReverse()
	{
		newLine = pastLine;
		pastLine = currentLine;
		currentLine = newLine;
		toggleCount = ((toggleCount > 0) ? -1 : 1);
		skipGrow = true;
		repaint();
	}  
   
	public synchronized void reset()
	{
		x = 0;
		y = 0;
		pastLine = new byte[columnSize];
		currentLine = new byte[columnSize];
		newLine = new byte[columnSize];	
		
		if(rCA.seedType.equals("Custom"))
		{
			pastLine = seed1;
			currentLine = seed2;
		}
		else 	setSeed(rCA.seedType);
		
		resetCounter(0);
		clearFlag = true;
		skipGrow = true;
		repaint();
	}
	
	public synchronized void setSeed(String s)
	{
		seed = new byte[columnSize];	
		
		if("Custom".equals(s))
		{
			pastLine = seed1;
			currentLine = seed2;
			return;
		}
		
		if("Single".equals(s))
		{
			seed[midColumn] = 1;
			pastLine = seed;
			currentLine = seed;
		}
		else if("Random".equals(s))
		{
			for(int i = 1; i < columnSize; i++)
			{
				if(Math.random() < 0.98)
				{
					seed3[i] = 0;
				}
				else seed3[i] = 1;
			}
			
			seed3[0] = seed3[columnSize -2];
			seed3[columnSize - 1] = seed3[1];

			for(int i = 1; i < columnSize; i++)
			{
				if(Math.random() < 0.98)
				{
					seed4[i] = 0;
				}
				else seed4[i] = 1;
			}
			
			seed4[0] = seed4[columnSize -2];
			seed4[columnSize - 1] = seed4[1];

			pastLine = seed3;
			currentLine = seed4;			
		}
		skipGrow = true;
		repaint();		
		setCounter();		
	}
	
	public synchronized void setSeeds(String first, String second)
	{
		char[] f = first.toCharArray();
		char[] s = second.toCharArray();
		
		byte[] firstSeed = new byte[f.length];
		byte[] secondSeed = new byte[s.length];
		
		seed1 = new byte[columnSize];
		seed2 = new byte[columnSize];				
		
		for(int i = 0; i < f.length; i++)
		{
			byte temp = Byte.parseByte(""+f[i]);
			if(temp >= 0 && temp <= 1)
			{
				firstSeed[i] = temp;
			}
			else return;
		}
		
		for(int i = 0; i < s.length; i++)
		{
			byte temp = Byte.parseByte(""+s[i]);
			if(temp >= 0 && temp <= 1)
			{
				secondSeed[i] = temp;
			}
			else return;
		}			
		
		if(!first.equals(""))
		{
			int column = midColumn - firstSeed.length / 2;
			
			for(int i = 0; i < firstSeed.length; i++)
			{
				seed1[column] = firstSeed[i];
				column++;
			}

		}
		if(!second.equals(""))
		{
			int column = midColumn - secondSeed.length / 2;
			
			for(int i = 0; i < secondSeed.length; i++)
			{
				seed2[column] = secondSeed[i];
				column++;
			}
		}
		
		pastLine = seed1;			
		currentLine = seed2;
	}

	public void jump(int j)
	{
		if(j == count) return;
		
		if(JumpThread.isNull())
		{
			if((toggleCount == 1 && j < count) || (toggleCount == -1 && j > count)) goReverse();
			JumpThread jt = JumpThread.getJumpThread(this, count, j, toggleCount);
			jt.start();
		}
	}

	public void update(Graphics g)
	{
		dbHandler.update(g);
	}
	
	public void paint(Graphics g)
	{
		update(g);
	}
	
	public int rInt(double x){
		return (int) Math.round(x);
	}
	
	protected Dimension d;
	
	boolean reverseRule;
	boolean clearFlag;
	boolean skipGrow;

	double shader = .25;
	Color black = Color.black;
	Color white = Color.white;
	Color gray = new Color(128,128,128);
	Color red = Color.red;
	Color blue = Color.blue;
	Color yellow = Color.yellow;
	Color orange = Color.orange;
	Color green = Color.green;
	Color purple = new Color(128,0,128);
	Color PB = new Color (64,0,191);
	Color PR = new Color (191,0,64);
	Color GY = new Color(128,255,0);
	Color GB = new Color (0,128,128);
	Color OR = new Color (255,64,0);
	Color OY = new Color (255,191,0);
	Color DR = new Color (rInt(255 * (1 - shader)),0,0);
	Color DO = new Color (rInt(255 * (1-shader)),rInt(127.5 * (1-shader)),0);
	Color DY = new Color (rInt(255 * (1 - shader)),rInt(255 * (1 - shader)),0);
	Color DB = new Color (0,0,rInt(255 * (1 - shader)));
	Color DP = new Color (rInt(127.5 * (1-shader)),0,rInt(127.5 * (1-shader)));
	Color LR = new Color (255,rInt(255 * shader),rInt(255 * shader));
	Color LO = new Color (255,rInt(127.5 + 127.5 * shader), rInt(255 * shader));
	Color LY = new Color (255,255,rInt(255 * shader));
	Color LB = new Color(rInt(255 * shader),rInt(255 * shader),255);
	
	Color RY = new Color(255, 128, 0);
	Color YG = new Color(128, 255, 0);
	Color GC = new Color(0, 255, 128);
	Color CB = new Color(0, 128, 255);
	Color BM = new Color(128, 0, 255);
	Color MR = new Color(255, 0, 128);
	Color magenta = new Color(255, 0, 255);
	
	//For Rule 37R
	//Color[] colorArr0 = {white, black, white, black, black, white, black, white};
	//Color[] colorArr1 = {white, black, white, black, black, white, black, white};
	
	//Comment for border viewing (3 of 4)
	//For Rule 37R
	Color[] colorArr0 = {LY, LR, DB, DR, LR, LB, DR, DY};
	Color[] colorArr1 = {LY, LR, DB, DR, LR, LB, DR, DP};
	
	//For Rule 37R
	//Color[] colorArr0 = {LY, LO, DB, DO, LO, LB, DO, DY};
	//Color[] colorArr1 = {LY, LR, DB, DR, LR, LB, DR, DP};
	
	//For Rule 37R
	//Color[] colorArr0 = {LY, LO, DB, DO, LO, LB, DO, DY};
	//Color[] colorArr1 = {LY, LR, DB, DR, LR, LB, DR, DP};
	
	//For Rule 37R
	//Color[] colorArr0 = {OY, white, PB, PR, white, GB, PR, GY};
	//Color[] colorArr1 = {OY, OR, PB, black, OR, GB, black, purple};
	
	//For Rule 37R
	//Color[] colorArr0 = {OY, OR, PB, PR, OR, GB, PR, GY};
	//Color[] colorArr1 = {OY, OR, PB, PR, OR, GB, PR, purple};
	
	//For Rule 37R
	//Color[] colorArr0 = {YG, RY, BM, MR, RY, CB, MR, GC};
	//Color[] colorArr1 = {YG, RY, BM, MR, RY, CB, MR, magenta};
	
	//For Rule 37R
	//Color[] colorArr0 = {RY, MR, CB, BM, MR, GC, BM, GC};
	//Color[] colorArr1 = {RY, MR, CB, BM, MR, GC, BM, blue};
	
	//For Rule 37R
	//Color[] colorArr0 = {yellow, red, blue, red, red, blue, red, yellow};
	//Color[] colorArr1 = {yellow, red, blue, red, red, blue, red, purple};
	
	//For Rule 37R
	//Color[] colorArr0 = {yellow, red, DB, DR, red, blue, DR, DY};
	//Color[] colorArr1 = {yellow, red, DB, DR, red, blue, DR, DP};
	
	//For Rule 37R
	//Color[] colorArr0 = {LY, LR, blue, red, LR, LB, red, yellow};
	//Color[] colorArr1 = {LY, LR, blue, red, LR, LB, red, purple};
	
	//For Rule 37R
	//Color[] colorArr0 = {white, black, black, black, black, black, black, white};
	//Color[] colorArr1 = {white, black, black, black, black, black, black, black};
	
	//For Rule 73R
	//Color[] colorArr0 = {yellow, yellow, yellow, red, yellow, green, red, red};
	//Color[] colorArr1 = {yellow, green, red, red, green, green, red, blue};
	
	//For Rule 73R
	//Color[] colorArr0 = {yellow, yellow, yellow, red, yellow, yellow, red, red};
	//Color[] colorArr1 = {yellow, yellow, red, red, yellow, yellow, red, blue};
	
	//For Rule 73R
	//Color[] colorArr0 = {white, white, white, black, white, white, black, black};
	//Color[] colorArr1 = {white, white, black, black, white, white, black, black};
	
	//Color[] partArr0 = {red, OR, OY, yellow, GY, GB, blue, PB};
	//Color[] partArr1 = {white, white, white, white, white, white, white, white};
	
	Color[][] colorArr = {colorArr0,colorArr1};
	
	//Uncomment for border viewing (4 of 4)
	//For Rule 37R
	//Color[] partArr0 = {white, black, white, black, black, white, black, white};
	//Color[] partArr1 = {white, black, white, black, black, white, black, white};
	//Color[][]partArr ={partArr0,partArr1};

	int width;
	int height;
	int cellSize;
	int columnSize;
	int midColumn;
	int count;
	int toggleCount = 1;
	int x;
	int y;
	int prevPattern;
	int[] patternArr;

	byte[] pastLine;
	byte[] prevLine;
	byte[] currentLine;
	byte[] newLine;
	byte[] seed;
	byte[] seed1;
	byte[] seed2;
	byte[] seed3;
	byte[] seed4;	
}


class JumpThread extends Thread
{
	private CACanvas owner;
	
	private static JumpThread jumpThread = null;
	

	public static boolean isNull()
	{
		if(jumpThread == null) return true;
		else return false;
	}
	
	public static JumpThread getJumpThread(CACanvas o, int begin, int goal, int toggle)
	{
		if(jumpThread == null) jumpThread = new JumpThread(o, begin, goal, toggle);
		return jumpThread;
	} 

	protected JumpThread(CACanvas o, int begin, int goal, int toggle)
	{
		owner = o;
		this.begin = begin;
		this.goal = goal;
		this.toggle = toggle;
	}
	
	public void run()
	{
		while(begin != goal)
		{
			owner.grow();
			begin += toggle;
			temp++;	
		
		
			if (temp == 3333)
			{
				temp = 0;
				owner.rCA.counter.setText("" + begin);
			}
		}
		owner.count = begin;
		owner.rCA.counter.setText("" + begin);
		jumpThread = null;
		return;
	}

	private int begin;
	private int goal;
	private int toggle;
	private int temp;
}