/**
 *For Mao online; a component that can be transferred between a Manipulator and a Pile
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/16/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
package mao.draggable;
 
import mao.winind.*;
import mao.util.*;
import mao.manipulators.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;

/*
 *Mastery aspects shown in this class:
 *HL: Encapsulation, polymorphism, inheritance, hierarchical composite, parsing
 *			a data stream,
 *SL: 2,3,4,8,9,10,14
 */


/*
 *For now, all DraggableComponents MUST include their centroids.
 */
public abstract class DraggableComponent extends RotatableDisplayComponent
			implements ListElement<DraggableComponent>
{
	//Stores the value which will represent a rotation of Pi radians (180 degreess).
	//The negative of this value will also represent the maximum rotation, as
	//-pi radians is equivalent to pi radians
	//Means that angles will be represented by multiples of pi/90
	public static final int MAX_ANGLE = 90;

	//Used to initialize the IDs of DraggableComponents
	//IDs start at 1; 0 is null ID
	private static int nextId = 1;
	
	private int id; //holds the ID of the DraggableComponent, which is used to make
						//each uniquely identifiable.
	
	//Used to implement the methods of the ListElement interface. Stores references to
	//the ListNode containing this draggable element for each DoublyLinkedList
	//containing this draggable element. Also used to enumerate
	//through the lists representing each sector this draggable component is in.
	private Map<DoublyLinkedList<DraggableComponent>,
			DoublyLinkedList<DraggableComponent>.ListNode> containingListNodes;
	
	/*
	 *Empty constructor. Merely sets its ID to the next ID  and initializes
	 *containingListNodes
	 */
	public DraggableComponent()
	{
		//Synchronized is needed in case another thread constructs a DraggableComponent
		//in between the following two lines
		//That could yield two DraggableComponents with the same ID!
		synchronized(DraggableComponent.class)
		{
			id = nextId;
			nextId++;
		}
		containingListNodes = new HashMap<DoublyLinkedList<DraggableComponent>,
											DoublyLinkedList<DraggableComponent>.ListNode>();
	}
	
	/*
	 *Not presently used, but will be used when I overhaul the game state transfer system.
	 *This is used to initialize copies of Draggable components present on other computers
	 */
	public DraggableComponent(byte[] serialized)
	{
		containingListNodes = new HashMap<DoublyLinkedList<DraggableComponent>,
											DoublyLinkedList<DraggableComponent>.ListNode>();
		id = BinaryUtils.asBigEndianInt(serialized, 0, 4);
		read(serialized);
	}
	
	//Returns the ID
	public int getID()
	{
		return id;
	}
	
	//Returns the ListNode in lst whose element is this, or null if this is not in lst
	public DoublyLinkedList<DraggableComponent>.ListNode getContainingListNode(
			DoublyLinkedList<DraggableComponent> lst)
	{
		return containingListNodes.get(lst);
	}
	
	//Used to store the ListNode in lst whose element is this
 	public void setContainingListNode(DoublyLinkedList<DraggableComponent> lst,
 		DoublyLinkedList<DraggableComponent>.ListNode node)
 	{
 		containingListNodes.put(lst, node);
 	}
 	
 	//Used to specify that a given DoublyLinkedList no longer contains this,
 	//and there is no longer any ListNode within that list containing a reference to this
 	public void removeContainingList(DoublyLinkedList<DraggableComponent> lst)
 	{
 		containingListNodes.remove(lst);
 	}
 	
 	//Returns a set of all DoublyLinkedLists that have this as an element
 	public Set<DoublyLinkedList<DraggableComponent>> getContainingLists()
 	{
 		return containingListNodes.keySet();
 	}
 	
 	//Takes two angles in multiples of pi/MAX_ANGLE (i.e.: 2 degrees, since MAX_ANGLE=90),
 	//and returns their sum
 	//as an equivalent angle in the interval [MAX_ANGLE,-MAX_ANGLE] (i.e.: [pi, -pi]
 	protected byte degreeSum(byte theta1, byte theta2)
 	{
 		int sum = (((int)theta1)+theta2)%(2*MAX_ANGLE);
 		if(sum > MAX_ANGLE)
 			sum -= 2* MAX_ANGLE;
 		return (byte)sum;
 	}
	
	//Returns an object of java.awt.geom.Area that represents the region
	//this draggable component covers.
	//The current implementation requires this area be connected -- i.e.:
	//for any two points in the area, there should be a path between them lying entirely
	//in the area. Or, more informally, the area must not consist of two or more disjoint
	//blobs
	public abstract Area getArea();
	
	//Rotates this DraggableComponent
	public abstract void rotate(byte thetaOver2Degrees);
	
	//After calling this method, the DraggableComponent should be the same as a
	// thetaOver2Degrees rotation from its starting "upright" position
	public abstract void setRotation(byte thetaOver2Degrees);
	
	//Optional operation -- if this DraggableComponent has two sides, then this should flip
	//it to display the other side
	public abstract void flip();
	
	//Returns a small icon representing this DraggableComponent. This icon will draw
	//below a player's manipulator when he is carrying this.
	public abstract Image getIcon();
	
	//Returns a point representing the centroid of this DraggableComponent.
	//When a DraggableComponent is rotated, it should be rotated about the centroid.
	//Under current implementation, a component should always include its centroid --
	//no crescent figures whose centroid is not part of the component
	public abstract Vector2D getCentroid();
	
	//Returns a byte array consisting of a compact representation of this
	//DraggableComponent.
	//Passing this array to the byte array constructor should yield an identical
	//copy of this
	//DraggableComponent. This method will be used for checksumming purposes.
	public abstract byte[] serialize();
	
	/*
	 *Initializes the draggable component from an array containing a compact byte representation,
	 *not including adjacencies. The ID (first four bytes) will have already been read when
	 *this is called.
	 */
	protected abstract void read(byte[] serialized);
}