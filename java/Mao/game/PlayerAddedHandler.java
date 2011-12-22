/**
 *For Mao online; handles messages about players being added
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 2/02/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.game;
 
 import mao.online.*;
 import mao.util.*;
 import mao.manipulators.*;
 import java.io.*;
 
 //Master aspects:
 //HL: Polymorphism, parsing a data stream
 //SL: 1,2,3,4,8,9,10,14
 
 public class PlayerAddedHandler implements MessageHandler
 {
 	/*
 	 *Format of PlayerAddedMessages: 1-byte id
 	 *Upto 255 byte Pascal-style* ASCII string
 	 *4-byte big-endian next manipulator action ID
 	 *
 	 *  *A Pascal-style string consists of a byte representing
 	 *		the length of the string, followed
 	 *   by the string
 	 */
 	
 	//Reads the player represented by this message, adds that player to the game,
 	//and returns
 	//a byte array representing the message read
 	public byte[] handleMessage(InputStream str)
 	{
 		try
 		{
 	    	
	 		byte id = (byte)str.read();
	 		byte len = (byte)str.read();
 		    byte[] msg = new byte[len+5];
 		    
 	    	msg[0] = id;
 	    	msg[1] = len;
 	    	
 	    	str.read(msg, 2, len);
 	    	String name = BinaryUtils.asASCIIString(msg,2,len);
 	    	
 	    	msg[len+2] = (byte)str.read();
 	    	msg[len+3] = (byte)str.read();
 	    	msg[len+4] = (byte)str.read();
 	    	
 	    	int nextManActionID = BinaryUtils.asBigEndianInt(msg, len+2, 3);
 	    	
 	    	if(id == Game.getLocalPlayerID())
 	    	{
 	    		Game.addPlayer(new Player(id, name, true, nextManActionID));
 	    		Game.getTable().setMouseEventHandler(new Controls(
 	    			(NativeManipulator)Game.getLocalPlayer().getManipulator()));
 	    	}
 	    	else
 	    		Game.addPlayer(new Player(id, name, false, nextManActionID));
 	    	
 	    	Game.getLocalPlayer().getManipulator().setFrozen(false);
 	    	Game.getTable().invalidateBuffer();
 	    	
 	    	return msg;
 		}
 		catch(IOException e)
 		{
 			Game.handlePlayerAddedError(e);
 			return new byte[0];
 		}
  	}
  	
  	//Implements method from message handler
  	public boolean shouldSendToSelf()
  	{
  		return true;
  	}
 	
 	//Implements method from message handler
 	public boolean shouldPropagate()
 	{
 		return true;
 	}
 }