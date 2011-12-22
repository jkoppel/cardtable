/**
 *For Mao online; a class implementing a box that has chat features
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/1/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;
 
 import mao.game.Game;
 import java.util.*;
 import java.awt.*;
 import java.awt.event.*;
 import javax.swing.*;
 import java.io.*;
 
 
 //Master aspects:
 //HL: Encapsulation, polymorphism
 //SL: 3,4,8,9,14
 
 public class ChatBox extends JPanel implements KeyListener
 {
 	private JTextArea chatDisplay; //Displays the chat
 	private JTextField entry; //Used to enter new chat messages
 	
 	//Creates a new chat box
 	public ChatBox()
 	{
 		//This chat box will now receive all chat messages
 		GameClient.addHandler(Prefixes.CHAT, new ChatMessageHandler(this));
 		
 		chatDisplay = new JTextArea(40,15);
 		chatDisplay.setEnabled(false);
 		
 		entry = new JTextField();
 			
 		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
 		
 		add(new JScrollPane(chatDisplay));
 		add(entry);
 		
 		entry.addKeyListener(this);
 	}
 	
 	//A chat line has been received over the Internet; displays it
 	//
 	//Note that this line of chat may have actually originated here, but
 	//being sent through the GameServer's pipeline makes everything
 	//be in uniform order
 	public void chatLineReceived(String line)
 	{
 		chatDisplay.append(line);
 	}
 	
 	//Required to implement KeyListener
 	public void keyPressed(KeyEvent e)
 	{
 		
 	}
 	
 	//Required to implement KeyListener
 	public void keyReleased(KeyEvent e)
 	{
 		
 	}
 	
 	//When Enter is struck, sends the typed line of chat
 	//in entry over the Internet.
 	public void keyTyped(KeyEvent e)
 	{
 		if(e.getKeyChar()=='\n')
 		{
 			try
 			{
 				String line = Game.getLocalPlayer().getName() + ": " +
 						entry.getText() + "\000";
 				entry.setText("");
 				GameClient.getInstance().sendMessage(Prefixes.CHAT,
 											line.getBytes());
 			}
 			catch(IOException f)
 			{
 				System.err.println("Error sending chat message.");
 				f.printStackTrace();
 			}
 		}
 		
 	}
 }