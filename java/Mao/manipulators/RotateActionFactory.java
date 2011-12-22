/**
 *For Mao online; creates the appropriate ManipulatorAction type from a byte array
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 1/20/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 //Mastery aspects shown
 //HL: Polymorphism, inheritance
 //SL: 1,2,3,8,9,10
 
 public class RotateActionFactory extends ManipulatorActionFactory
 {
 	//constructs the RotateAction whose information is stored in bytes
 	public ManipulatorAction buildFromByteArray(byte[] bytes)
 	{
 		RotateAction m = new RotateAction();
 		m.read(bytes);
 		return m;
 	}
 	
 	//returns how many bytes the serialized form of the type of action
 	//this ManipulatorActionFactory constructs takes
 	public int byteArrayLength()
 	{
 		return new RotateAction().byteArrayLength();
 	}
 }