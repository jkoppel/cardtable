/**
 *For Mao online; handles JoinRequestAccepted messages
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/19/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;
 
 import java.io.*;
 import javax.swing.JOptionPane;
 
 
 //Mastery aspects
 //HL: polymorphism
 //SL: 1,8,10,14
 
public class JoinRequestAcceptedHandler implements MessageHandler
{
	//A JoinRequestAccepted message only consists of a prefix -- nothing
	//to do but alert the player of the join request acceptance
	public byte[] handleMessage(InputStream str)
	{
		JOptionPane.showMessageDialog(null, "Join request accepted!");
		return new byte[0];
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