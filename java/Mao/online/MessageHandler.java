/**
 *For Mao online; interface for all classes capable of handling
 *				a certain type of message
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 11/30/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;
 
 import java.io.*;
 
 //Mastery aspects:
 //HL: polymorphism
 //SL: 1,8,9,10,14
 
public interface MessageHandler
{
	//Must return a byte[] equal to the message read (not including prefix)
	//For messages received exlusively by clients, may return null if convenient
	//(e.g.: game state transfer)
	byte[] handleMessage(InputStream str);
	
	//True if this class should be responsible for performing
	//the corresponding action to this message locally as well,
	//meaning the GameClient/GameServer should send this type of message to itself
	boolean shouldSendToSelf();
	
	//False if this type of message is only meant for a single client
	boolean shouldPropagate();
}