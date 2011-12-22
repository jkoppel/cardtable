/**
 *For Mao online; manipulator action representing a player ending a drag
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/25/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 import mao.game.*;
 import mao.draggable.*;
 import mao.util.*;
 	
 //Mastery aspects shown:
 //HL: polymorphism, inheritance
 //SL: 2,8,10
 	
 public class TermDragAction extends ExternallyInteractingManipulatorAction
 {
 	//Initializes a TermDragAction containing the given information
 	public TermDragAction(byte playerId, int abstractX, int abstractY,
 											long checksum)
 	{
 		super(playerId, abstractX, abstractY, checksum);
 	}
	
	//Called when deserializing term-drag actions
	protected TermDragAction()
	{
		
	}
 	
 	//Orders the local copy of the relevant player's manipulator
 	//to mirror the action performed by the player
 	public void perform() throws ConflictException
 	{
 		Manipulator m = Game.getPlayer(getPlayerID()).getManipulator();
 		long c = m.terminateDrag();
 		checkConflict(c);
 	}
 	
 	//returns the prefix byte indicating the type of this ManipulatorAction
 	public byte getType()
 	{
 		return ManipulatorAction.TERMDRAG;
 	}
 }