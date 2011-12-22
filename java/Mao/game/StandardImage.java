/**
 *For Mao online; holds images that come packaged with the game; makes them more efficient
 *					to serialize
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/20/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
  package mao.game;
  
  import java.awt.*;
  import java.io.*;
  import javax.imageio.ImageIO;
  import java.awt.GraphicsConfiguration;
  import java.awt.image.*;
  
  
  //Mastery aspects:
  //HL: encapsulation
  //SL: 1,3,6,8,9,10,14
  
public class StandardImage
{
	//holds the standard images
	private static Image[] images = new Image[256];
	
	/*
	 *Following variables contain the index of
	 *images other then cards
	 */
	public static final int BLUE_BACK = 104;
	public static final int BLUE_BACK_ICON = 105;
	public static final int RED_BACK = 106;
	public static final int RED_BACK_ICON = 107;
	public static final int DEEP_RED_MANIPULATOR = 108;
	public static final int PINK_MANIPULATOR = 109;
	public static final int BLUE_MANIPULATOR = 110;
	public static final int CARD_PILE = 111;
	public static final int BLANK_IMAGE = 112;
	
	/*
	 *When this class is loaded, reads the images, storing them into the images array
	 */
	static
	{
		/*
		 *Card front images (including jokers) are stored in 1.png, 2.png,...,54.png
		 *in the mao/images folder
		 *
		 *The number of a card's filename is the same as returned by Card.toIndex for that card
		 *
		 *Icons for those cards are stored in 1_icon.png, 2_icon.png, etc
		 */
		 try
		 {
			for(int i = 0; i < 52; i++)
			{
				images[2*i] = ImageIO.read(new File("mao/images/"+(i+1)+".png"));
				images[2*i+1] = ImageIO.read(new File("mao/images/"+(i+1)+"_icon.png"));
			}
		
			//two options for card backs
			images[104] = ImageIO.read(new File("mao/images/b1fv.png"));
			images[105] = ImageIO.read(new File("mao/images/b1fv_icon.png"));
			images[106] = ImageIO.read(new File("mao/images/b2fv.png"));
			images[107] = ImageIO.read(new File("mao/images/b2fv_icon.png"));
			images[108] = ImageIO.read(new File("mao/images/hand_red_1.png"));
			images[109] = ImageIO.read(new File("mao/images/hand_pink_1.png"));
			images[110] = ImageIO.read(new File("mao/images/hand_blue_1.png"));
			images[111] = ImageIO.read(new File("mao/images/card_pile.png"));
			images[112] = ImageIO.read(new File("mao/images/blank.png"));
		 }
		 catch(IOException e)
		 {
		 	e.printStackTrace();
		 	javax.swing.JOptionPane.showMessageDialog(null,
		 		"There was trouble loading the images.");
		 	System.exit(1);
		 }
	}
	
	
	//this class is uninstantiable
	private StandardImage()
	{
		
	}
	
	//returns the image associated with the given index
	public static Image getImage(int idx)
	{
		return images[idx];
	}
    	
}