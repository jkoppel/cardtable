/**
 *For Mao online; displays icons of all the cards held by a player's
 *				manipulator, of a card pile with or the
 *				number of cards if too many
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/23/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */


package mao.manipulators;

import mao.game.*;
import mao.draggable.*;
import mao.util.*;
import mao.winind.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.AffineTransform;

//Master aspects:
//HL: encapsulation, inheritance, polymorphism
//SL: 2,3,4,6,8,9,14


//As is explained in the section on the WindowIndependent system,
//a rotatable anchor component is a component that has constant
// orientation,
//but whose position rotates if the screen rotates perspective. They draw
//relative to an "anchor" point
//
//However, this class wants to always draw below the Manipulator,
//no matter what, and
//will thus need to work around the WindowIndependentContainer class
//and its implementation
//of rotations for perspetives. It does this by setting its anchor
//so that the anchor will be rotated
//to where it wants to be after drawng
//
//Note that much of this class is currently unnecessay, as having the table draw
//at different angles fro different players is unimplemented 
//(though much of the supporting code is)

public class CardIconDisplay extends RotatableAnchorComponent
{
	//The Manipulator this class will be displaying icons for
	private Manipulator parent;
	
	//How far below the Manipulator's centroid this class should draw
	private static final Vector2D fromCentroidToAnchor =
													new Vector2D(0,80);
	
	//How many cards to display before switching
	//to displaying number of cards held instead
	//of icons of individual cards
	private static final int maxDisplay = 5;
	
	//How tall this display is drawn
	private static final int height = 30;
	
	//Initializes this CardIconDisplay with the given parent Manipulator
	public CardIconDisplay(Manipulator parent)
	{
		this.parent = parent;
		updateAnchor();
	}
	
	//Calculates the point it should set its anchor to so that the anchor will
	//be rotated to a point below the Manipulator
	public void updateAnchor()
	{
		if(parent.isEmpty())
			return; // won't display anything anyway
		
		Vector2D centroid = parent.getCards().first().getCentroid();
		
		Vector2D anchor = centroid.plus(fromCentroidToAnchor);
		
		//We want the anchor to be below the manipulator post-rotation, so
		//we have to rotate it the other way
		AffineTransform rotater = new AffineTransform();
		rotater.rotate(-parent.getSurface().getRotationAngle(),
										centroid.getX(), centroid.getY());
		
		rotater.transform(anchor, anchor);
		
		setAnchor(anchor);
	}
	
	//Draws the icons representing the manipulator's cards
	public void draw(Graphics2D g)
	{
		AffineTransform translater;
		
		if(parent.isEmpty())
			return;
			
			
		int width = maxDisplay*StandardImage.getImage(
									StandardImage.BLUE_BACK_ICON).getWidth(null);
		BufferedImage img = new BufferedImage(width, height,
												 BufferedImage.TYPE_INT_ARGB);
		Graphics2D h = img.createGraphics();
		
		//draws transparent background
		h.setColor(new Color(255,0,0,0));
		h.fillRect(0,0,width,height);
		
		/*
		 *Draws cardPile image + number of cards
		 */
		if(parent.getCards().size() > maxDisplay) 
		{
			Image cardPile = StandardImage.getImage(StandardImage.CARD_PILE);
			translater = AffineTransform.getTranslateInstance(
											width/2-cardPile.getWidth(null),0);
			h.drawImage(cardPile, translater, null);
			String number = " x " + Integer.toString(parent.getCards().size());
			h.setColor(Color.BLACK);
			h.drawString(number, width/2,height);
		}
		else //Draws icons for each card
		{
			translater = new AffineTransform();
			for(DraggableComponent c : parent.getCards())
			{
				Image icon = c.getIcon();
				h.drawImage(icon,translater,null);
				translater.translate(icon.getWidth(null),0);
			}
		}
		
		translater = AffineTransform.getTranslateInstance(
									getAnchor().getX() - width/2,
						 			getAnchor().getY()-height/2);
		g.drawImage(img,translater,null);
	}
}