/**
 *For Mao online; handles game state transfers done
 *		via TransferableState
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 11/30/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;
 
 import mao.game.Game;
 import java.io.*;
 
 //Mastery aspects:
 //HL: Polymorphism
 //SL: 1,2,3,8,9,10,14
 
public class GameStateTransferHandler implements MessageHandler
{
	//Reads the transferable state sent and orders it to perform
	//the state transfer
	//Does not need to return the message read
	public byte[] handleMessage(InputStream str)
	{
		try
		{
			((TransferableState)((ObjectInputStream)str).readObject()
					).loadState();
		    return null;
		}
		catch(Exception e)
		{
			Game.handleGameStateTransferError(e);
 			System.exit(1);
 			return null;
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
 		return false;
 	}
}