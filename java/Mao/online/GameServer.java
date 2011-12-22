/**
 *For Mao online; a server class that sends and
 *	receives messages from the other players, and
 *	propagates received messages
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/1/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;
 
 import mao.util.*;
 import mao.game.Game;
 import java.util.*;
 import java.net.*;
 import java.io.*;
 
 import javax.swing.*;
 
 //Master aspects
 //HL: Encapsulation, inheritance, polymorphism
 //SL: 1,2,3,4,6,8,9,10,14
 

 /*
  *This is a singleton class -- there can be only
  *one instance of GameServer
  *Furthermore, GameServer extends GameClient
  *and replaces its functionality
  *
  *If you have a GameServer, you can't also have a different
  *	 GameClient
  */

 /*
  *Important note: Apparently, there are buffering issues that cause
  *errors when using different stream objects to communicate over a socket,
  *so the object i/o streams associated with the socket are stored for reuse.
  */
public class GameServer extends GameClient
{
	//Stores the input and output sream of all connections
	private List<Pair<ObjectInputStream, ObjectOutputStream>> connections =
		new ArrayList<Pair<ObjectInputStream, ObjectOutputStream>>();
	
	
	//Used to listen for connections from other players
	private ServerSocket server; 
	
	//Returns the one instance of GameServer, or makes a new one over the port
	//if it doesn't exist
	public static GameClient makeInstance(int port) throws IOException
	{
		if(null == instance)
			instance = new GameServer(port);
		
		return instance;
	}
	
	//Initializes a GameServer listening over the port
	private GameServer(int port) throws IOException
	{
		server = new ServerSocket(port);
		listenForConnections();
	}
	
	//Sends the given message consisting of prefix+message to all connected
	//clients, and itself for message types where the MessageHandler handles
	//it even if generated locally
	public synchronized void sendMessage(byte prefix, byte[] message)
														 throws IOException
	{		
		if(handlers[prefix].shouldSendToSelf())
			sendToSelf(prefix, message);
		propagateMessage(prefix, message, null);
		dispatch();
	}
	
	//Sends the given message consisting of prefix+message to all connected
	//clients,
	private synchronized void propagateMessage(byte prefix,
				byte[] message, ObjectOutputStream exclusion) throws IOException
	{
		for(Pair<ObjectInputStream,ObjectOutputStream> p : connections)
		{
			ObjectOutputStream oos = p.getSecond();
			if(oos == exclusion)
				continue;
				
			oos.write(prefix);
			oos.write(message);
		}
	}
	
	//Flushes all written messages
	private void dispatch() throws IOException
	{
		for(Pair<ObjectInputStream,ObjectOutputStream> p : connections)
			p.getSecond().flush();
	}
	
	//Receives new connections from other players
	private void listenForConnections()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					while(true)
					{
						Socket conn = server.accept();
						System.err.println("received conn from " + conn);
						
						openTentativeReceiveThread(conn);
					}
				}
				catch(IOException e)
				{
					System.err.println("Error establishing connection.");
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	//After receiving a connection from another player, does
	//all needed to bring that player into the game
	/*
 	*
 	*Due to strange errors, JOIN_REQUEST handling is currently
 	*partially disabled;
 	*GameServer currently automatically accepts all join requests
 	*/
	private void openTentativeReceiveThread(Socket connArg)
	{
		final Socket conn = connArg;
		new Thread()
		{
			public void run()
			{
				try
				{
					ObjectOutputStream out =
							new ObjectOutputStream(conn.getOutputStream());
					out.flush();
					ObjectInputStream in =
						new ObjectInputStream(conn.getInputStream());
					
					String name = in.readUTF();
					
 					//When adding a player,
 					//that player will need to know, and all other
 					//players will need to wait while he's addeds
					out.write(Prefixes.JOIN_REQUEST_ACCEPTED);
					sendMessage(Prefixes.PLAYER_BEING_ADDED, new byte[0]);
					connections.add(new Pair(in, out));
					runReceiveThread(in, out);
					Game.readyRemotePlayer(out, name);
				}
				catch(Exception e)
				{
					System.err.println("Error receiving from client.");
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	//Runs a permanent thread listening for messages from the connection
	//represented by inArg and outArg
	private void runReceiveThread(ObjectInputStream inArg,
										ObjectOutputStream outArg)
	{
		final ObjectInputStream in = inArg;
		final ObjectOutputStream out = outArg;
		new Thread()
		{
			public void run()
			{
				try
				{
					while(true)
					{
						int prefix = in.read();
						synchronized(GameServer.this)
						{
							byte[] message = handlers[prefix].handleMessage(in);
							
							propagateMessage((byte)prefix, message, out);
							dispatch();
						}
					}
				}
				catch(IOException e)
				{
					System.err.println("Error receiving from client.");
					e.printStackTrace();
				}
			}
		}.start();
	}
}