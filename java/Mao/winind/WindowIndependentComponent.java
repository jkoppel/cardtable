/**
 *For Mao online; a component that has a
 *					position in an abstract space that can be mapped
 *					to actual space
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
import javax.swing.*;
import java.awt.geom.*;

//Mastery aspects:
//HL: Encapsulation, polymorphism, inheritance
//SL: 2,3,8,9,10,14

public abstract class WindowIndependentComponent extends JComponent
{
	/*
	 *See B1 for an explanation of actual vs. abstract coordinates
	 *
	 *In a nutshell, components specify their position in abstract coordinates,
	 *which are converted by the window independent system via
	 *dilation and rotation
	 *to actual coordinates for drawing
	 */
	 
	private int abstractX; //abstract x-coordinate of this component
	private int abstractY; //abstract y-coordinate of this component
	
	//container this component is in
	private WindowIndependentContainer parent; 
	
	/*
	 *See B1 for a full description of the next two methods
	 *
	 *Basically, different components rotate different ways
	 *when the WindowIndependentContainer shifts perspective.
	 *
	 *DrawOperation handles how it rotates, and then calls draw to
	 *actually draw it
	 */
	
	public abstract void drawOperation(AffineTransform rotater,
													Graphics2D g);
	public abstract void draw(Graphics2D g);
	
	
	//Overrides method in JComponent
	public Dimension getPreferredSize()
	{
		return getSize();
	}
	
	//Returns the abstract x
	public int getAbstractX()
	{
		return abstractX;
	}
	
	
	//Returns the abstract y
	public int getAbstractY()
	{
		return abstractY;
	}
	
	//Sets the abstract x
	public void setAbstractX(int x)
	{
		abstractX = x;
	}
	
	//Sets the abstract y
	public void setAbstractY(int y)
	{
		abstractY = y;
	}
	
	//Returns the (x,y) abstract location
	public Vector2D getAbstractLocation()
	{
		return new Vector2D(getAbstractX(),getAbstractY());
	}
	
	//Sets the abstract location
	public void setAbstractLocation(Point p)
	{
		setAbstractX((int)p.getX());
		setAbstractY((int)p.getY());
	}
	
	//Sets the abstract location
	public void setAbstractLocation(int x, int y)
	{
		setAbstractX(x);
		setAbstractY(y);
	}
	
	//Sets the container that contains this component
	public void setParent(WindowIndependentContainer c)
	{
		parent = c;
	}
	
	//Returns the container that contains this component
	public WindowIndependentContainer getParent()
	{
		return parent;
	}
}