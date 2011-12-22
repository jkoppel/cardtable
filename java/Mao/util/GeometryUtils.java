/**
 *For Mao online; contains some helper methods for working
 *with Java geometry classes
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/03/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.util;
 
 import java.awt.geom.*;
 
 //Master aspects
 //HL: none
 //SL: 8,9,10,14
 
 public class GeometryUtils
 {
 	//Returns whether the two areas intersect
 	public static boolean intersects(Area a, Area b)
 	{
 		Area a2 = (Area)a.clone();
 		a2.intersect(b);
 		return !a2.isEmpty();
 	}
 }