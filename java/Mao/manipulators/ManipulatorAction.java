/**
 *For Mao online; abstract superclass of data types which encapsulate an
 *action performed on the
 *table for purposes of sending over the Internet to be duplicated
 *on other machines
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/24/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */


package mao.manipulators;

import mao.online.*;
import mao.util.*;

 //Mastery aspects shown:
 //HL: polymorphism, encapsulation, parsing a data stream
 //SL: 1,2,3,8,9,10

	/*
	 *General format of Manipulator Action serialization:
	 *	8-bit player ID
	 *	8-bit prefix     ---------could easily be 4
	 *			(would uglify code though)
	 *	24-bit location
	 *
	 *for actions other than move or shuffle or initdrag:
	 *	64-bit checksum of affected adjacency graph
	 *				(for conflict checking purposes)
	 *
	 *
	 *If rotation:
	 *	6-bit rotation amount (stored in entire byte)
	 *
	 *If pick-up or put down:
	 *	1 bit each for pileMode, flip, straightening
	 *						(joined together in one byte)
	 *
	 *If shuffle or put-down:  (shuffle actions are not yet implemented)
	 *	Random seed (64 bits)
	 */


public abstract class ManipulatorAction implements java.io.Serializable
{
	/*
	 *Next several fields hold the prefixes indicating the various
	 *	types of manipulator actions
	 *
	 *When reading manipulator actions, this is needed to decide which
	 *		class to instantiate from the data
	 */
	public static final int NULLTYPE = 127;
	public static final int MOVE = 0;
	public static final int ROTATE = 1;
	public static final int FLIP = 2;
	public static final int PICKUP = 3;
	public static final int PUTDOWN = 4;
	public static final int INITDRAG = 5;
	public static final int TERMDRAG = 6;
	public static final int SHUFFLE = 7;
	
	//The number of types of manipulator actions
	public static final int NUM_ACTION_TYPES = 8;
	
	private byte playerID; //ID of the player that performed the action
	
	private int id; //ID of this manipulator action
					//This is unused as it is only needed for
					//undoing conflicting
					//manipulator actions, which is unimplemented
					//(and has yet to be an issue)
	
	
	//Abstract coordinates are explained in the Window Independent
	//system section of B3
	//abstract-X coordinate of where this action was performed
	private int abstractX; 
	//abstract-Y coordinate of where this action was performed
	private int abstractY;
	
	//Initializes a ManipulatorAction containing the given information
	public ManipulatorAction(byte playerID, int abstractX, int abstractY)
	{
		this.playerID = playerID;
		this.abstractX = abstractX;
		this.abstractY = abstractY;
	}
	
	//Super is called implicitly by subclasses' constructors if
	//not called explicitly,
	//so a blank constructor must be provided
	protected ManipulatorAction()
	{
		
	}
	
	//returns the ID of the palyer who performed this action
	public byte getPlayerID()
	{
		return playerID;
	}
	
	//returns the ID of this action
	public int getID()
	{
		return id;
	}
	
	//return the abstract X coordinate where this action was performed
	public int getAbstractX()
	{
		return abstractX;
	}
	
	//returns the abstract y coordinate where this action was performed
	public int getAbstractY()
	{
		return abstractY;
	}
	
	 
	 //Serializes the information in this ManipulatorAction
	public byte[] toByteArray()
	{
		byte[] bytes = new byte[byteArrayLength()];
		bytes[0] = getType();
		bytes[1] = getPlayerID();
		int loc = BinaryUtils.mergeInts(abstractX, abstractY, 12);
		bytes[2] = BinaryUtils.extractByte(loc, 16);
		bytes[3] = BinaryUtils.extractByte(loc,8);
		bytes[4] = BinaryUtils.extractByte(loc,0);
		return bytes;
	}
	
	//Inverse of previous method -- initializes the information of
	//this ManipulatorAction
	//common to all ManipulatorActions from the serialized
	//information in bytes
	public void read(byte[] bytes)
	{
		playerID = bytes[1];
		int loc = BinaryUtils.asBigEndianInt(bytes, 2, 3);
		abstractX = BinaryUtils.onBitMask(12)&loc;
		loc = loc >> 12;
		abstractY = BinaryUtils.onBitMask(12)&loc;
	}
	
	//Returns how many bytes are used to serialize a the information
	//common to all ManipulatorActions
	public int byteArrayLength()
	{
		return 5;
	}
	
	//Returns a prefix specifying the type of this ManipulatorAction
	public abstract byte getType();
	
	//Returns a ManipulatorAction that undoes this ManipulatorAction
	//for purposes of conflict resolution
	//
	//Not yet implemented
	//public abstract ManipulatorAction inverse();
	
	//Orders the local copy of the relevant manipulator
	//to mirror the action originally done by a remote player
	//which is described in this ManipulatorAction
	public abstract void perform() throws ConflictException;
	
	//Throws a ConflictException if performing this action
	//causes a desynchronization error
	//no conflicts by default
	public void checkConflict(long c) throws ConflictException
	{
		
	}
}