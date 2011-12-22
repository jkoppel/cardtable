/**
 *For Mao online; handles incoming ManipulatorAction
 *		messages from the server
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 1/20/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 import mao.online.*;
 import mao.game.Game;
 import java.io.*;
 
 //Master aspects:
 //HL: polymorphism, encapsulation
 //SL: 1,2,3,8,9,10
 
 public class ManipulatorActionMessageHandler implements MessageHandler
 {
 	//Used to map the prefix specifying the type of a manipulator
 	//action to the ManipulatorActionFactory which constructs
 	//ManipulatorActions of that type
 	private static ManipulatorActionFactory[] manFacs =
 			new ManipulatorActionFactory[ManipulatorAction.NUM_ACTION_TYPES];
 	
 	
 	/*
 	 *Initializes manFacs
 	 */
 	static
 	{
 		manFacs[ManipulatorAction.MOVE] = new MoveActionFactory();
 		manFacs[ManipulatorAction.ROTATE] = new RotateActionFactory();
 		manFacs[ManipulatorAction.FLIP] = new FlipActionFactory();
 		manFacs[ManipulatorAction.PICKUP] = new PickUpActionFactory();
 		manFacs[ManipulatorAction.PUTDOWN] = new DropActionFactory();
 		manFacs[ManipulatorAction.INITDRAG] = new InitDragActionFactory();
 		manFacs[ManipulatorAction.TERMDRAG] = new TermDragActionFactory();
 		//manFacs[ManipulatorAction.SHUFFLE] = new ShuffleActionFactory();
 	}
 	
 	//Constructs and executes the ManipulatorAction described by the incoming
 	//ACTION_PERFORMED message. Returns the byte array representing
 	//the ManipulatorAction which was just read
 	public byte[] handleMessage(InputStream str)
 	{
 		try
 		{
 			int type = str.read();
 			byte[] bytes = new byte[manFacs[type].byteArrayLength()];
 			bytes[0] = (byte)type;
 			str.read(bytes, 1, manFacs[type].byteArrayLength()-1);
 			try
 			{
 				manFacs[type].buildFromByteArray(bytes).perform();
 			}
 			catch(ConflictException e)
 			{
 				//Conflict resolution as described in B1 is not yet
 				//implemented
 			}
 			return bytes;
 		}
 		catch(IOException e)
 		{
 			Game.handleManipulatorActionError(e);
 			return new byte[0];
 		}
 	}
 	
 	//Implements method in MessageHandler
 	public boolean shouldSendToSelf()
 	{
 		return false;
 	}
 	
 	//Implements method in MessageHandler
 	public boolean shouldPropagate()
 	{
 		return true;
 	}
 }