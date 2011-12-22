/**
 *For Mao online; a draggable component representing a playing card
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/16/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.game;
 
 import mao.draggable.*;
 import mao.util.*;
 import java.awt.*;
 import java.awt.geom.*;
 import javax.swing.Icon;
 import java.util.Random;
 
 
 //Mastery aspects shown:
 //Higher level: Polymorphism, inheritance, encapsulation, parsing a data stream
 //Standard level: 1,2,3,8,9,10,14

 public class Card extends DraggableComponent
 {
 	//Playing cards consist of a Rank and a Suit. These
 	//two enums create a convenient and beautiful way for dealing with those,
 	//passing them around, iterating through them, etc
 	//
 	//(Cards are actually one of the examples in the official Java tutorial on enums.)
	public static enum Rank {ACE,KING,QUEEN,JACK,TEN,NINE,EIGHT,SEVEN,SIX,FIVE,
				FOUR,THREE,TWO}
    public static enum Suit { CLUBS, SPADES, HEARTS, DIAMONDS }

	//Holds the default image for card backs
	private static ImageProxy defaultBackImage = new ImageProxy(StandardImage.RED_BACK);

 	private ImageProxy front; //References the standard image representing the card's front
 	private ImageProxy back; //References the standard image representing the card's back
 	private ImageProxy currentImage; //Which of the above images is currently showing
 	private boolean faceUp; //Whether this card is face up
 	private byte theta; //How much this card has been rotated, stored in multiples of
 								//pi/90 radians
 	 	
 	private Rank rank; //The rank of this card
 	private Suit suit; //The suit of this card
 	
 	//Helper method that uniquely converts a rank and suit into a number
 	//by creating a mixed-base number where the right digit takes four values
 	//and repreesents the suit, and the left digit takes 13 values and represents the rank
 	//This is used to create a good way to index the image of a specific card
 	protected static byte toIndex(Rank rank, Suit suit)
 	{
 		return (byte)(rank.ordinal() * 4 + suit.ordinal());
 	}
 	
 	//Given an index like one created by toIndex, returns the rank of the
 	//card it represents
 	protected static Rank rankFromIndex(byte index)
 	{
 		return Rank.values()[index/4];
 	}
 	
 	//Given an index like one created by toIndex, returns the suit of the
 	//card it represents
 	protected static Suit suitFromIndex(byte index)
 	{
 		return Suit.values()[index%4];
 	}
 	
 	//Creats a new card with the given rank and suit
 	public Card(Rank rank, Suit suit)
 	{
 		this.rank = rank;
 		this.suit = suit;
 			
 		faceUp = true;
 		currentImage = front;
 		theta = 0;
 		
 		initializeImages();
 	}
 	
 	//Creates a card as a copy of a card that exists elsewhere
 	//serialized is a byte array that has been created by this class's "serialize" method
 	public Card(byte[] serialized)
 	{
 		super(serialized);
 	}
 	
 	//Finds which card images this card should use
 	private void initializeImages()
 	{
 		front = ImageProxy.getCardImage(rank, suit);
 		back = new ImageProxy(StandardImage.RED_BACK);
 		
 		if(faceUp)
 			currentImage = front;
 		else
 			currentImage = back;
 	}
 	
 	//Expresses cards in a form similar to "TWO of SPADES"
 	public String toString()
 	{
 		return rank + " of " + suit;
 	}
 	
 	//Creates a card with a random suit and rank
 	public static Card randomCard(Random r)
 	{
 		return new Card(Rank.values()[r.nextInt(Rank.values().length)],
 			Suit.values()[r.nextInt(Suit.values().length)]);
 	}
 	
 	//Flips this card over
 	public void flip()
 	{
 		faceUp = !faceUp;
 		if(faceUp)
 			currentImage = front;
 		else
 			currentImage = back;
 	}
 	
 	//Rotates this card clockwise by theta,
 	//which is in multiples of pi/90
 	//
 	//Remember that theta can be negative
 	public void rotate(byte theta)
 	{
 		this.theta = degreeSum(this.theta, theta);
 	}
 	
 	//Sets this card to the position of
 	//having been rotated by theta from the upright position.
 	//Theta is in multiples of pi/90
 	//
 	//Remember that theta can be negative
 	public void setRotation(byte theta)
 	{
 		this.theta = theta;
 	}
 	
 	//Returns an object of java.awt.geom.Area representing the region
 	//this card covers
 	public Area getArea()
 	{
 		return new Area(computeBounds());
 	}
 	
 	///Returns a rotatable rectangle representing the area this card covers
 	private RotatableRectangle computeBounds()
 	{
 		return new RotatableRectangle(getAbstractX(), getAbstractY(), 
 				currentImage.getImage().getWidth(null),
 				currentImage.getImage().getHeight(null), theta*Math.PI/MAX_ANGLE);
 	}
 	
 	//Returns the icon representative of this card
 	public Image getIcon()
 	{
 		if(currentImage == front)
 			return ImageProxy.getCardIcon(rank, suit).getImage();
 		else
 			return StandardImage.getImage(StandardImage.RED_BACK_ICON); //NTS: change
 	}

	//Draws this card by drawing its image at a rotation
 	public void draw(Graphics2D g)
 	{
 		Vector2D centroid = computeBounds().getCentroid();
 		AffineTransform rotater = AffineTransform.getRotateInstance(theta*Math.PI/MAX_ANGLE,
 			centroid.getX(), centroid.getY());
 		rotater.translate(getAbstractX(),getAbstractY());
 		g.drawImage(currentImage.getImage(), rotater, null);
 	}
 	
 	//Returns the centroid of the region this card covers
 	public Vector2D getCentroid()
 	{
 		return computeBounds().getCentroid();
 	}
 	
 	
 	//Returns a byte array containing a compact specification of this card,
 	//including its ID, position, rotation, whether it's face up or face down, and rank
 	//and suit
 	public byte[] serialize()
 	{
 		byte[] bytes = new byte[11];
 		BinaryUtils.storeAsBytes(getID(), bytes, 0, 4);
 		BinaryUtils.storeAsBytes(getAbstractX(), bytes, 4, 2);
 		BinaryUtils.storeAsBytes(getAbstractY(), bytes, 6, 2);
 		bytes[8] = theta;
 		bytes[9] = BinaryUtils.booleanToBit(faceUp);
 		bytes[10] = toIndex(rank, suit);
 		return bytes;
 	}
 	
 	//Inverse of the above method -- initializes a card as a copy
 	//of a Card created elsewhere that has been serialized.
 	//
 	//The DraggableComponent class will have already read the ID when this is called
 	protected void read(byte[] serialized)
 	{
 		setAbstractX(BinaryUtils.asBigEndianInt(serialized, 4, 2));
 		setAbstractY(BinaryUtils.asBigEndianInt(serialized, 6, 2));
 		setRotation(serialized[8]);
 		faceUp = BinaryUtils.bitToBoolean(serialized[9]);
 		this.rank = rankFromIndex(serialized[10]);
 		this.suit = suitFromIndex(serialized[10]);
 		
 		initializeImages();
 	}
 }