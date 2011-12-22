/**
 *For Mao online; frictionlessly implements ability to change
 *											game perspective
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 11/29/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */

package mao.winind;

import mao.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;

//Mastery aspects:
//HL: Inheritance, polymorphism, encapsulation
//SL: 2,3,4,5,8,9,10,14

public abstract class WindowIndependentContainer extends JComponent
						 implements MouseInputListener, MouseWheelListener
{
	//How much this WindoxIndendentContainer is rotaed in display,
	//in radians
	private double theta; 
		
	//An AffineTransform that handles the rotation
	private AffineTransform rotater; 
	
	/*
	 *See B1 for an explanation of actual vs. abstract coordinates
	 *
	 *In a nutshell, components specify their position in abstract coordinates,
	 *which are converted by the window independent system via dilation and rotation
	 *to actual coordinates for drawing
	 */
	
	private int abstractWidth; //abstract width of this container
	private int abstractHeight; //abstract height of this container
	
	private int actualWidth; //actual width of this container
	private int actualHeight; //actual height of this container
	
	//all mouse events will be sent here
	private WindowIndependentMouseEventHandler mouseHandler; 
	
	//Stores what this window drew; in future draws,
	//in can just copy this image until it is invalidated
	//Fields declared transient will not be serialized
	//This is transient because images cannot be serialized
	private transient Image buffer;
	
	//Initializes this WindowIndependentContainer
	public WindowIndependentContainer()
	{
		setAbstractSize(1,1);
		setRotationAngle(0);
	}
	
	//Sets this WindowIndependentContainer to receive mouse events
	//and redirect them to e
	public void setMouseEventHandler(WindowIndependentMouseEventHandler e)
	{
		if(null == e)
		{
			removeMouseListener(this);
			removeMouseMotionListener(this);
			removeMouseWheelListener(this);
			mouseHandler = null;
		}
		else if(null == mouseHandler)
		{
			e.setParent(this);
			mouseHandler = e;
			addMouseListener(this);
			addMouseMotionListener(this);
			addMouseWheelListener(this);
		}
		else
		{
			e.setParent(this);
			mouseHandler = e;
		}
	}
	
	//Returns the mouse event handler foor this container
	public WindowIndependentMouseEventHandler getMouseEventHandler()
	{
		return mouseHandler;
	}
	
	//Does the behavior of adding c to this container
	//common to all WindowIndependentContainers
	public void addWindowIndComponent(WindowIndependentComponent c)
	{
		c.setParent(this);
	}
	
	//Returns an AffineTransform which handles the dilation
	//in converting between abstract and actual coordinates
	protected AffineTransform makeScaleTransform()
	{
		AffineTransform scaler = new AffineTransform();
		scaler.scale(((double)getWidth())/getAbstractWidth(),
						((double)getHeight())/getAbstractHeight());
		return scaler;
	}
	
	//See B1 for a thorough description of this method
	public void handleDraw(WindowIndependentComponent c, Graphics2D g)
	{
		c.drawOperation(rotater, g);
	}
	
	/*
	 *Called to state that the display has changed, and the container
	 *will need to redraw rather than simply displaying its buffer
	 */
	public void invalidateBuffer()
	{
		buffer = null;
	}
	
	//Does prep work to draw this container,
	//and orders it to draw
	//
	//Simply copies the buffer to the screen if it can
	public void paintComponent(Graphics g)
	{	
		setSize(getActualSize());
		
		if(null==buffer)
		{	
			buffer = new BufferedImage(getActualWidth(), getActualHeight(),
				BufferedImage.TYPE_4BYTE_ABGR);
			
			Graphics2D g2D = (Graphics2D)buffer.getGraphics();
			
			super.paintComponent(g2D);
			
			AffineTransform oldTransform = g2D.getTransform();
			
			g2D.transform(makeScaleTransform());		
			draw(g2D);
			g2D.setTransform(oldTransform);
		}
		g.drawImage(buffer,0,0,this);
	}
	
	/*
	 *The actual drawing behavior is specified in subclasses
	 */
	public abstract void draw(Graphics2D g);
	
	//Returns by what angle this WindowIndependentContainer is rotated
	public double getRotationAngle()
	{
		return theta;
	}
	
	//Sets the rotation angle of this WindowIndependentContainer
	public void setRotationAngle(double radians)
	{
		theta = radians;
		createRotater();
		invalidateBuffer();
	}
	
	//Returns the abstract height
	public int getAbstractHeight()
	{
		return abstractHeight;
	}
	
	//Returns the abstract width
	public int getAbstractWidth()
	{
		return abstractWidth;
	}
	
	//Sets the abstract height
	public void setAbstractHeight(int h)
	{
		abstractHeight = h;
		createRotater();
		invalidateBuffer();
	}
	
	//Sets the abstract width
	public void setAbstractWidth(int w)
	{
		abstractWidth = w;
		createRotater();
		invalidateBuffer();
	}
	
	//Returns the abstract size
	public Dimension getAbstractSize()
	{
		return new Dimension(getAbstractWidth(), getAbstractHeight());
	}
	
	//Sets the abstract size
	public void setAbstractSize(Dimension d)
	{
		setAbstractWidth((int)d.getWidth());
		setAbstractHeight((int)d.getHeight());
	}
	
	//Sets the abstract size
	public void setAbstractSize(int w, int h)
	{
		setAbstractWidth(w);
		setAbstractHeight(h);
	}
	
	//Returns a Vector2D representing the point
	//which is the center of this WindowIndependentContainer
	public Vector2D getCenter()
	{
		return new Vector2D(getAbstractWidth()/2,getAbstractHeight()/2);
	}
	
	
	//Returns the actual height
	public int getActualHeight()
	{
		return actualHeight;
	}
	
	//Returns the actual width
	public int getActualWidth()
	{
		return actualWidth;
	}
	
	//Sets the actual height
	public void setActualHeight(int h)
	{
		actualHeight = h;
		invalidateBuffer();
	}
	
	//Sets the actual width
	public void setActualWidth(int w)
	{
		actualWidth = w;
		invalidateBuffer();
	}
	
	//Returns the actual size
	public Dimension getActualSize()
	{
		return new Dimension(getActualWidth(), getActualHeight());
	}
	
	//Sets the actual size
	public void setActualSize(Dimension d)
	{
		setActualWidth((int)d.getWidth());
		setActualHeight((int)d.getHeight());
	}
	
	//Sets the actual size
	public void setActualSize(int w, int h)
	{
		setActualWidth(w);
		setActualHeight(h);
	}
	
	//Creates an AffineTransform which
	//handles the rotations involved in drawing
	private synchronized void createRotater()
	{
		Point center = getCenter();
		rotater = AffineTransform.getRotateInstance(
											getRotationAngle(),
											center.getX(), center.getY());
	}
	
	//Returns a MouseEvent which is a copy of e, but with
	//actual coordinates converted into abstract coordinates
	private MouseEvent toAbstractSpace(MouseEvent e)
	{
		Point actualPoint = e.getPoint();
		Point abstractPoint = new Point(0,0);
		
		try
		{
			
			AffineTransform actualToAbstract = rotater.createInverse();
			actualToAbstract.concatenate(
						makeScaleTransform().createInverse());
		
			actualToAbstract.transform(actualPoint, abstractPoint);
		}
		catch(NoninvertibleTransformException f)
		{
			f.printStackTrace();
			System.exit(1);
		}
		
		return new MouseEvent((Component)e.getSource(), e.getID(), e.getWhen(),
								e.getModifiersEx(), (int)abstractPoint.getX(),
								(int)abstractPoint.getY(), e.getClickCount(),
								e.isPopupTrigger(), e.getButton());
	}
	
	/*
	 *The rest of the methods redirect
	 *event handling methods to the mouse event handler
	 */
	
	public void mouseClicked(MouseEvent e)
	{
		getMouseEventHandler().mouseClicked(toAbstractSpace(e));
	}
	
	public void mouseEntered(MouseEvent e)
	{
		getMouseEventHandler().mouseEntered(toAbstractSpace(e));
	}
	
	public void mouseExited(MouseEvent e)
	{
		getMouseEventHandler().mouseExited(toAbstractSpace(e));
	}
	
	public void mousePressed(MouseEvent e)
	{
		getMouseEventHandler().mousePressed(toAbstractSpace(e));
	}
	
	public void mouseReleased(MouseEvent e)
	{
		getMouseEventHandler().mouseReleased(toAbstractSpace(e));
	}
	
	public void mouseDragged(MouseEvent e)
	{
		getMouseEventHandler().mouseDragged(toAbstractSpace(e));
	}
	
	public void mouseMoved(MouseEvent e)
	{
		getMouseEventHandler().mouseMoved(toAbstractSpace(e));
	}
	
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		getMouseEventHandler().mouseWheelMoved(e);
	}
}