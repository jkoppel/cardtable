/**
 *For Mao online; a component that has a position in an abstract space
 *					that can be mapped
 *					to actual space and is drawn in terms of a
 *					certain anchor point
 * 					which is rotated with the display
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
//HL: Inheritance, encapsulation, polymorphism
//SL: 2,3,8,9,10,14

public abstract class RotatableAnchorComponent
						extends WindowIndependentComponent
{
	private int anchorX; //The abstract x of the anchor
	private int anchorY; //The abstract y of the anchor
	
	//Returns the anchor's x coordinate
	public int getAnchorX()
	{
		return anchorX;
	}
	
	//Returns the anchor's y coordinate
	public int getAnchorY()
	{
		return anchorY;
	}
	
	//Sets the anchor's x coordinate
	public void setAnchorX(int x)
	{
		anchorX = x;
	}
	
	//Sets the anchor's y coordinate
	public void setAnchorY(int y)
	{
		anchorY = y;
	}
	
	//Sets the anchor
	public void setAnchor(Point p)
	{
		setAnchorX((int)p.getX());
		setAnchorY((int)p.getY());
	}
	
	//Sets the anchor
	public void setAnchor(int x, int y)
	{
		setAnchorX(x);
		setAnchorY(y);
	}
	
	//Returns the anchor
	public Vector2D getAnchor()
	{
		return new Vector2D(getAnchorX(), getAnchorY());
	}
		
	

	/*
	 *Rotates the anchor, then calls draw,
	 *which should draw in terms of the anchor,
	 *then restores the anchor
	 *
	 *Synchronized, due to temperarily changing anchor; 
	 *	should not be a problem, but desired still
	 */
	public synchronized void drawOperation(AffineTransform rotater, Graphics2D g)
	{	
		Point abstractAnchor = getAnchor();
											 
		Point rotatedAnchor = new Point(0,0);
		rotater.transform(abstractAnchor, rotatedAnchor);
		
		setAnchor(rotatedAnchor);
		
		draw(g);
		
		setAnchor(abstractAnchor);
	}
}