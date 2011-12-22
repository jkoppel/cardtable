/**
 *For Mao online; a label whose position is defined
 *						by a rotatable anchor
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

//Mastery components:
//HL: Inheritance, polymorphism, encapsulation
//SL: 2,3,8,9,10,14

public class RotatableLabel extends
								RotatableAnchorComponent
{
	//The text to display
	private String text; 
		
	 //The color to display it in
	private Color color = Color.BLACK;
	
	//Initializes this RotatableLabel
	public RotatableLabel(String text, int centerX, int centerY)
	{
		this.text = text;
		setAnchor(centerX, centerY);
	}
	
	//Returns the text of this label
	public String getText()
	{
		return text;
	}
	
	//Sets the text of this label
	public void setText(String text)
	{
		this.text = text;
	}
	
	//Returns the color of this label
	public Color getColor()
	{
		return color;
	}
	
	//Sets the color of this label
	public void setColor(Color c)
	{
		color = c;
	}
	
	//Draws this label
	public void draw(Graphics2D g)
	{
		Color oldColor = g.getColor();
		g.setColor(getColor());
		
		Rectangle2D bounds = g.getFont().getStringBounds(getText(),
													g.getFontRenderContext());
		
		Vector2D topLeft = getAnchor().minus(new Vector2D(bounds.getWidth()/2,
															bounds.getHeight()/2));
		g.drawString(text, (int)topLeft.getX(), (int)topLeft.getY());
		
		g.setColor(oldColor);
	}
}