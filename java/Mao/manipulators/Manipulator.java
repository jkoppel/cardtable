/**
 *For Mao online; a concept of a floating hand-image
 *							that carries and acts on cards
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/18/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */


package mao.manipulators;

import mao.draggable.*;
import mao.util.*;
import mao.game.*;
import java.util.Random;
import java.awt.geom.Point2D;

import mao.winind.*;

//Mastery aspects
//HL: Inheritance, encapsulation, polymorphism
//SL: 1,2,3,4,6,8,9,10,14

/*
 *Return value convention: -1 means action did not happen;
 *0 means action happened but there
 *is no adjacency graph to compute the checksum from
 *(e.g.: dropped a card on an empty part of the table)
 */

	
	
public abstract class Manipulator extends RotatableDisplayComponent
{
	//stores the cards held by this Manipulator, from top to bottom
	protected DoublyLinkedList<DraggableComponent> cards;
	
	//the DraggableSurface that this Manipulator is on
	private DraggableSurface surface;
	
	//Currently hardcoded. The offset from this Manipulator's location
	//to the point on the finger where clicks are considered to occur
	private Vector2D hotspotOffset = new Vector2D(8,0);
	
	//whether this Manipulator is dragging anything
	protected DraggableComponent dragged = null;
	
	//Dragged components keep their relative position to the manipulator
	//That is, if the finger of the manipulator is on the top left
	//when dragging begins,
	//it the draggable component will move so that the finger
	//stays at the top left
	//until it is let go.
	//
	//That relative position is stored hered
	protected Vector2D draggedOffset = null;
		
	//Whether this Maniplator is frozen --
	//frozen Manipulators stop receiving and performing actions
	private boolean frozen = false;
	
	//The ID of the player owning this Manipulator
	private byte playerID;
	
	//Creates a Manipulator on the surface for the player with ID pID
	public Manipulator(DraggableSurface surface, byte pID)
	{
		this.surface = surface;
		playerID = pID;
		cards = new DoublyLinkedList<DraggableComponent>();
	}
	
	//Returns the list of cards carried by the Manipulator
	public DoublyLinkedList<DraggableComponent> getCards()
	{
		return cards;
	}
	
	//Returns the point where clicks are considered to occur
	public Vector2D getHotspot()
	{
		return getAbstractLocation().plus(hotspotOffset);
	}
	
	//Returns the surface this Manipulator is on
	public DraggableSurface getSurface()
	{
		return surface;
	}
	
	//Sets the surface this Manipulator is on
	public void setSurface(DraggableSurface s)
	{
		surface = s;
	}
	
	/*
	 *Performs a pick-up operation by removing a card from the surface
	 *and adding it to this Manipulator. Picks up a whole pile
	 *adjacency graph) if pileMode is on, turns the picked up
	 *cards over in the process if flip is on,
	 *straightens the cards as well if straightening is on
	 *
	 *Returns a checksum of the adjacency graph the picked-up component was in post pick-up
	 *or, for pileMode,
	 *since there will be no adjacency graph post-pickup, pre-pickup
	 *
	 *Returns -1 if the action is not performed
	 */
	public synchronized long pickUp(boolean pileMode,
											boolean flip, boolean straightening)
	{
		if(pileMode)
		{
			DraggableComponent c = getSurface().getTopComponent(getHotspot());
			
			if(null==c)
				return -1;
			
			DirectedGraph<DraggableComponent> graph = getSurface().getAdjacencyGraph(c);
			DoublyLinkedList<DirectedGraph<DraggableComponent>.GraphNode> toPick =
																 graph.linearizedView();
			
			long checksum = DraggableUtils.checksum(graph);
			
			if(toPick.isEmpty())
				return -1;
			
			if(flip)
				toPick.reverse();
			
			for(DirectedGraph<DraggableComponent>.GraphNode n : toPick)
				pickUpSingle(n.getElement(),flip,straightening);
			getSurface().invalidateBuffer();
			getSurface().repaint();
			
			return checksum;
		}
		else
		{
			DraggableComponent c = getSurface().getTopComponent(getHotspot());
			if(null == c)
				return -1;
			
			DirectedGraph<DraggableComponent> graph =
								getSurface().getAdjacencyGraph(c);
			getSurface().invalidateAdjacencyGraph(graph);
			
			graph.nodeFor(c).removeSelf();
			long checksum = DraggableUtils.checksum(graph);
			
			pickUpSingle(c,flip,straightening);
			getSurface().invalidateBuffer();
			getSurface().repaint();
			return checksum;
		}
	}
	
	//Picks up a single card by removing it from the table and adding it to this
	//Manipulator
	private void pickUpSingle(DraggableComponent c, boolean flip,
												boolean straightening)
	{
			getSurface().remove(c);
		
			if(flip)
				c.flip();
			if(straightening)
				c.setRotation((byte)0);
			
			c.setAbstractLocation(getHotspot());
			
			getCards().insertTail(c);
	}
	
	/*
	 *Performs a pick-up operation by removing a card from the manipulator
	 *and adding it to this surface. Puts down up all carried cards
	 *if pileMode is on, turns the dropped cards over in the process if flip is on.
	 *Cards will move a small random amount upon dropping according to random numbers
	 *from a Random object seeded by randomSeed, unless carefully is turned on,
	 *in which case there is no drop chaos.
	 *
	 *Returns a checksum of the adjacency graph the dropped component is in post drop
	 *since there will be no adjacency graph post-pickup, pre-pickup
	 *
	 *Returns -1 if the action is not performed
	 */
	public synchronized long putDown(boolean pileMode, boolean flip, boolean carefully,
		long randomSeed)
	{
		
		if(isEmpty())
			return -1;
		if(pileMode)
		{
			
			if(!flip)
			{
				getCards().reverse();				
			}
			
			DraggableComponent lastPlaced = null;
			
			Random r = new Random(randomSeed);
			
			for(DraggableComponent c : getCards())
			{
				putDownSingle(c,flip,carefully, r);
				lastPlaced = c;
			}
			getSurface().invalidateBuffer();
			getSurface().repaint();
			
			DirectedGraph<DraggableComponent> graph =
						getSurface().computeAdjacencyGraph(lastPlaced);
			return DraggableUtils.checksum(graph);
		}
		else
		{
			if(isEmpty())
				return -1;
			DraggableComponent toDrop = getCards().last();
			
			if(!putDownSingle(toDrop, flip, carefully, new Random(randomSeed)))
				return -1;
			
			getSurface().invalidateBuffer();
			getSurface().repaint();
			
			DirectedGraph<DraggableComponent> graph =
							getSurface().getAdjacencyGraph(toDrop);
			return DraggableUtils.checksum(graph);
		}
	}
	
	//Puts down a card by removing it from this Manipulator, shifting its position
	//a small random amount using r, then adding to the surface
	private boolean putDownSingle(DraggableComponent c, boolean flip,
					boolean carefully, Random r)
	{
		
		int chaosX = 0;
		int chaosY = 0;
		int oldX = c.getAbstractX();
		int oldY = c.getAbstractY();
		/*
		 *Drop chaos: By default, when you drop a card, it will appear +-10 pixels
		 *in each axis from where you meant to drop it
		 */
		if(!carefully)
		{
			chaosX = r.nextInt(2*Controls.DROP_CHAOS+1) - Controls.DROP_CHAOS;
			chaosY = r.nextInt(2*Controls.DROP_CHAOS+1) - Controls.DROP_CHAOS;
		}
		
		
		c.setAbstractX(c.getAbstractX()+chaosX);
		c.setAbstractY(c.getAbstractY()+chaosY);
			
		if(!getSurface().addDraggable(c))
		{
			c.setAbstractX(oldX);
			c.setAbstractY(oldY);
			return false;
		}
		
		if(getCards().contains(c)) //In case this is called from terminateDrag
			getCards().remove(c);
		
		if(flip)
			c.flip();
		
		return true;	
	}
	
	/*Performs a rotate operation.
	 *
	 *Because rotations can move cards over and under other cards,
	 *and can also greatly change adjacency graphs, this is not that straightforward
	 *of an operation. The simple but slow algorithm currently used involves picking
	 *up the entire adjacency graph, then rotating the relevant card by amt,
	 *then putting it back down.
	 *
	 *If there are cards carried by this Manipulator, then instead
	 *it rotates each of them -- much simpler
	 *
	 *Note that pileMode is disabled, as pile rotations, which rotate each
	 *card separately,
	 *look incredibly odd and disturbing.
	 *
	 *Returns the checksum of the adjacency graph of the card that was rotated,
	 *post rotation
	 */
	public long rotate(byte amt, boolean pileMode)
	{
		if(isEmpty())
		{
			DraggableComponent c = getSurface().getTopComponent(getHotspot());
			if(null == c)
				return -1;
			
			DirectedGraph<DraggableComponent> graph = getSurface().getAdjacencyGraph(c);
			DoublyLinkedList<DirectedGraph<DraggableComponent>.GraphNode> lst =
																 graph.linearizedView();
			for(DirectedGraph<DraggableComponent>.GraphNode n : lst)
				getSurface().remove(n.getElement());
			
			
			c.rotate(amt);
				
			lst.reverse();
			for(DirectedGraph<DraggableComponent>.GraphNode n : lst)
				getSurface().addDraggable(n.getElement());
			
			getSurface().invalidateAdjacencyGraph(graph);
			getSurface().invalidateBuffer();
			getSurface().repaint();
			
			//need to construct a new adjacency graph to take checksum, as rotation
			//can change adjacencies
			return DraggableUtils.checksum(getSurface().getAdjacencyGraph(c));
		}
		else
		{
			for(DraggableComponent d : getCards())
				d.rotate(amt);
			
			getSurface().repaint();
			return 0;
		}
	}
	
	/*
	 *Performs a flip operation, flipping one or, of pileMode is on,
	 *and entire adjacency graph of cards beneath this Manipulator.
	 *If this Manipulator
	 *is carrying cards, then instead it turns over the carried cards.
	 *
	 *Returns the checksum of the affected adjacency graph, post flip
	 */
	public long flip(boolean pileMode)
	{
		
		if(!isEmpty())
		{
			for(DraggableComponent d : getCards())
				d.flip();
			getSurface().repaint();
			return 0;
		}
		else
		{
			DraggableComponent c = getSurface().getTopComponent(getHotspot());
			if(null == c)
				return -1;
				
			DirectedGraph<DraggableComponent> graph = getSurface().getAdjacencyGraph(c);
				
			if(pileMode)
			{
				DoublyLinkedList<DirectedGraph<DraggableComponent>.GraphNode> lst =
																	graph.linearizedView();
				for(DirectedGraph<DraggableComponent>.GraphNode n : lst)
					n.getElement().flip();
			}
			else
				c.flip();
			getSurface().invalidateBuffer();
			getSurface().repaint();
			
			return DraggableUtils.checksum(graph);
		}
	}
	
	//Returns whether this Manipulator is dragging a card
	public boolean dragging()
	{
		return null != dragged;
	}
	
	/*
	 *Performs an init drag operation if not carrying anything,
	 *removing the dragged card from the surface and "pinning" it 
	 *to move along with the Manipulator
	 *
	 *Returns the checksum of the graph the card was removed from
	 */
	public long initiateDrag()
	{
		if(dragging())
			return -1;
		if(isEmpty())
		{
			dragged = getSurface().getTopComponent(getHotspot());
				
			if(null == dragged)
				return -1;
			
			DirectedGraph<DraggableComponent> graph =
					getSurface().getAdjacencyGraph(dragged);
			
			draggedOffset = getHotspot().minus(dragged.getAbstractLocation());
			getSurface().remove(dragged);
			
			getSurface().invalidateAdjacencyGraph(graph);
			
			graph.nodeFor(dragged).removeSelf();
			
			getSurface().invalidateBuffer();
			
			return DraggableUtils.checksum(graph);
		}
		return -1;
	}
	
	/*
	 *Performs a terminate drag operation if dragging something,
	 *removing the dragged card from the Manipulator and placing it 
	 *on the surface
	 *
	 *Returns the checksum of the graph the card was added to from
	 */
	public long terminateDrag()
	{
		if(dragging())
		{
			if(!putDownSingle(dragged, false, true, null))
				return -1;
			
			DirectedGraph<DraggableComponent> graph =
						getSurface().computeAdjacencyGraph(dragged);
				
			dragged = null;
			draggedOffset = null;
			
			getSurface().invalidateBuffer();
			
			return DraggableUtils.checksum(graph);
		}
		return -1;
	}
	
	//Returns whether this Manipulator is carrying any cards
	public boolean isEmpty()
	{
		return getCards().isEmpty();
	}
	
	//Sets whether this Manipulator is frozen
	public void setFrozen(boolean b)
	{
		frozen = b;
	}
	
	//Returns whether this Manipulator is frozen
	public boolean frozen()
	{
		return frozen;
	}
	
	//Returns the ID of the player owning this Manipulator
	public byte getPlayerID()
	{
		return playerID;
	}
}