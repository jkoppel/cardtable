/**
 *For Mao online; represents a surface allowing DraggableObjects to be picked
 *up and put down
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 12/15/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
package mao.draggable;

import mao.winind.*;
import mao.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.*;
import java.lang.reflect.Array;


//Mastery aspects shown:
//HL: Recursion, polymorphism, inheritance, encapsulation, hierarchical-composite
			//data structures,
//SL: 1,2,3,4,6,7,8,9,10,14

public abstract class DraggableSurface extends WindowIndependentContainer
{
	//See the general notes at the beginning of C1 for an explanation of the sector
	//representation. This represents a 2D grid of equal-size "sectors" that this
	//surface
	//is partitioned into. Each sector is represented by a DoublyLinkedList holding 
	//each DraggableComponent that lies at least partially into its sector, from top
	//to bottom.
	private DoublyLinkedList<DraggableComponent>[][] sectorMemberLists;
	
	//Contains a set of all DraggableComponents lying on the surface
	private Set<DraggableComponent> draggables;
	
	//See the general notes at the beginning of C1 for an explanation of adjacency graphs. 
	//This memoizes adjacency graphs.
	//That means that, when the adjacency graph that contains a draggable component is computed,
	//it is stored in this hash, keyed by the draggable components.
	//Whenever a method needs the adjacency graph of a draggable component, it is simply
	//returned from memory.
	private HashMap<DraggableComponent, DirectedGraph<DraggableComponent>> adjacencyGraphs;
	
	//N-arg constructor -- simply initializes fields to empty data structures, then 
	//partitions itself into sectors
	public DraggableSurface()
	{
		super();
		draggables = new HashSet<DraggableComponent>();
		adjacencyGraphs = new HashMap<DraggableComponent, DirectedGraph<DraggableComponent>>();
		sectorize();
	}
	
	//See the general C1 notes for an explanation of the sector system.
	//This returns the size of each sector.
	//This value must be constant.
	protected abstract Dimension getSectorSize();
	
	//Returns the sector height
	protected int getSectorHeight()
	{
		return (int)getSectorSize().getHeight();
	}
	
	//Returns the sector width
	protected int getSectorWidth()
	{
		return (int)getSectorSize().getWidth();
	}
	
	//This partitions or repartitions this surface into sectors of size getSectorSize(),
	//and initializes the elements of sectorMemberLists, either to blank DoublyLinkedLists, or,
	//if this is being called because of a resize, to the list that represented that same sector
	//before the resize
	//
	//At current implementation, that means if this surface is shrunk, anything off the screen
	//is destroyed.
	protected void sectorize()
	{
		int h = getAbstractHeight()/getSectorHeight()+1;
		int w =getAbstractWidth()/getSectorWidth()+1;
		
		//Java currently cannot initialize arrays of generics without odd techniques like this
		DoublyLinkedList<DraggableComponent>[][] newSectorMemberLists = 
			(DoublyLinkedList<DraggableComponent>[][])(
				Array.newInstance(DoublyLinkedList.class, new int[]{h,w}));
				
		//Iterates through each sector, either initializing it, or copying ir from the old list
		for(int i = 0; i < h; i++)
		{
			for(int j = 0; j < w; j++)
			{
				if(null != sectorMemberLists &&
					    i<sectorMemberLists.length && j < sectorMemberLists[0].length)
					newSectorMemberLists[i][j] = sectorMemberLists[i][j];
				else
					newSectorMemberLists[i][j] = new DoublyLinkedList<DraggableComponent>();
			}
		}
		
		sectorMemberLists = newSectorMemberLists;
	}
	
	//Resizing now requires re-sectorization, so this is overriden
	public void setAbstractHeight(int h)
	{
		super.setAbstractHeight(h);
		sectorize();
	}
	
	//Resizing now requires re-sectorization, so this is overriden
	public void setAbstractWidth(int w)
	{
		super.setAbstractWidth(w);
		sectorize();
	}
	
	//Resizing now requires re-sectorization, so this is overriden
	public void setAbstractSize(Dimension d)
	{
		super.setAbstractSize(d);
		sectorize();
	}
	
	//Resizing now requires re-sectorization, so this is overriden
	public void setAbstractSize(int w, int h)
	{
		super.setAbstractSize(w,h);
		sectorize();
	}
	
	//Returns an object of java.awt.Rectangle representing
	//the area covered by the y-th sector from the top, x-th sector from the left
	protected Rectangle rectangleForSector(int y, int x)
	{
		return new Rectangle(x*getSectorWidth(), y*getSectorHeight(), getSectorWidth(),
															getSectorHeight());
	}
	
	//Adds a draggable component to this table. Returns true iff sucessful.
	//To decide which sectors a draggable component lies in, this uses a recursive flood fill
	public synchronized boolean addDraggable(DraggableComponent c)
	{
		//If the point that would be the component's top left corner if it were upright
		// is out of bounds, add fails.
		if(c.getAbstractX() >= getAbstractWidth() || c.getAbstractY() >= getAbstractHeight())
			return false;
	
		
		Area a = c.getArea();
		
		//To prevent the floodfill from checking if the draggable component lies in sectors that
		//have already been checked
		boolean[][] checked = new boolean[sectorMemberLists.length][sectorMemberLists[0].length];
		
		Vector2D centroid = c.getCentroid();
		
		//Computes which sector the centroid lies in. Note that a component may not include
		//the point
		//returned by getPosition()
		int y = (int)centroid.getY()/getSectorHeight(), x =
				(int)centroid.getX()/getSectorWidth();
		
		addDraggableHelper(c, a, checked, y, x);
		
		draggables.add(c);
		
		//Must redraw after adding a component
		invalidateBuffer();
		
		return true;
	}
	
	//Performs a flood-fill to find which sector-member lists a DraggableComponent
	//should be added to
	//That is, given a sector (y,x) that the DraggableComponent c lies in, checks all
	//adjacency sectors to see 
	//if the DraggableComponent also lies in some of those. If it does, then it recurses.
	//This is why a DraggableComponent's area must be connected.
	//
	//Uses checked to avoid checking the same sector twice, and passes around c.getArea()
	//in the form of a
	//to avoid recomputing it
	private void addDraggableHelper(DraggableComponent c, Area a, boolean[][] checked,
			int y, int x)
	{
		if(y < 0 || x < 0 || y >= checked.length || x >= checked[0].length)
			return;
		if(checked[y][x])
			return;
		checked[y][x] = true;
		if(a.intersects(rectangleForSector(y,x)))
		{
			sectorMemberLists[y][x].insertHead(c);
			addDraggableHelper(c, a, checked, y+1,x);
			addDraggableHelper(c, a, checked, y-1,x);
			addDraggableHelper(c, a, checked, y,x+1);
			addDraggableHelper(c, a, checked, y,x-1);
		}
	}
	
	//Overrides a method in Container
	//This removes a DraggableComponent from this surface; it then needs to recompute
	//adjacency graphs
	//and redraw
	public synchronized void remove(Component comp)
	{
		if(!(comp instanceof DraggableComponent))
			throw new IllegalArgumentException();
		DraggableComponent c = (DraggableComponent)comp;
		
		//Java lacks support to create arrays of generics without using odd ways like this
		DoublyLinkedList<DraggableComponent>[] dummy =
			 (DoublyLinkedList<DraggableComponent>[])Array.newInstance(
			 		DoublyLinkedList.class,0);
			 
		DoublyLinkedList<DraggableComponent>[] sectors =
			c.getContainingLists().toArray(dummy);
		
		//Removes this from all sectors it's in
		for(DoublyLinkedList<DraggableComponent> sector : sectors)
			sector.remove(c);
			
		draggables.remove(c);
		adjacencyGraphs.remove(c);
		invalidateBuffer();
	}
	
	//Returns the top DraggableComponent lying at p, or null
	//if none exists
	public DraggableComponent getTopComponent(Point p)
	{
		//Gets the (y,x) of the sector this point lies in
		int y = ((int)p.getY())/getSectorHeight();
		int x = ((int)p.getX())/getSectorWidth();
		
		if(x < 0 || y < 0 || y >= sectorMemberLists.length || x >=
				sectorMemberLists[0].length)
			return null;
		
		//Iterates down the sector, finding the highest DraggableComponent that covers
		//a region containing this point
		DoublyLinkedList<DraggableComponent> lst = sectorMemberLists[y][x];
		for(DraggableComponent c : lst)
		{
			if(c.getArea().contains(p))
				return c;
		}
		return null;
	}
	
	//Returns the adjacency graph for a component, computing it only
	//if necessary
	public DirectedGraph<DraggableComponent> getAdjacencyGraph(DraggableComponent c)
	{
		DirectedGraph<DraggableComponent> g = adjacencyGraphs.get(c);
		if(g==null)
			return computeAdjacencyGraph(c);
		else
			return g;
	}
	
	//Marks that an adjacency graph is now obsolete, and this class
	//will need to recompute the adjacency graph for all DraggableComponents
	//in it
	public synchronized void invalidateAdjacencyGraph(DirectedGraph<DraggableComponent> g)
	{
		for(DirectedGraph<DraggableComponent>.GraphNode node : g)
			adjacencyGraphs.remove(node.getElement());
	}
	
	//Computes and returns the adjacency graph for a component. Algorithm is explained below
	//on its helper method
	public synchronized DirectedGraph<DraggableComponent> 
					computeAdjacencyGraph(DraggableComponent c)
	{
		DirectedGraph<DraggableComponent> g = new DirectedGraph<DraggableComponent>();
		Set<DraggableComponent> checked = new HashSet<DraggableComponent>();
		
		computeAdjacencyGraphHelper(c, g, checked);
		
		//Caches that this is the adjacency graph for all pther draggable components in it
		for(DirectedGraph<DraggableComponent>.GraphNode node : g)
			adjacencyGraphs.put(node.getElement(), g);
		
		return g;
	}
	
	//Recursively computes an adjacency graph using the following flood-fill based algorithm:	
	//For the given DraggableComponent c, it iterates through each sector it's in.
	//It iterates through each DraggableComponent in that sector from top to bottom.
	//For each DraggableComponent d above c intersecting c, it creates an edge from d to c.
	//For each DraggableComponent d below c intersecting c, it creates an edge from c to d.
	//For each DraggableComponent d that was added to the graph, it calls this method on d,
	//then adds d to the "checked" set so that it will know not to call this method on d again.
	private void computeAdjacencyGraphHelper(DraggableComponent c,
									DirectedGraph<DraggableComponent> g,
		Set<DraggableComponent> checked)
	{
		if(checked.contains(c))
			return;
		checked.add(c);
		
		Area a = c.getArea();
		DirectedGraph<DraggableComponent>.GraphNode node = g.nodeFor(c);
		
		for(DoublyLinkedList<DraggableComponent> l : c.getContainingLists())
		{
			boolean passed = false;
			for(DraggableComponent d : l)
			{
				if(d == c)
				{
					passed = true;
					continue;
				}
				
				if(GeometryUtils.intersects(a, d.getArea()))
				{
					if(passed)
						node.addChild(g.nodeFor(d));
					else	
						node.addParent(g.nodeFor(d));
					
					computeAdjacencyGraphHelper(d, g, checked);
				}
			}
		}
	}
	
	//Draws all DraggableComponents from bottom to top
	//using the DirectedGraph.linearizedView() method to compute the draw order for every
	//DraggableComponent in the same adjacency graph, and marking each DraggableComponent 
	//as it draws it to avoid repetition
	public synchronized void draw(Graphics2D g)
	{
		Set<DraggableComponent> drawn = new HashSet<DraggableComponent>();
		
		for(DraggableComponent c : draggables)
		{
			if(drawn.contains(c))
				continue;
			
			DoublyLinkedList<DirectedGraph<DraggableComponent>.GraphNode> drawOrder =
				getAdjacencyGraph(c).linearizedView();
			drawOrder.reverse();
			for(DirectedGraph<DraggableComponent>.GraphNode n : drawOrder)
			{
				DraggableComponent d = n.getElement();
				handleDraw(d, g);
				drawn.add(d);
			}
		}
	}
}