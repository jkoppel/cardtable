/**
 *For Mao online; manipulator action representing a rotation
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/25/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 import mao.util.*;
 import mao.game.*;
 import mao.draggable.*;
 	
 //Mastery aspects shown:
 //HL: polymorphism, inheritance, encapsulation, parsing a data stream
 //SL: 1,2,3,8,9,10	
 	
 public class RotateAction extends ExternallyInteractingManipulatorAction
 {
 	//by what increment the card was rotated by, expressed in multiples
 	//of mao.game.Controls.ROTATION_AMOUNT
 	private byte rotateAmount; 
 	                           
 	                           
 	//whether CTRL was held when this action was performed
 	private boolean pileMode;
 	//(note that this currently does nothing, as pileMode is
 	// disabled for rotate actions)
 	
 	//Initializes a RotateAction containing the given information
 	public RotateAction(byte playerId, int abstractX, int abstractY,
 						long checksum, byte rotateAmount,
 									boolean pileMode)
 	{
 		super(playerId, abstractX, abstractY, checksum);
 		this.rotateAmount = rotateAmount;
 		this.pileMode = pileMode;
 	}
	
	//Called when deserializing rotate actions
	protected RotateAction()
	{
		
	}
 	
 	//Serializes the information contained in this RotateAction,
 	//using the super method to serialize the information common
 	//to all ExternallyInteractingManipulatorActions
 	public byte[] toByteArray()
 	{
 		byte[] bytes = super.toByteArray();
 		int superlen = super.byteArrayLength();
 		//I wanted to store rotateAmount in 7 bits and pileMode in the 8th,
 		//but the sign caused problems. I took the lazy way out and
 		//just gave pileMode a whole byte to itself
 		bytes[superlen] = (byte)rotateAmount;
 		bytes[superlen+1] = BinaryUtils.booleanToBit(pileMode);
 		return bytes;
 	}
 	
 	//Inverse of previous method -- initializes this RotatateAction
 	//from the serialized information
 	//in bytes
 	public void read(byte[] bytes)
 	{
 		super.read(bytes);
 		int superlen = super.byteArrayLength();
 		pileMode = BinaryUtils.bitToBoolean(bytes[superlen+1]);
 		rotateAmount = bytes[superlen];
 	}
 	
 	//Returns how many bytes are used to serialize a RotateAction
 	public int byteArrayLength()
 	{
 		return super.byteArrayLength()+2;
 	}
 	
 	//Orders the local copy of the relevant player's manipulator
 	//to mirror the action performed by the player
 	public void perform() throws ConflictException
 	{
 		Manipulator m = Game.getPlayer(getPlayerID()).getManipulator();
 		long c = m.rotate(rotateAmount, pileMode);
 		checkConflict(c);
 	}
 	
 	//returns the prefix byte indicating the type of this ManipulatorAction
 	public byte getType()
 	{
 		return ManipulatorAction.ROTATE;
 	}														
 }