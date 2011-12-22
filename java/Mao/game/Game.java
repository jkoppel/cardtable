/**
 *For Mao online; top-level class holding information on the state of the game
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 1/07/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.game;
 
 import mao.draggable.*;
 import mao.manipulators.*;
 import mao.online.*;
 import mao.util.*;
 import java.util.*;
 import java.net.Socket;
 import java.io.*;
 import java.awt.Color;
 import javax.swing.*;
 
 //Mastery aspects shown:
 //Higher level: Encapsulation, hierarchical composite data structures
 //Standard level: 1,2,3,4,6,7,8,9,10,14
 
 public class Game
 {
 	//holds players in order of ID, keyed by ID
 	private static LinkedHashMap<Byte, Player> players = new LinkedHashMap<Byte, Player>();
 	
 	//The table that all the action is happening on
 	private static Table table;
 	
 	//Whether this computer is the host
 	private static boolean amServer;
 	
 	//For server only -- holds the ID of the next player to be created
 	//0 is  a null ID; IDs start at 1
 	private static byte nextPlayerID = 1;
 	
 	//The ID of the player on this computer
 	private static byte localPlayerID;
 	
 	//Starts a new game, with the host taking the handle in name
 	//This consists of starting the server, creating the players list,
 	//adding a Player object representing this player, 
 	//creating the table the game takes place on and adding
 	//it and a chat box to the window, and creating a deck of 52 cards
 	//to play with
 	public static void beginNewGame(String name)
 	{
 		try
 		{
 			GameServer.makeInstance(GameClient.DEFAULT_PORT);
 		}
 		catch(IOException e)
 		{
 			JOptionPane.showMessageDialog(CardGamePlayer.getInstance(),
 												"Error creating server");
 			return;
 		}
 		
 		players = new LinkedHashMap<Byte, Player>();
 		setServer(true);
 		nextPlayerID = 1;
 		
 		table = new Table();
 		table.setActualSize(700, 600);
 		table.setAbstractSize(700,600);
		
		table.setBorder(BorderFactory.createLineBorder(Color.RED,2));
 		
 		addPlayer(makeLocalPlayer(name));
 		
 		table.setMouseEventHandler(new Controls(
 				(NativeManipulator)getLocalPlayer().getManipulator()));
 		
 		CardGamePlayer.getInstance().setTable(table);
 		CardGamePlayer.getInstance().addChatBox();
 		
 		
 		for(Card.Rank r : Card.Rank.values())
 			for(Card.Suit s : Card.Suit.values())
 				table.addDraggable(new Card(r, s));
 	}
 	
 	//Called when state is being transferred -- sets the list of players to h
 	protected static void setPlayers(LinkedHashMap<Byte, Player> h)
 	{
 		players = h;
 	}
 	
 	//Sets whether this computer is the host
 	public static void setServer(boolean b)
 	{
 		amServer = b;
 	}
 	
 	//Gets which index the player p is stored in the LinkedHashMap players
 	public static int getPlayerIndex(Player p)
 	{
 		int idx = 0;
 		for(Player q : players.values())
 		{
 			if(p.getID()==q.getID())
 				return idx;
 			idx++;
 		}
 		return -1;
 	}
 	
 	//Returns the list of players
 	public static LinkedHashMap<Byte, Player> getPlayers()
 	{
 		return players;
 	}
 	
 	//Gets the player with ID id
 	public static Player getPlayer(byte id)
 	{
 		return players.get(id);
 	}
 	
 	//Gets the player playing on this computer
 	public static Player getLocalPlayer()
 	{
 		return getPlayer(getLocalPlayerID());
 	}
 	
 	//Adds a player named name, with oos connected to his computer,
 	//transferring the state to him, and then signalling all computers
 	//to add a Player object
 	//for this player to their player list -- including the player's computer
 	public static void readyRemotePlayer(ObjectOutputStream oos, String name)
 																 throws IOException
 	{
 		Player p = newRemotePlayer(name);
 		new CardGameState(p.getID()).sendSelf(oos);
 		sendAddPlayerMessage(p);
 	}
 	
 	//Adds p to the player list
 	public static void addPlayer(Player p)
 	{
 		players.put(p.getID(), p);
 	}
 	
 	//This creates a PLAYER_ADDED message encoding the information of p,
 	//and sends it
 	//
 	//See the PlayerAddedHandler class for a description of the format
 	public static void sendAddPlayerMessage(Player p)
 	{
 		if(amServer)
 		{
 			byte[] bytes = new byte[p.getName().length()+5];
 			bytes[0] = p.getID();
 			bytes[1] = (byte)p.getName().length();
 			byte[] s = p.getName().getBytes();
 			System.arraycopy(s,0,bytes,2,s.length);
 			BinaryUtils.storeAsBytes(p.getNextManipulatorActionID(), bytes,
 																s.length+2, 3);
 			
 			try
 			{
 				GameServer.getInstance().sendMessage(Prefixes.PLAYER_ADDED, bytes);
 			}
 			catch(java.io.IOException e)
 			{
 				handlePlayerAddedError(e); 
 			}
 		}
 	}
 	
 	//Creates a player object representing a player playing on this client
 	//named name
 	public static Player makeLocalPlayer(String name)
 	{
 		localPlayerID = nextPlayerID;
 		return new Player(nextPlayerID++, name, true, 1);
 	}
 	
 	//Creates a player object representing a player playing remotely
 	//named name
 	public static Player newRemotePlayer(String name)
 	{
 		return new Player(nextPlayerID++, name, false, 1);
 	}
 	
 	//Sets the ID of the loca player
 	public static void setLocalPlayerID(byte id)
 	{
 		localPlayerID = id;
 	}
 	
 	//Gets the ID of the local player
 	public static byte getLocalPlayerID()
 	{
 		return localPlayerID;
 	}
 	
 	//Returns the table the game takes place on
 	public static Table getTable()
 	{
 		return table;
 	}
 	
 	//Sets the table the game takes place on
 	public static void setTable(Table t)
 	{
 		table = t;
 	}
 	
 	
 	/*
 	 *
 	 *The next several methods are called to handle various types of errors,
 	 *to put most of the errror-handling code in one place.
 	 *
 	 *This is not entirely complete, as there are still several places with error
 	 *handling code that needs to be moved here
 	 */
 	
 	public static void handleChatError(Exception e)
 	{
 		System.err.println("Error receiving chat message");
 		e.printStackTrace();
 	}
 	
 	public static void handleManipulatorActionError(Exception e)
 	{
 		System.err.println("Error receiving manipulator action");
 		e.printStackTrace();
 	}
 	
 	public static void handlePlayerAddedError(Exception e)
 	{
 		System.err.println("Error receiving player information");
 		e.printStackTrace();
 	}
 	
 	public static void handleGameStateTransferError(Exception e)
 	{
 		System.err.println("Error receiving game state information");
 		e.printStackTrace();
 	}
 }