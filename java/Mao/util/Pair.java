/**
 *Simple data structure that holds two values
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/19/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 
 package mao.util;
 
 import java.io.Serializable;
 
 //Mastery aspects:
 //HL: Encapsulation
 //SL: 2,3,8,9,10
 
 public class Pair<U,V> implements Serializable
 {
 	private U first; //The first value
 	private V second; //The second value
 	
 	
 	//Initializes this Pair
 	public Pair(U u, V v)
 	{
 		first = u;
 		second = v;
 	}
 	
 	//Returns first value
 	public U getFirst()
 	{
 		return first;
 	}
 	
 	//Sets first value
 	public void setFirsT(U u)
 	{
 		first = u;
 	}
 	
 	//Returns second value
 	public V getSecond()
 	{
 		return second;
 	}
 	
 	//Sets second value
 	public void setSecond(V v)
 	{
 		second = v;
 	}
 }