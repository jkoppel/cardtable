/**
 *For Mao online; implements the controls for the game
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/18/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.game;
 
 import mao.winind.*;
 import mao.manipulators.*;
 import mao.draggable.*;
 import mao.online.*;
 import java.awt.event.*;
 
 import javax.swing.event.MouseInputAdapter;
 
 //Mastery aspects:
 //Higher level: Polymorphism, Encapsulation
 //Standard level: 2,3,4,5,8,9,14
 
 public class Controls extends MouseInputAdapter implements
 				WindowIndependentMouseEventHandler, java.io.Serializable
 {
 	//How much in a single dimension cards can randomly move upon drops
 	public static final int DROP_CHAOS = 10; 
 	
 	//How much to rotate a card on every movement of the mouse wheel, expressed in
 	//multiples of pi/90. So this value is equivalent to 6 degrees
 	public static final byte ROTATION_AMOUNT = 3;
 	
 	private NativeManipulator manipulator; //The manipulator of the local player that
 	                                       //is being controlled
 	
 	//Initializes this oobject
 	public Controls(NativeManipulator m)
 	{
 		manipulator = m;
 		GameClient.addHandler(Prefixes.ACTION_PERFORMED, 
 						new ManipulatorActionMessageHandler());
 	}
 	
 	//Holds a reference to the container that is sending this Controls object
 	//its mouse events
 	private WindowIndependentContainer parent;
 	
 	//Changes the parent
 	public void setParent(WindowIndependentContainer parent)
 	{
 		this.parent = parent;
 	}
 	
 	//Handles mouse clicks of all three types
 	public void mouseClicked(MouseEvent e)
 	{
 		//Updates the position of the NativeManipulator
 		mouseMoved(e);
 		
 		//The three modifiers on clicks are described in the design
 		//and the user documentation. The following code
 		//finds which modifier keys are being pressed
 		boolean pileMode = false;
 		boolean flip = false;
 		boolean neat = false;
 		
 		if((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK)
 			pileMode = true;
 		
 		if((e.getModifiers() & InputEvent.SHIFT_MASK) == InputEvent.SHIFT_MASK)
 			flip = true;
 		
 		if((e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK)
 			neat = true;
 			
 			
 		//Do a pick-up event on left click
 		if(1 == e.getButton())
 		{
 			manipulator.pickUp(pileMode, flip, neat);
 		}
 		//Middle click flips
 		else if(2 == e.getButton())
 		{
 			manipulator.flip(pileMode);
 		}
 		//Right click puts down
 		else if(3 == e.getButton())
 		{
 			//Seed drop chaos using current time
 			manipulator.putDown(pileMode, flip, neat, System.currentTimeMillis());
 		}
 	}
 	
 	//The manipulator moves with the mouse
 	public void mouseMoved(MouseEvent e)
 	{
 		manipulator.setAbstractLocation(e.getPoint());
 		
 		parent.repaint();
 	}
 	
 	//Mouse wheel rotates cards
 	public void mouseWheelMoved(MouseWheelEvent e)
 	{	
 		boolean pileMode = false;
 		
 		if((e.getModifiers() & InputEvent.CTRL_MASK) == InputEvent.CTRL_MASK)
 			pileMode = true;
 			
 		manipulator.rotate((byte)(e.getWheelRotation()*ROTATION_AMOUNT), pileMode);
 	}
 	
 	//Dragging the mouse drags cards
 	public void mouseDragged(MouseEvent e)
 	{
 		mouseMoved(e);
 		manipulator.initiateDrag();
 	}
 	
 	//When the mouse button is released, if the manipulator is dragging,
 	//it stops dragging
 	public void mouseReleased(MouseEvent e)
 	{
 		manipulator.terminateDrag();
 	}
 	
 	//Players should not be able to drag cards outside of the surface
 	public void mouseExited(MouseEvent e)
 	{
 		manipulator.terminateDrag();
 	}
 }