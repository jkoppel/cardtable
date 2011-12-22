/**
 *For Mao online; exception thrown when a conflict
 *			between manipulator actions is detected
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/25/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 import mao.util.*;
 
 //Mastery aspects shown:
 //HL: Encapsulation, inheritance
 //SL: 2,3,8,10,14
 
 public class ConflictException extends Exception
 {
 	//the ManipulatorAction that caused this Exception to be thrown
 	private ManipulatorAction conflicter;
 	
 	//Initializes this object with the given value for conflicter
 	public ConflictException(ManipulatorAction conflicter)
 	{
 		this.conflicter = conflicter;
 	}
 	
 	//returns the ManipulatorAction that caused this exception to be thrown
 	public ManipulatorAction getConflictingAction()
 	{
 		return conflicter;
 	}
 }