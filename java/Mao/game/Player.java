/**
 *For Mao online; holds information about a player
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/24/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.game;
 
 import mao.manipulators.*;

//Mastery aspects:
//HL: Encapsulation
//SL: 2,3,4,8,9,10

public class Player implements java.io.Serializable
{
	private byte id; //this Player's ID
	private String name; //player's name
	
	protected boolean isNative; //whether this Player object
								//represents the player playing on this client
	
	//Each ManipulatorAction by a given player has a unique ID;
	//this tracks the next unused ID for that purpose
	private int nextManipulatorActionID;
	
	//The manipulator of this player
	private Manipulator manipulator;
	
	//Creates a copy of rem, modifying the isNative field and
	//replacing NativeManipulators
	//with AlienManipulators as necessary
	public static Player copyRemotePlayer(Player rem)
	{
		Player loc = new Player();
		loc.id = rem.getID();
		loc.name = rem.getName();
		loc.isNative = false;
		loc.nextManipulatorActionID = rem.getNextManipulatorActionID();
		if(rem.isNative)
			loc.manipulator = new AlienManipulator((NativeManipulator)rem.getManipulator());
		else
			loc.manipulator = rem.getManipulator();
		
		return loc;
	}
	
	//Empty constructor used by the above method
	private Player()
	{
		
	}
	
	//Initializes a new player from the given information
    public Player(byte id, String name, boolean isNative, int nextManipulatorActionID)
    {
    	this.id = id;
    	this.name = name;
    	this.isNative = isNative;
    	this.nextManipulatorActionID = nextManipulatorActionID;
    	
    	if(isNative)
    		manipulator = new NativeManipulator(Game.getTable(), id);
    	else
    		manipulator = new AlienManipulator(Game.getTable(), id);
    }
    
    //Returns this player's manipulator
    public Manipulator getManipulator()
    {
    	return manipulator;
    }
    
    
    //returns whether this object represents the player playing on this client
    public boolean isNative()
    {
    	return isNative;
    }
    
    //retursn this players' name
    public String getName()
    {
    	return name;
    }
    
    //returns this player's ID
    public byte getID()
    {
    	return id;
    }
    
    //This is currently unused, but will be used when I implement showing the table
    //at different perspectives for different players. It returns the angle
    //that the table should be shown at for this player
    public double getRotationAngle()
    {
    	return 2*Math.PI * ((double)Game.getPlayerIndex(this) / Game.getPlayers().size());
    }
    
    //Used to claim a unique ID for a manipulator action,
    public synchronized int nextManipulatorActionID()
    {
    	int i = nextManipulatorActionID;
    	nextManipulatorActionID++;
    	return i;
    }
    
    //Simply returns the nextManipulatorActionID without incrementing it
    public int getNextManipulatorActionID()
    {
    	return nextManipulatorActionID;
    }
}