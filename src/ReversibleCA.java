/*
ReversibleCA.java
by Eric J.Parfitt (ejparfitt@gmail.com)

There are three different kinds of patterns which generally crop up in
rule 37R, and this program colors them three different colors, to help
see the pattern better.  Also the code in CACAnvas can be modified to 
visualize the borders between the different patterns.

This program is a modified version of a program by Kwanghyun Paek, 
found here: http://sjsu.rudyrucker.com/~kwanghyung.paek/applet/

Version: 1.0 alpha
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.WindowListener;

public class ReversibleCA extends JApplet implements 
	ActionListener, ItemListener, ChangeListener, Parameters
{
	protected CACanvas canvas;
	protected CARule caRule;
	protected Animator animator;	
	protected JPanel controlPanel;
	protected JDialog customSeed;

	public ReversibleCA()
	{
		Container container = getContentPane();
		container.setLayout(new BorderLayout());
		canvas = new CACanvas(this);
		caRule = CARule.getCARule();
		
		container.add(canvas, BorderLayout.CENTER);
		animator = new Animator(canvas);
				
		controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(3, 5, 1, 1));


		start = new JButton("Start");
		stop = new JButton("Stop");
		step = new JButton("Step");
		goReverse = new JButton("Go Reverse");
		reset = new JButton("Reset");
		
		wolframRuleLabel = new JLabel("Wolfram Rule",SwingConstants.CENTER);
		wolframRule = new JTextField("", 1);
		reverseRule = new JCheckBox("Reversible", DEFAULT_REVERSIBILITY);
		counterLabel = new JLabel("Counter",SwingConstants.CENTER);
		counter = new JTextField("", 1);

		seedLabel = new JLabel("Seed",SwingConstants.CENTER);		
		delay = new JSlider(JSlider.HORIZONTAL, MINIMUM_DELAY, MAXIMUM_DELAY, DEFAULT_DELAY);		
		jumpLabel = new JLabel("Jump",SwingConstants.CENTER);
		jump = new JTextField("", 1);
		
		controlPanel.add(start);
		controlPanel.add(stop);
		controlPanel.add(step);
		controlPanel.add(goReverse);		
		controlPanel.add(reset);
		

		controlPanel.add(wolframRuleLabel);
		controlPanel.add(wolframRule);
		controlPanel.add(reverseRule);
		controlPanel.add(counterLabel);		
		controlPanel.add(counter);
		

		controlPanel.add(seedLabel);
		choice = new Choice();
		choice.addItem("Random");
		choice.addItem("Single");
		choice.addItem("Custom");
		controlPanel.add(choice);

		controlPanel.add(delay);
		
		controlPanel.add(jumpLabel);		
		controlPanel.add(jump);			
		
		container.add(controlPanel, BorderLayout.SOUTH);

		start.addActionListener(this);
		stop.addActionListener(this);
		step.addActionListener(this);
		goReverse.addActionListener(this);
		reset.addActionListener(this);		
		reverseRule.addItemListener(this);
		delay.addChangeListener(this);
		choice.addItemListener(this);
		
		wolframRule.addActionListener(this);
		jump.addActionListener(this);
		
		setRule(DEFAULT_RULE);
		wolframRule.setText(DEFAULT_RULE);
	}

	public void init()
	{
		String att = getParameter("delay");
		if (att != null)
		{
			int delay = Integer.parseInt(att);
			animator.setDelay(delay);
		}
		else {animator.setDelay(DEFAULT_DELAY);}
		
		String cs = getParameter("cellsize");
		if(cs != null)
		{
			cellSize = Integer.parseInt(cs);
		}
		else {cellSize = DEFAULT_CELLSIZE;}
		
		String seed = getParameter("seedtype");
		if(seed != null)
		{
			seedType = seed;
		}
		else {seedType = DEFAULT_SEEDTYPE;}
		
		String rule = getParameter("rule");
		if(rule != null)
		{
			setRule(rule);
			wolframRule.setText(rule);
		}
		else {setRule(DEFAULT_RULE);}

		validate();
		canvas.initCanvas();
	}

	public void start()
	{
		animator.start();
	}
	
	public void stop()
	{
		animator.stop();
	}
	
	private void setRule(String r)
	{
		int wRule = 0;
		
		try {wRule = Integer.parseInt(r);}
		catch(Exception ee)
		{
			jump.setText("Error");
			return;
		}
		
		if(wRule <= 255 && wRule >= 0)
		{
			caRule.setRule(wRule);
		}
	}
	
	private void goJump(String j)
	{
		int jumpjump = 0;
		
		try {jumpjump = Integer.parseInt(j); 
		//wolframRule.setText(" done");
		}
		catch(Exception ee)
		{
			jump.setText("Error");
			return;
		}
		
		animator.stop();
		
		canvas.jump(jumpjump);

	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();

		if(src == start)
		{
			setRule(wolframRule.getText());
			animator.start();
		}
		else if(src == stop) {animator.stop();}
		else if(src == step)
		{
			if(animator.animationThread != null)
			{
				animator.stop();
			}
			animator.step();
		}
		else if(src == reset)
		{
			animator.stop();
			canvas.reset();
		}
		else if(src == goReverse)
		{
		
			if(animator.animationThread == null)
			{
				canvas.goReverse();
			}
			else
			{
				animator.stop();
				canvas.goReverse();
				animator.start();
			}
			
			
		}
		else if(src == wolframRule)
		{
			setRule(wolframRule.getText());
			animator.start();
		}
		else if(src == jump)
		{
			goJump(jump.getText());
		}
	}

	public void itemStateChanged(ItemEvent e)
	{
		Object src = e.getSource();
		boolean on = e.getStateChange() == ItemEvent.SELECTED;
		
		if (src == reverseRule) {canvas.reverseRule = on;}
		else 
		{
			seedType = (String)(e.getItem());
			if("Custom".equals(seedType))
			{
				if(customSeed == null)
				{
					customSeed = new CustomSeedDialog(this);
					customSeed.setLocation(500,50);
					customSeed.setVisible(true);
				}
				else customSeed.show();
			}
			else {canvas.setSeed(seedType);}
		}
   }

	public void stateChanged(ChangeEvent e)
	{
		animator.setDelay(delay.getValue());
   }
   
	class CustomSeedDialog extends JDialog
	{
		public CustomSeedDialog(JApplet owner)
		{
			setTitle("Custom Seed");
			inputPanel = new JPanel(new GridLayout(2,2));

			
			firstSeedLabel = new JLabel(" First Line Seed");
			secondSeedLabel = new JLabel(" Second Line Seed");
			firstSeed = new JTextField("", 10);
			secondSeed = new JTextField("", 10);
			
			inputPanel.add(firstSeedLabel);
			inputPanel.add(firstSeed);
			inputPanel.add(secondSeedLabel);						
			inputPanel.add(secondSeed);						
		
			okPanel = new JPanel(new GridLayout(1,3));
			okButton = new JButton("OK");
			clearButton = new JButton("Clear");
			cancelButton = new JButton("Cancel");
			
			okPanel.add(okButton);
			okPanel.add(clearButton);
			okPanel.add(cancelButton);
			
			Container container = getContentPane();
			container.add(inputPanel, BorderLayout.CENTER);
			container.add(okPanel, BorderLayout.SOUTH);
			
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event)
				{
					returnSeeds();
				}
			});
			
			clearButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event)
				{
					firstSeed.setText("");
					secondSeed.setText("");
				}
			});
			
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event)
				{
					dispose();
				}
			});
			
			setSize(300, 130);
			setFocusable(true);
		}
		
		public void returnSeeds()
		{
			if(!firstSeed.getText().equals("")
				|| !secondSeed.getText().equals(""))
			{
				canvas.setSeeds(firstSeed.getText(), secondSeed.getText());
			}
		}
		
		JPanel inputPanel;
		JPanel okPanel;
		
		JLabel firstSeedLabel;
		JLabel secondSeedLabel;
		JTextField firstSeed;
		JTextField secondSeed;
		JButton okButton;
		JButton clearButton;
		JButton cancelButton;		
	}   
	
	JLabel wolframRuleLabel;
	JTextField wolframRule;
	JButton start;
	JButton stop;
	JButton step;
	JButton reset;

	JButton goReverse;
	JCheckBox reverseRule;
	JLabel counterLabel;
	JTextField counter;
	JLabel jumpLabel;
	JTextField jump;
	JLabel seedLabel;
	Choice choice;
	JSlider delay;
	
	int cellSize;
	String seedType;
}

interface Parameters
{
	int DEFAULT_DELAY = 30;
	int MINIMUM_DELAY = 5;
	int MAXIMUM_DELAY = 200;	

	String DEFAULT_SEEDTYPE = "Random";
	String DEFAULT_RULE = "37";
	boolean DEFAULT_REVERSIBILITY = true;
	byte DEFAULT_CELLSIZE = 2;		
}