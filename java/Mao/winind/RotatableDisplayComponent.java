/**
 *For Mao online; a component that has a position
 *					in an abstract space that can be mapped
 *					to actual space and whose entire display
 *					is rotated with the change
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

//Mastery aspects
//HL: Inheritance, polymorphism
//SL: 8,9,14

public abstract class RotatableDisplayComponent
						extends WindowIndependentComponent
{

	//Simply draws this component applying
	//the rotation on evry pixel drawn
	public void drawOperation(AffineTransform rotater,
										 Graphics2D g)
	{
		AffineTransform old = g.getTransform();
		
		g.transform(rotater);
		
		draw(g);
		
		g.setTransform(old);
	}
}