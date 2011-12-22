/**
 *For Mao online; manipulator action representing picking up cards
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
 
 public class PickUpAction extends ExternallyInteractingManipulatorAction
 {
 	//whether CTRL was held when this action was performed
 	private boolean pileMode; 
 		
 	//whether SHIFT was held when this action was performed
 	private boolean flip; 
 		
 	//whether ALT was held when this action was performed
 	private boolean neat;
 	
 	//Initializes a PickupAction containing the given information
 	public PickUpAction(byte playerId, int abstractX, int abstractY,
 								 long checksum,	boolean pileMode,
 								 boolean flip, boolean straightening)
 	{
 		super(playerId, abstractX, abstractY, checksum);
 		this.pileMode = pileMode;
 		this.flip = flip;
 		this.neat = straightening;
 	}
	
	//Called when deserializing pick-up actions
	protected PickUpAction()
	{
		
	}
 	
 	//Serializes the information contained in this RotateAction,
 	//using the super method to serialize the information common
 	//to all ExternallyInteractingManipulatorActions
 	public byte[] toByteArray()
 	{
 		byte[] bytes = super.toByteArray();
 		int superlen = super.byteArrayLength();
 		//straightening in bit 0, flip in bit 1, pileMode in bit 2
 		bytes[superlen] = BinaryUtils.flagsToByte(neat, flip, pileMode);
 		return bytes;
 	}
 	
 	//Inverse of previous method --
 	//initializes this PickUpAction from the serialized information
 	//in bytes
 	public void read(byte[] bytes)
 	{
 		super.read(bytes);
 		int superlen = super.byteArrayLength();
 		byte modifiers = bytes[superlen];
 		pileMode = BinaryUtils.bitToBoolean((byte)(modifiers&(1<<2)));;
 		flip = BinaryUtils.bitToBoolean((byte)(modifiers&(1<<1)));;
 		neat = BinaryUtils.bitToBoolean((byte)(modifiers&1));;
 	}
 	
 	//Returns how many bytes are used to
 	//serialize a PickupAction
 	public int byteArrayLength()
 	{
 		return super.byteArrayLength()+1;
 	}
 	
 	//Orders the local copy of the relevant player's manipulator
 	//to mirror the action performed by the player
 	public void perform() throws ConflictException
 	{
 		Manipulator m = Game.getPlayer(getPlayerID()).getManipulator();
 		long c = m.pickUp(pileMode, flip, neat);
 		checkConflict(c);
 	}
 	
 	//returns the prefix byte indicating the type of this ManipulatorAction
 	public byte getType()
 	{
 		return ManipulatorAction.PICKUP;
 	}
 }