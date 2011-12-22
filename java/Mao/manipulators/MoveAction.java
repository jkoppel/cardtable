/**
 *For Mao online; manipulator action representing a player
 *			moving his mouse/manipulator
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/25/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 import mao.game.*;
 import mao.util.*;
 
 //Mastery aspects shown:
 //HL: polymorphism, inheritance
 //SL: 2,8,10
 
 public class MoveAction extends ManipulatorAction
 {
 	//Initializes a MoveAction containing the given information
 	public MoveAction(byte playerID, int abstractX, int abstractY)
 	{
 		super(playerID, abstractX, abstractY);
 	}
	
	//called when deserializing move actions
	protected MoveAction()
	{
		
	}
 	
 	//Orders the local copy of the relevant player's manipulator
 	//to mirror the action performed by the player
 	public void perform()														
 	{
 		Manipulator m = Game.getPlayer(getPlayerID()).getManipulator();
 		m.setAbstractLocation(getAbstractX(), getAbstractY());
 		m.getSurface().repaint();
 	}
 	
 	//returns the prefix byte indicating the type of this ManipulatorAction
 	public byte getType()
 	{
 		return ManipulatorAction.MOVE;
 	}
 }