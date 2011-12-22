/**
 *For Mao online; the top-level GUI class
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/19/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.game;
 
 import mao.online.*;
 import javax.swing.*;
 import java.awt.*;
 import java.awt.event.*;
 
 //Mastery aspects shown:
 //Higher level: Inheritance, encapsulation
 //Standard level: 2,8,9,10,14
 
 
 /*
  *This is a singleton class -- there can be only one instance of
  *CardGamePlayer
  */
 public class CardGamePlayer extends JFrame
 {
 	//Holds the instance
 	private static CardGamePlayer instance;
 	
 	//Returns the instance, or creates it if it's currently null
 	public static CardGamePlayer getInstance()
 	{
 		if(instance == null)
 			instance = new CardGamePlayer();
 			
 		return instance;
 	}
 	
 	//Initializes the GUI
 	private CardGamePlayer()
 	{
 		//Temperary name of this application
 		super("Lambda Cards");
 		
 		//Creating the menues
 		JMenuBar menuBar = new JMenuBar();
 		JMenu menu = new JMenu("File");
 		JMenuItem connect = new JMenuItem("Connect to game");
 		menu.add(connect);
 		JMenuItem host = new JMenuItem("Host a game");
 		menu.add(host);
 		menu.addSeparator();
 		JMenuItem exit = new JMenuItem("Exit");
 		menu.add(exit);
 		
 		//Setting the background to casino green
		getContentPane().setBackground(new Color(0,128,0));
 		
 		menuBar.add(menu);
 		setJMenuBar(menuBar);
 		
 		//When the connect menu item is clicked, this actionPerformed
 		//method will be called, creating a JoinRequestPanel.
 		connect.addActionListener(new ActionListener()
 		{
 			public void actionPerformed(ActionEvent e)
 			{
 				getContentPane().removeAll();
 				JoinRequestPanel panel = new JoinRequestPanel(
 											CardGamePlayer.this);
 				panel.setSize(400,200);
 				panel.setVisible(true);
 			}
 		});
 		
 		//When the "host a game" menu items is clicked,
 		//this actionPerformed method will be called, showing a dialog to 
 		//get the player's handle, then beginning the game.
 		host.addActionListener(new ActionListener()
 		{
 			public void actionPerformed(ActionEvent e)
 			{
 				getContentPane().removeAll();
 				String name = JOptionPane.showInputDialog(CardGamePlayer.this,
 						"Enter your handle", Preferences.getDefaultName());
 						
 				if(name.equals(""))
 					return;
 					
 				Preferences.setDefaultName(name);
 				getContentPane().removeAll();
 				Game.beginNewGame(name);
 			}
 		});
 		
 		//When the exit menu item is clicked, the program exits.
 		exit.addActionListener(new ActionListener()
 		{
 			public void actionPerformed(ActionEvent e)
 			{
 				System.exit(0);
 			}
 		});
 	}
 	
 	//Adds the table to the window
 	public void setTable(Table t)
 	{
 		getContentPane().add(t, BorderLayout.WEST);
 		t.updateUI();
 		repaint();
 	}
 	
 	//Creates a chat box and adds it to the window
 	public void addChatBox()
 	{
 		ChatBox c = new ChatBox();
 		getContentPane().add(c, BorderLayout.EAST);
 		c.updateUI();
 		repaint();
 	}
 	
 	//When the program is run, a CardGamePlayer window is displayed
 	public static void main(String[] args)
 	{
 		CardGamePlayer c = CardGamePlayer.getInstance();
 		c.setSize(1024,768);
 		c.setVisible(true);
 	}
 }