/**
 *For Mao online; a class that can send and
 *	receive messages from the server,
 *	 and delegate their processing
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 11/30/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.online;
 
 import java.net.*;
 import java.io.*;
 
 import mao.game.*;
 
 //Mastery aspects:
 //HL: Encapsulation
 //SL: 1,2,3,4,6,8,9,10,14
 
 
 /*
  *This is a singleton class -- there can be only one
  *instance of GameClient
  */
 
  /*
  *Important note: Apparently, there are buffering
  *issues that cause
  *errors when using different stream objects to communicate
  *over a socket,
  *so the object i/o streams associated with the socket are
  *stored for reuse.
  */
public class GameClient
{
	
	//The port the game runs over
	public static final int DEFAULT_PORT = 7777;
	
	//Holds that one instance
	protected static GameClient instance;
	
	//Maps message prefixes to classes handling them
	protected static MessageHandler[] handlers =
							new MessageHandler[256];
	
	//holds the connection to the server
	private Socket conn; 
		
	//wraps conn's input stream
	private ObjectInputStream ois; 
		
	//wraps conn's output stream
	private ObjectOutputStream oos; 


	/*
	 *Initializes handlers with the various types
	 *of message handlers
	 */
	static
	{
		addHandler(Prefixes.GAME_STATE_TRANSFER, new GameStateTransferHandler());
		addHandler(Prefixes.JOIN_REQUEST_ACCEPTED, new JoinRequestAcceptedHandler());
		addHandler(Prefixes.PLAYER_BEING_ADDED, new PlayerBeingAddedHandler());
		addHandler(Prefixes.PLAYER_ADDED, new PlayerAddedHandler());
	}
	
	//Returns the one instance of GameClient
	public static GameClient getInstance()
	{
		return instance;
	}
	
	//Returns the one instance of GameClient,
	//or makes one to the given host and port, connecting with the given handle
	//if it's null
	public static GameClient makeInstance(String host, int port,
										 String handle) throws IOException
	{
		if(null == instance)
			instance = new GameClient(host, port, handle);
		
		return instance;
	}
	
	//Returns the one instance of GameClient,
	//or makes one to the given host and port,
	//connecting with the given handle
	//if it's null
	public static GameClient makeInstance(InetAddress host, int port,
									String handle) throws IOException
	{
		if(null == instance)
			instance = new GameClient(host, port, handle);
		
		return instance;
	}
	
	//Destroys the one instance of GameClient
	public static void destroyInstance()
	{
		instance = null;
	}
	
	//Begins directing all incoming messages whose type is
	//specified by prefix
	//to handler
	public static void addHandler(byte prefix, MessageHandler handler)
	{
		handlers[prefix] = handler;
	}
	
	//Initializes a GameClinet over port to the given host,
	//requesting to join using the given handle
	private GameClient(String host, int port, String handle)
												throws IOException
	{
		conn = new Socket(host, port);
		oos = new ObjectOutputStream(conn.getOutputStream());
		oos.flush();
		ois = new ObjectInputStream(conn.getInputStream());
		
		oos.writeUTF(handle);
		oos.flush();
		
		runReceiveThread();
	}
	
	//Initializes a GameClinet over port to the given host,
	//requesting to join using the given handle
	private GameClient(InetAddress host, int port, String handle)
														throws IOException
	{
		
		conn = new Socket(host, port);
		oos = new ObjectOutputStream(conn.getOutputStream());
		oos.flush();
		ois = new ObjectInputStream(conn.getInputStream());
		oos.writeUTF(handle);
		oos.flush();
		
		runReceiveThread();
	}
	
	//Required for subclasses with constructors that don't call
	//the super constructor
	protected GameClient()
	{
		
	}
	
	//Sends the message stored in prefix+message to the server
	public synchronized void sendMessage(byte prefix, byte[] message)
														throws IOException
	{
		if(handlers[prefix].shouldSendToSelf())
			sendToSelf(prefix, message);
		oos.write(prefix);
		oos.write(message);
		oos.flush();
	}
	
	//Has the appropriate message handlers process the message
	//This avoids having to have separate mechanisms to perform
	//actions being done by the player on this computer
	//and other players over the Internet
	protected synchronized void sendToSelf(byte prefix, byte[] message)
														throws IOException
	{
		InputStream str = new ByteArrayInputStream(message);
		handlers[prefix].handleMessage(str);
	}
	
	//Runs a thread receiving messages from the server
	private void runReceiveThread()
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					while(true)
					{
						int prefix = ois.read();
						handlers[prefix].handleMessage(ois);
					}
				}
				catch(IOException e)
				{
					System.err.println("Error receiving from server.");
					e.printStackTrace();
				}
			}
		}.start();
	}
}