/**
 *For Mao online; manipulator action representing dropping cards
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/25/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 import mao.draggable.*;
 import mao.util.*;
 import mao.game.*;
 
 //Mastery aspects shown:
 //HL: polymorphism, inheritance, encapsulation, parsing a data stream
 //SL: 1,2,3,8,9,10
 
 public class DropAction extends ExternallyInteractingManipulatorAction
 {
 	 //whether CTRL was held when this action was performed
 	private boolean pileMode;
 	
 	//whether SHIFT was held when this action was performed
 	private boolean flip; 
 		
 	//whether ALT was held when this action was performed
 	private boolean neat; 
 		
 	//the value that was used to see the pseudorandom number generator
 	//that computed the drop chaos of this action
 	private long randomSeed; 
 	                         
 	
 	//Initializes a DropAction containing the given information
 	public DropAction(byte playerId, int abstractX, int abstractY, long checksum,
 							boolean pileMode, boolean flip, boolean neat, long randomSeed)
 	{
 		super(playerId, abstractX, abstractY, checksum);
 		this.pileMode = pileMode;
 		this.flip = flip;
 		this.neat = neat;
 		this.randomSeed = randomSeed;
 	}
	
	//Called when deserializing drop actions
	protected DropAction()
	{
		
	}
 	
 	//Serializes the information contained in this DropAction,
 	//using the super method to serialize the information common
 	//to all ExternallyInteractingManipulatorActions
 	public byte[] toByteArray()
 	{
 		byte[] bytes = super.toByteArray();
 		int superlen = super.byteArrayLength();
 		//straightening in bit 0, flip in bit 1, pileMode in bit 2
 		bytes[superlen] = BinaryUtils.flagsToByte(neat, flip, pileMode);
 		BinaryUtils.storeLongAsBytes(randomSeed, bytes, superlen+1, 8);
 		return bytes;
 	}
 	
 	//Inverse of previous method -- initializes this DropAction from the
 	//serialized information
 	//in bytes
 	public void read(byte[] bytes)
 	{
 		super.read(bytes);
 		int superlen = super.byteArrayLength();
 		byte modifiers = bytes[superlen];
 		pileMode = BinaryUtils.bitToBoolean((byte)(modifiers&(1<<2)));
 		flip = BinaryUtils.bitToBoolean((byte)(modifiers&(1<<1)));
 		neat = BinaryUtils.bitToBoolean((byte)(modifiers&1));
 		randomSeed = BinaryUtils.asBigEndianLong(bytes, superlen+1,8);
 	}
 	
 	//Returns how many bytes are used to serialize a DropAction
 	public int byteArrayLength()
 	{
 		return super.byteArrayLength()+9;
 	}
 	
 	//Orders the local copy of the relevant player's manipulator
 	//to mirror the action performed by the player
 	public void perform() throws ConflictException
 	{
 		Manipulator m = Game.getPlayer(getPlayerID()).getManipulator();
 		long c = m.putDown(pileMode, flip, neat, randomSeed);
 		checkConflict(c);
 	}
 	
 	//returns the prefix byte indicating the type of this ManipulatorAction
 	public byte getType()
 	{
 		return ManipulatorAction.PUTDOWN;
 	}
 															
 }