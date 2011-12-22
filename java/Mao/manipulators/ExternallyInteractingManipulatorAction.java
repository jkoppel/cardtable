/**
 *For Mao online; abstract superclass of manipulator actions which
 *				act on the table
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/24/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 import mao.util.*;
 import mao.draggable.*;
 
 //Mastery aspects shown:
 //HL: polymorphism, inheritance, encapsulation, parsing a data stream
 //SL: 1,2,3,8,9,10
 	
 public abstract class ExternallyInteractingManipulatorAction extends ManipulatorAction
 {
 	//used for conflict checking; the checksum of the affected adjacency graph
 	private long checksum; 
 	
 	//Initializes an ExternallInteractingManipulatorAction
 	//containing the given information
 	public ExternallyInteractingManipulatorAction(byte playerId, int abstractX,
 												int abstractY, long checksum)
 	{
 		super(playerId, abstractX, abstractY);
 		this.checksum = checksum;
 	}
	
	//Super is called implicitly by subclasses' constructors if not
	//called explicitly,
	//so a blank constructor must be provided
	protected ExternallyInteractingManipulatorAction()
	{
		
	}
 	
 	//Serializes the information contained in all
 	//ExternallInteractingManipulatorActions,
 	//using the super method to serialize the information common
 	//to all ManipulatorActions
 	public byte[] toByteArray()
 	{
 		byte[] bytes = super.toByteArray();
 		int superlen = super.byteArrayLength();
 		BinaryUtils.storeLongAsBytes(checksum, bytes, superlen, 8);
 		return bytes;
 	}
 	
 	//Inverse of previous method -- initializes the checksum of this
 	//ExternallyInteractingManipulatorAction from the serialized information
 	//in bytes
 	public void read(byte[] bytes)
 	{
 		super.read(bytes);
 		int superlen = super.byteArrayLength();
 		checksum = BinaryUtils.asBigEndianLong(bytes, superlen, 8);
 	}
 	
 	//Returns how many bytes are used to serialize the information common
 	//to all ExternallyInteractingManipulatorActions
 	public int byteArrayLength()
 	{
 		return super.byteArrayLength()+8;
 	}
 	
 	//Throws a ConflictException if the checksum of the affected adjacency graph
 	//after performing the action is different locally than when originally done --
 	//that means this action would result in desynchronization
 	public void checkConflict(long c) throws ConflictException
 	{
 		if(checksum != c)
 			throw new ConflictException(this);
 	}
 															
 }