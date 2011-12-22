/**
 *For Mao online; handles incoming chat messages
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/2/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;

 import mao.game.Game; 
 import mao.util.*;
 import java.io.*;
 
 //Mastery aspects:
 //HL: encapsulation, polymorphism
 //SL: 1,2,3,8,9,10,14
 
 public class ChatMessageHandler implements MessageHandler
 {
 	//The ChatBox that will display incoming chat messages
 	private ChatBox chatBox;
 	
 	//Initializes a new ChatMessageHandler sending messages to c
 	public ChatMessageHandler(ChatBox c)
 	{
 		chatBox = c;
 	}
 	
 	//Reads a sent line of chat and displays in it the chat box
 	//Returns the byte array read.
 	public byte[] handleMessage(InputStream str)
 	{
 		try
 		{
 			String line = BinaryUtils.readNullTerminatedString(str);
 			chatBox.chatLineReceived(line+"\n");
 			return (line+'\000').getBytes();
 		}
 		catch(IOException e)
 		{
 			Game.handleChatError(e);
 			return "\n".getBytes();
 		}
 	}
 	
 	//Implements method from MessageHandler
 	public boolean shouldSendToSelf()
 	{
 		return true;
 	}
 	
 	//Implements method from MessageHandler
 	public boolean shouldPropagate()
 	{
 		return true;
 	}
 }