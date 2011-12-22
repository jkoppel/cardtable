/**
 *For Mao online; manipulator action representing beggining to drag a card
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/25/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 import mao.game.*;
 import mao.util.*;
 import mao.draggable.*;
 	
 //Mastery aspects shown:
 //HL: polymorphism, inheritance
 //SL: 2,8,10
 	
 public class InitDragAction extends ExternallyInteractingManipulatorAction
 {
 	//Initializes an InitAction containing the given information
 	public InitDragAction(byte playerId, int abstractX,
 									int abstractY, long checksum)
 	{
 		super(playerId, abstractX, abstractY, checksum);
 	}
	
	//Called when deserializing init drag actions
	protected InitDragAction()
	{
		
	}
 	
 	//Orders the local copy of the relevant player's manipulator
 	//to mirror the action performed by the player
 	public void perform() throws ConflictException
 	{
 		Manipulator m = Game.getPlayer(getPlayerID()).getManipulator();
 		long c = m.initiateDrag();
 		checkConflict(c);
 	}
 	
 	//returns the prefix byte indicating the type of this ManipulatorAction
 	public byte getType()
 	{
 		return ManipulatorAction.INITDRAG;
 	}														
 }