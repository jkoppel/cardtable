/**
 *For Mao online; subclasses of this construct create the appropriate
 *		ManipulatorAction type from a byte array
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 1/20/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 //Mastery aspects shown
 //HL: Polymorphism
 //SL: 1,8,9,10
 
 public abstract class ManipulatorActionFactory
 {
 	//constructs the ManipulatorAction of the type
 	//the concrete subclass of ManipulatorActionFactory
 	//represents whose information is stored in bytes
 	public abstract ManipulatorAction buildFromByteArray(byte[] bytes);
 	
 	//returns how many bytes the serialized form of the type of action
 	//this ManipulatorActionFactory constructs takes
 	public abstract int byteArrayLength();
 }