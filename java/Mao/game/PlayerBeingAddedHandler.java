/**
 *For Mao online; handles PlayersBeingAdded messages
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/19/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.game;
 
 import mao.online.*;
 import java.io.*;
 import javax.swing.JOptionPane;
 
 //Mastery aspects:
 //HL: Polymorphism
 //SL: 1,2,8,10,14
 
public class PlayerBeingAddedHandler implements MessageHandler
{
	/*
	 *PlayerBeingAddedMessages are bodiless, containing nothing
	 *beyond their prefix.
	 *The response is to freeze the manipulator while the player
	 *is being added, and display
	 *a message about it.
	 */
	public byte[] handleMessage(InputStream str)
	{
		Game.getLocalPlayer().getManipulator().setFrozen(true);
		JOptionPane.showMessageDialog(null, "A player is being added; stand by.");
		return new byte[0];
	}
	
	//Implements method from MessageHandler
	public boolean shouldSendToSelf()
	{
		return false;
	}
	
	//Implements method from MessageHandler
	public boolean shouldPropagate()
	{
		return false;
	}
}