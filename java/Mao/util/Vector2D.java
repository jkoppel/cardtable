/**
 *Vector2D class for Mao online; makes point
 *		arithmetic more convenient
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 11/29/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
package mao.util;

import java.awt.geom.Point2D;
import java.awt.Point;

//Mastery aspects:
//HL: Inheritance
//SL: 2,8,9,10,14

public class Vector2D extends Point
{
	//Creates a Vector2D represented the given coordinates,
	//rounded down
	//to the nearest integer
	public Vector2D(double x, double y)
	{
		super((int)x,(int)y);
	}
	
	//Creates a Vector2D as a copy of the given Point2D
	public Vector2D(Point2D from)
	{
		this(from.getX(), from.getY());
	}
	
	//Returns a new Vector2D representing the vector-sum of 
	//this and other
	public Vector2D plus(Vector2D other)
	{
		return new Vector2D(getX()+other.getX(),getY()+
												other.getY());
	}
	
	//Returns a new Vector2D representing the vector-differences of 
	//this and other
	public Vector2D minus(Vector2D other)
	{
		return new Vector2D(getX()-other.getX(),getY()-other.getY());
	}
}