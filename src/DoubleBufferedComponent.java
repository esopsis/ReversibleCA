import java.awt.*;

interface DoubleBufferedComponent
{
	void paintFrame(Graphics g);
	Dimension getSize();
	Image createImage(int width, int height);
}