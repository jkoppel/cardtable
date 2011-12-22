/**
 *For Mao online; immutable class that seemlessly lets standard
 *images be represented by an integer, while
 *leaving in the opportunity to use custom images
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/23/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
package mao.game;

import java.awt.Image;
import java.io.Serializable;

//Mastery aspects:
//Higher level: Encapsulation
//Standard level: 2,3,4,8,9,10,14

public class ImageProxy implements Serializable
{
	//If this ImageProxy represents a custom image, holds that image
	private Image customImage; 
	
	//If this ImageProxy represents a standard image,
	//holds that image's index
	private int standardImageIdx; 
	
	//Creates an ImageProxy representing a standard image
	public ImageProxy(int idx)
	{
		customImage = null;
		standardImageIdx =idx;
	}
	
	//Creates an ImageProxy representing a custom image
	public ImageProxy(Image img)
	{
		customImage = img;
		standardImageIdx = -1;
	}
	
	//Returns whether this represents a custom image
	public boolean isCustom()
	{
		return customImage != null;
	}
	
	//Returns the image this image proxy represents
	public Image getImage()
	{
		if(isCustom())
			return customImage;
		else
			return StandardImage.getImage(standardImageIdx);
	}
	
	//Returns an ImageProxy representing the standard image for the card
	//with the givenr ank and suit
	public static ImageProxy getCardImage(Card.Rank rank, Card.Suit suit)
	{
		int imageIdx = Card.toIndex(rank, suit)*2;
		return new ImageProxy(imageIdx);
	}
	
	//Returns an ImageProxy representing the standard image for the icon of the
	//card with the givenr ank and suit
	public static ImageProxy getCardIcon(Card.Rank rank, Card.Suit suit)
	{
		int imageIdx = Card.toIndex(rank, suit)*2+1;
		return new ImageProxy(imageIdx);
	}
}