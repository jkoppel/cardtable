/**
 *For Mao online; manipulator action representing flipping cards
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
 //HL: polymorphism, inheritance, encapsulation, parsing a data stream
 //SL: 1,2,3,8,9,10
 	
 public class FlipAction extends ExternallyInteractingManipulatorAction
 {
 	 //whether CTRL was held when this action was performed
 	private boolean pileMode;
 	
 	//Initializes a FlipAction containing the given information
 	public FlipAction(byte playerId, int abstractX, int abstractY,
 									long checksum, boolean pileMode)
 	{
 		super(playerId, abstractX, abstractY, checksum);
 		this.pileMode = pileMode;
 	}
	
	//Called when deserializing flip actions
	protected FlipAction()
	{
		
	}
 	
 	//Serializes the information contained in this FlipAction,
 	//using the super method to serialize the information common
 	//to all ExternallyInteractingManipulatorActions
 	public byte[] toByteArray()
 	{
 		byte[] bytes = super.toByteArray();
 		int superlen = super.byteArrayLength();
 		//pileMode in bit 0
 		bytes[superlen] = BinaryUtils.flagsToByte(pileMode);
 		return bytes;
 	}
 	
 	//Inverse of previous method -- initializes this FlipAction
 	//from the serialized information
 	//in bytes
 	public void read(byte[] bytes)
 	{
 		super.read(bytes);
 		int superlen = super.byteArrayLength();
 		pileMode = BinaryUtils.bitToBoolean(bytes[superlen]);
 	}
 	
 	//Returns how many bytes are used to serialize a FlipAction
 	public int byteArrayLength()
 	{
 		return super.byteArrayLength()+1;
 	}
 	
 	//Orders the local copy of the relevant player's manipulator
 	//to mirror the action performed by the player
 	public void perform() throws ConflictException
 	{
 		Manipulator m = Game.getPlayer(getPlayerID()).getManipulator();
 		long c = m.flip(pileMode);
 		checkConflict(c);
 	}
 	
 	//returns the prefix byte indicating the type of this ManipulatorAction
 	public byte getType()
 	{
 		return ManipulatorAction.FLIP;
 	}
 }