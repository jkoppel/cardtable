/**
 *For Mao online; a concept of a floating hand-image that carries and acts on cards
 *and is controlled by a remote player player.
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
 import java.awt.*;
 import java.awt.geom.AffineTransform;
 import java.awt.image.BufferedImage;
 import java.util.Stack;
 
 
 //Mastery aspects:
 //HL: Inheritance, polymorphism, encapsulation
 //SL: 2,3,4,6,8,9,10,14
 
 public class AlienManipulator extends Manipulator
 {
 	private ImageProxy handImg; //holds the image of this cursor
 	
 	//Creates a new AlienManipulator on the given draggable surface
 	//for the player with ID pID
 	public AlienManipulator(DraggableSurface surface, byte pID)
 	{
 		super(surface, pID);
 		
 		//implements the current system of Manipulator-color alterations
 		//First player gets red, second gets pink, third gets blue, then cycle repeats
 		if(pID % 3 == 1)
 			handImg = new ImageProxy(StandardImage.DEEP_RED_MANIPULATOR);
 		else if(pID % 3 == 2)
 			handImg = new ImageProxy(StandardImage.PINK_MANIPULATOR);
 		else
 			handImg = new ImageProxy(StandardImage.BLUE_MANIPULATOR);
 			
 		setLocation(new Point(0,0));
 	}
 	
 	//Creates an AlienManipulator that will mirror the given NativeManipulator
 	//on a remote computer
 	public AlienManipulator(NativeManipulator rem)
 	{
 		super(rem.getSurface(), rem.getPlayerID());
 		cards = rem.getCards();
 		handImg = rem.getCursorImg();
 		setAbstractLocation(rem.getAbstractLocation());
 	}
 	
 	//Overrides method in Manipulator, adding respect for being frozen
 	public long pickUp(boolean pileMode, boolean flip, boolean straightening)
 	{
 		if(frozen())
 			return -1;
 		return super.pickUp(pileMode, flip, straightening);
 	}
 	
 	//Overrides method in Manipulator, adding respect for being frozen
 	public long putDown(boolean pileMode, boolean flip, boolean carefully,
 																long randomSeed)
 	{
 		if(frozen())
 			return -1;
 		return super.putDown(pileMode, flip, carefully, randomSeed);
 	}
 	
 	//Overrides method in Manipulator, adding respect for being frozen
 	public long flip(boolean pileMode)
 	{
 		if(frozen())
 			return -1;
 		return super.flip(pileMode);
 	}
 	
 	//Overrides method in Manipulator, adding respect for being frozen
 	public long rotate(byte amt, boolean pileMode)
 	{
 		if(frozen())
 			return -1;
 		return super.rotate(amt, pileMode);
 	}
 	
 	//Overrides method in Manipulator, adding respect for being frozen
 	public long initiateDrag()
 	{
 		if(frozen())
 			return -1;
 		return super.initiateDrag();
 	}
 	
 	//Overrides method in Manipulator, adding respect for being frozen
 	public long terminateDrag()
 	{
 		if(frozen())
 			return -1;
 		return super.terminateDrag();
 	}
 	
 	//Draws all cards held by the AlienManipulator in order below the Manipulator,
 	//then draws the cursor image over it
 	public void draw(Graphics2D g)
 	{
 		//Draws dragged card
 		if(dragging())
 		{
 			dragged.setAbstractLocation(getHotspot().minus(draggedOffset));
 			getSurface().handleDraw(dragged, g);
 		}
 			
 		//Moves all carried cards to below the manipulator
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
 		 
 		g.drawImage(handImg.getImage(), translater, null);
 	}
 }