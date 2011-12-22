/**
 *For Mao online; transfers the current state of the game to newly
 *		connected players
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 2/12/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.game;
 
 import mao.draggable.DraggableSurface;
 import mao.online.*;
 import java.net.*;
 import java.util.*;
 import java.io.*;
 
//Mastery aspects shown:
//Higher level: Polymorphism, encapsulation
//Standard level: 2,3,6,8,9,14


/*
 *A simple way to transfer the state of a card game from one computer
 *to another using Java's default serialization capabilities
 */
 public class CardGameState implements TransferableState
 {
 	private Table table; //The Table the game takes place on
 	private LinkedHashMap<Byte,Player> players; //A list of all players
 	private byte localPlayerID; //The ID of the player about to be added
 	
 	//Initializes this CardGameState
	public CardGameState(byte localID)
 	{
 		table = Game.getTable();
 		players = Game.getPlayers();
 		localPlayerID = localID;
 	}
 	
 	//Sets the state of the game on the local computer to match
 	//the one stored in this object
 	public void loadState()
 	{
 		Game.setServer(false);
 		Game.setTable(table);
 		CardGamePlayer.getInstance().setTable(table);
 		CardGamePlayer.getInstance().addChatBox();
 		Game.setLocalPlayerID(localPlayerID);
 		for(Player p : players.values())
 		{
 			Player pCop = Player.copyRemotePlayer(p);
 			Game.addPlayer(pCop);
 		}
 	}
 	
 	//Serializes itself over the ObjectOutputStream
 	public void sendSelf(ObjectOutputStream oos) throws IOException
 	{
 		oos.write(Prefixes.GAME_STATE_TRANSFER);
 		oos.writeObject(this);
 		oos.flush();
 	}
 }