/**
 *For Mao online; a concept of a floating hand-image that
 *							carries and acts on cards
 *and is controlled by the local player.
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/21/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.manipulators;
 
 import mao.util.*;
 import mao.draggable.*;
 import mao.game.*;
 import mao.online.*;
 import java.awt.Graphics2D;
 import java.awt.Point;
 import java.awt.event.*;
 import java.awt.geom.AffineTransform;
 import java.awt.image.BufferedImage;
 import javax.swing.Timer;
 import java.util.*;
 
 //Mastery aspects:
 //HL: Inheritance, polymorphism, encapsulation
 //SL: 2,3,4,6,8,9,10,14
 public class NativeManipulator extends Manipulator
 {
 	//How frequently, in miliseconds, ManipulatorActions
 	//that this
    //NativeManipulator has performed should be relayed
    //over the Internet
 	private static final int flushPeriod = 100;
 	
 	//The image representing the cursor of this manipulator
 	private ImageProxy cursorImg;
 	
 	//Contains the ManipulatorActions performed int he last flushPeriod miliseconds
 	private List<ManipulatorAction> performed;
 	
 	//Constructs a NativeManipulator on surface for the player with ID pID
 	public NativeManipulator(DraggableSurface surface, byte pID)
 	{
 		super(surface, pID);
 		//cursorImg = new StandardImage(56);
 		
 		//NTS: temp-ish -- implements the current system of
 		// Manipulator-color alterations
 		if(pID % 3 == 1)
 			cursorImg = new ImageProxy(StandardImage.DEEP_RED_MANIPULATOR);
 		else if(pID % 3 == 2)
 			cursorImg = new ImageProxy(StandardImage.PINK_MANIPULATOR);
 		else
 			cursorImg = new ImageProxy(StandardImage.BLUE_MANIPULATOR);
 		
 		
 		setLocation(new Point(0,0));
 		performed = new LinkedList<ManipulatorAction>();
 		startTimer();
 		
 		/*The next line blanks out the default mouse cursor, as the manipulator
 		 *essentially acts as a mouse cursor. However, this is disabled
 		 *as BufferedImages cannot be serialized, so the next line causes problems
 		 *
 		 *Note that, though the mouse cursor appears over the manipulators
 		 *in the program,
 		 *it can't be seen in screenshots, as the operating system's
 		 *standard screenshot
 		 *facility does not copy the mouse
 		 */
 		//getSurface().setCursor(DraggableUtils.getBlankCursor());
 	}
 	
 	//Performs a pick-up operation if not frozen, then creates
 	//and stores a PickUpAction representing this action
 	public long pickUp(boolean pileMode, boolean flip,
 									boolean straightening)
 	{
 		if(frozen())
 			return -1;
 		long checksum = super.pickUp(pileMode, flip, straightening);
 		if(-1 == checksum)
 			return -1;
 		PickUpAction p = new PickUpAction(getPlayerID(), getAbstractX(),
 									getAbstractY(), checksum, pileMode,
 										flip, straightening);
 		registerAction(p);
 		return checksum;
 	}
 	
 	//Performs a drop operation if not frozen, then creates
 	//and stores a DropAction representing this action
 	public long putDown(boolean pileMode, boolean flip,
 							boolean carefully, long randomSeed)
 	{
 		if(frozen())
 			return -1;

 		long checksum = super.putDown(pileMode, flip, carefully, randomSeed);
 		if(-1 == checksum)
 			return -1;
 		DropAction p = new DropAction(getPlayerID(), getAbstractX(),
 										 getAbstractY(), checksum, pileMode, 
 											 flip, carefully, randomSeed);
 		
 		registerAction(p);
 		return checksum;
 	}
 	
 	//Performs a flip operation if not frozen, then creates
 	//and stores a FlipAction representing this action
 	public long flip(boolean pileMode)
 	{
 		if(frozen())
 			return -1;
 		long checksum = super.flip(pileMode);
 		if(-1 == checksum)
 			return -1;
 		FlipAction p = new FlipAction(getPlayerID(), getAbstractX(),
 											 getAbstractY(), checksum,
 											 			pileMode);
 		registerAction(p);
 		return checksum;
 	}
 	
 	//Performs a rotate operation if not frozen, then creates
 	//and stores a RotateAction representing this action
 	public long rotate(byte amt, boolean pileMode)
 	{
 		if(frozen())
 			return -1;
 		long checksum = super.rotate(amt, pileMode);
 		if(-1 == checksum)
 			return -1;
 		RotateAction p = new RotateAction(getPlayerID(), getAbstractX(),
 											 getAbstractY(),checksum,
 											 			amt, pileMode);
 		registerAction(p);
 		return checksum;
 	}
 	
 	//Performs an init drag operation if not frozen, then creates
 	//and stores an InitDragAction representing this action
 	public long initiateDrag()
 	{
 		if(frozen())
 			return -1;
 		long checksum = super.initiateDrag();
 		if(-1 == checksum)
 			return -1;
 		InitDragAction p = new InitDragAction(getPlayerID(), 
 										getAbstractX(), getAbstractY(), 
 											checksum);
 		registerAction(p);
 		return checksum;
 	}
 	
 	//Performs a term-drag operation if not frozen, then creates
 	//and stores a TermDragAction representing this action
 	public long terminateDrag()
 	{
 		if(frozen())
 			return -1;
 		long checksum = super.terminateDrag();
 		if(-1==checksum)
 			return -1;
 		TermDragAction p = new TermDragAction(getPlayerID(), getAbstractX(),
 										 getAbstractY(), checksum);
 		registerAction(p);
 		return checksum;
 	}
 	
 	//Moves this NativeManipulator, then, if not frozen, creates
 	//and stores a MoveAction representing this action
 	public void setAbstractLocation(Point p)
 	{
 		super.setAbstractLocation(p);
 		if(!frozen()) //can move mouse, but won't be reflected on
 						//other players' screens
 			registerAction(new MoveAction(getPlayerID(),
 								getAbstractX(), getAbstractY()));
 	}
 	
 	//Draws all cards held by the NativeManipulator
 	//in order below the Manipulator,
 	//then draws the cursor image over it
 	public void draw(Graphics2D g)
 	{
 		if(dragging())
 		{
 			dragged.setAbstractLocation(
 						getHotspot().minus(draggedOffset));
 			getSurface().handleDraw(dragged, g);
 		}
 			
 		for(DraggableComponent c : getCards())
		{
			c.setAbstractLocation(getHotspot());
		}
		
		Stack<DraggableComponent> s = new Stack<DraggableComponent>();
 		for(DraggableComponent c : getCards())
 			s.push(c);
 		
 		
 		while(!s.empty())
 			getSurface().handleDraw(s.pop(),g);
 		 	
 		AffineTransform translater = AffineTransform.getTranslateInstance(
 													getAbstractLocation().getX(),
 													getAbstractLocation().getY());
 		 
 		g.drawImage(cursorImg.getImage(), translater, null);
 	}
 	
 	//Creates a timer which sends all recently-done actions over the Internet
 	//every flushPeriod seconds
 	private void startTimer()
 	{
 		ActionListener l = new ActionListener()
 		{
 			public void actionPerformed(ActionEvent e)
 			{
 				flush();
 			}
 		};
 		new Timer(flushPeriod, l).start();
 	}
 	
 	//Sends all ManipulatorActions recorded in performed over the Internet
 	protected void flush()
 	{
 			
 		int l = performed.size();
 		try
 		{
 			while(!performed.isEmpty())
 			{
 				GameClient.getInstance().sendMessage(Prefixes.ACTION_PERFORMED,
 												performed.remove(0).toByteArray());
 			}
 		}
 		catch(java.io.IOException e)
 		{
 			//I don't know if I should do anything here
 		}
 		catch(NoSuchElementException e)
 		{
 			e.printStackTrace();
 		}
 	}
 	
 	//Marks the ManipulatorAction a to send over the Internet at the next flush
 	public void registerAction(ManipulatorAction a)
 	{
 		performed.add(a);
 	}
 	
 	//Returns the image representing this manipulator action
 	public ImageProxy getCursorImg()
 	{
 		return cursorImg;
 	}
 }