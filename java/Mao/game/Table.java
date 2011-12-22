/**
 *For Mao online; a DraggableSurface acting as a card game table
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/19/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.game;
 
 import mao.draggable.*;
 import mao.manipulators.*;
 import java.awt.*;
 import java.awt.geom.*;
 
 //Mastery aspects
 //HL: Inheritance, polymorphism
 //SL: 2,3,6,8,9,10,14
 
 public class Table extends DraggableSurface
 {
 	//Constructor merely sets the background to casino green,
 	//and calls super
 	public Table()
 	{
 		super();
 		setBackground(Color.GREEN);
 	}
 	
 	//For a description of the sector system, see the general notes at the beginning
 	//of C1
 	//
 	//This sector size returned is four times the dimensions of the card images,
 	//striking a balance between finding cards based on position easily, and 
    //having each card in few sectors
 	public Dimension getSectorSize()
	{
		return new Dimension(152, 184);
	}	
	
	//In addition to drawing the cards on the table normally as in
	//DraggableSurface, it has to draw the players' manipulators
	//with icons.
	//
	//The manipulators are always drawn anew rather than being stored in a buffer;
	//that's why paintComponent is overriden instead of draw
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2D = (Graphics2D)g;
			
		AffineTransform oldTransform = g2D.getTransform();
		g2D.transform(makeScaleTransform());
			
		for(Player p : Game.getPlayers().values())
		{
			handleDraw(p.getManipulator(),g2D);
			handleDraw(new CardIconDisplay(p.getManipulator()), g2D);
		}
		
		g2D.setTransform(oldTransform);
	}
 }