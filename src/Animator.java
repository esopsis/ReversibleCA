import javax.swing.JComponent;

public class Animator implements Runnable
{
	public Animator(JComponent comp)
	{
		this.comp = comp;
	}
	
	public void setDelay(int delay)
	{
		this.delay = delay;
	}
	
	public int getDelay()
	{
		return delay;
	}
	
	public void start()
	{
		animationThread = new Thread(this);
		animationThread.start();
	}
	
	public void stop()
	{
		animationThread = null;
	}
	
	public void step()
	{
		step = true;
		start();
	}
	
	public synchronized void run()
	{
		while (Thread.currentThread() == animationThread)
		{
			try
			{
				Thread.sleep(delay);
			}
			catch(InterruptedException e) {}
			
			comp.repaint();
			
			if(step == true)
			{
				step = false;
				stop();
			}
		}
	}

	protected JComponent comp;
	protected int delay;
	protected boolean step = false;
	protected Thread animationThread;
}