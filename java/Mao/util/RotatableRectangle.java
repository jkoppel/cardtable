/**
 *For Mao online; a polygon in a rectangular shape which can
 *		be easily rotated about its center
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/16/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.util;
 
 import java.awt.*;
 import java.awt.geom.*;
 
 //Mastery aspects:
 //HL: Inheritance, polymorphism, encapsulation
 //SL: 1,2,8,9,10,14
 
 public class RotatableRectangle extends Polygon
 {
 	/*
 	 *These represent the coordinates of the four vertices
 	 */
 	private int x1;
 	private int y1;
 	private int x2;
 	private int y2;
 	private int x3;
 	private int y3;
 	private int x4;
 	private int y4;
 	
 	//Initializes this as a rectangle with given top-left
 	//corner, width, height,
 	//and angle of rotation
 	public RotatableRectangle(int x, int y, int width, int height,
 													 double rotation)
 	{
 		super(new int[]{x,x+width,x+width,x},
 							new int[]{y, y,y+height,y+height}, 4);
 		
 		x1 = x;
 		x2 = x+width;
 		x3 = x+width;
 		x4 = x;
 		
 		y1 = y;
 		y2 = y;
 		y3 = y+height;
 		y4 = y+height;
 		
 		rotate(rotation);
 	}
 	
 	//Translates this RotatableRectangle
 	public synchronized void translate(int deltaX, int deltaY)
 	{
 		super.translate(deltaX, deltaY);
 		
 		x1 += deltaX;
 		y1 += deltaY;
 		x2 += deltaX;
 		y2 += deltaY;
 		x3 += deltaX;
 		y3 += deltaY;
 		x4 += deltaX;
 		y4 += deltaY;
 	}
 	
 	//Returns the centroid of this shape
 	public Vector2D getCentroid()
 	{
 		int x = (x1+x2+x3+x4)/4;
 		int y = (y1+y2+y3+y4)/4;
 		return new Vector2D(x,y);
 	}
 	
 	//Overrides the method in Polygon; this can
 	//only represent a rectangular shape
 	public void addPoint(int x, int y)
 	{
 		throw new UnsupportedOperationException();
 	}
 	
 	//Rotates this RotatableRectangle about its centroid
 	//by theta in radians. Also changes its parent
 	//Polygon to match
 	public synchronized void rotate(double theta)
 	{
 		Vector2D centroid = getCentroid();
 		AffineTransform rotater = AffineTransform.getRotateInstance(theta, 
 			centroid.getX(), centroid.getY());
 		
 		
 		reset();
 		
 		Point a = new Point(x1,y1);
 		Point b = new Point(x2,y2);
 		Point c = new Point(x3,y3);
 		Point d = new Point(x4,y4);
 		
 		rotater.transform(a,a);
 		rotater.transform(b,b);
 		rotater.transform(c,c);
 		rotater.transform(d,d);
 		
 		x1 = (int)a.getX();
 		y1 = (int)a.getY();
 		x2 = (int)b.getX();
 		y2 = (int)b.getY();
 		x3 = (int)c.getX();
 		y3 = (int)c.getY();
 		x4 = (int)d.getX();
 		y4 = (int)d.getY();
 		
 		super.addPoint(x1, y1);
 		super.addPoint(x2, y2);
 		super.addPoint(x3, y3);
 		super.addPoint(x4, y4);
 	}
 	
 	//Returns a string description of the vertices of this
 	//RotatableRectangle
 	public String toString()
 	{
 		return "("+x1+","+y1+"),("+x2+","+y2+"),("+x3+","+y3+"),("+x4+","+y4+")";
 	}
 }