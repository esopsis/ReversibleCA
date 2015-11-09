import java.awt.*;


public class DoubleBufferHandler
{
	public DoubleBufferHandler(DoubleBufferedComponent comp)
	{
		this.comp = comp;
	}
	
	final public void setSize(Dimension dim)
	{
		d = dim;
		im = comp.createImage(d.width, d.height);
		offScreen = im.getGraphics();
	}
	
	final public void update(Graphics g)
	{
		if(im == null)
		{
			setSize(comp.getSize());
		}
		comp.paintFrame(offScreen);
		
		g.drawImage(im, 0, 0, null);
	}
	
	protected DoubleBufferedComponent comp;
	protected Dimension d;
	protected Image im;
	protected Graphics offScreen;
}