/**
 *Implements an unweighted directed graph where each node corresponds
 *to an outside object
 *This is the truest representation of the way draggable components
 *are on top of each other,
 *and they will be converted into these a lot in the
 *form of "adjacency graphs"
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/06/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */

package mao.util;

import java.util.*;
import java.io.Serializable;

//Mastery aspects:
//HL: Implementation of an abstract data type, encapsulation,
			//hierarchical composite data structure
//SL: 2,3,4,6,7,8,9,10

public class DirectedGraph<T> implements
			Iterable<DirectedGraph<T>.GraphNode>, Serializable
{
	//Maps the outside objects to the nodes representing them
	private Map<T, GraphNode> nodes;
	
	/*
	 *If node A has an edge to B,
	 *then A will be called a parent of B, and B is called a child of A.
	 */
	public class GraphNode implements ListElement<GraphNode>, Serializable
	{
		//Used to implement the methods of the ListElement interface. Stores
		//references to the ListNode containing
		//this GraphNode for each DoublyLinkedList containing this GraphNode.
		private Map<DoublyLinkedList<GraphNode>,
		 DoublyLinkedList<GraphNode>.ListNode> containingListNodes;
	
		//A list of all parents of this GraphNode
		private DoublyLinkedList<GraphNode> parents;
		//A list of all children of this GraphNode
		private DoublyLinkedList<GraphNode> children;
		//The element this GraphNode refers to
		private T element;
		
		//Initializes this GraphNode to refer to el
		public GraphNode(T el)
		{
			element = el;
			parents = new DoublyLinkedList<GraphNode>();
			children = new DoublyLinkedList<GraphNode>();
			nodes.put(el, this);
			
			containingListNodes = new HashMap<DoublyLinkedList<GraphNode>,
										DoublyLinkedList<GraphNode>.ListNode>();
		}
		
		//Returns the element this GraphNode refers to
		public T getElement()
		{
			return element;
		}
		
		//Sets the element this GraphNode refers to
		public void setElement(T t)
		{
			element = t;
		}
		
		//Returns the list of parents of this GraphNode
		public DoublyLinkedList<GraphNode> getParents()
		{
			return parents;
		}
		
		//Retursn the lkist of children of this GraphNode
		public DoublyLinkedList<GraphNode> getChildren()
		{
			return children;
		}
		
		//Returns the number of children
		//(out-degree is a graph theory term for number of directed
		//edges out of a node)
		public int outDegree()
		{
			return children.size();
		}
		
		//Returns the number of parents
		//(in-degree is a graph theory term for number of directed
		//edges into of a node)
		public int inDegree()
		{
			return parents.size();
		}
		
		//Adds the GraphNode g as a child of this node
		public void addChild(GraphNode g)
		{
			if(!children.contains(g))
			{
				children.insertTail(g);
				g.addParent(this);
			}
		}
		
		//Removes g as a child of this node
		public void removeChild(GraphNode g)
		{
			children.remove(g);
		}
		
		//Adds g as a paretn of this node
		public void addParent(GraphNode g)
		{
			if(!parents.contains(g))
			{
				parents.insertTail(g);
				g.addChild(this);
			}
		}
		
		//Removes g as a parent of this node
		public void removeParent(GraphNode g)
		{
			parents.remove(g);
		}
		
		//Returns the ListNode in lst whose element is this, or null
		//if this is not in lst
		public DoublyLinkedList<GraphNode>.ListNode getContainingListNode(
			DoublyLinkedList<GraphNode> lst)
		{
			return containingListNodes.get(lst);
		}
	
		//Used to store the ListNode in lst whose element is this
 		public void setContainingListNode(DoublyLinkedList<GraphNode> lst,
 			DoublyLinkedList<GraphNode>.ListNode node)
 		{
 			containingListNodes.put(lst, node);
 		}
 	
 		//Used to specify that a given DoublyLinkedList no longer contains this,
 		//and there is no longer any ListNode within that list containing a reference to this
 		public void removeContainingList(DoublyLinkedList<GraphNode> lst)
 		{
 			containingListNodes.remove(lst);
 		}
 		
 		//Returns a set of all DoublyLinkedLists that have this as an element
 		public Set<DoublyLinkedList<GraphNode>> getContainingLists()
 		{
 			return containingListNodes.keySet();
 		}
 		
 		//Returns the DirectedGraph containing this GraphNode
 		public DirectedGraph<T> getGraph()
 		{
 			return DirectedGraph.this;
 		}
 		
 		//Removes this node from the graph
 		public synchronized void removeSelf()
 		{
 			for(GraphNode n : parents)
 				n.removeChild(this);
 			for(GraphNode n : children)
 				n.removeParent(this);
 			parents = null;
 			children = null;
 			nodes.remove(getElement());
 			setElement(null);
 		}
	}
	
	//Initializes this graph
	public DirectedGraph()
	{
		nodes = new HashMap<T, GraphNode>();
	}
	
	//Returns the node referring to outside object t,
	//or creates a new one if there is none
	public GraphNode nodeFor(T t)
	{
		GraphNode g = nodes.get(t);
		if(null == g)
		{
			g = new GraphNode(t);
			nodes.put(t, g);
		}
		return g;
	}
 		
 	//Returns a linked list such that if an element B is later than an element
 	//A in the list,
 	//either B is a descendant of A, or B is in a different branch from A
 	//This is directly used to compute which cards should be displayed on top of others
 	/*
 	 *Algorithm: For each node, we track the number of parents not in the list.
 	 *One by one, we add those with 0 parents not in the list to the list, then 
 	 *decrement the tracked number by 1. We keep a list of all nodes with 0
 	 *unaccounted for parents.
 	 */
 	public DoublyLinkedList<GraphNode> linearizedView()
 	{
 		Collection<GraphNode> nodeCollection = nodes.values();
 		
 		Map<GraphNode, Integer> unaccountedParents= new HashMap<GraphNode, Integer>();
 		
 		DoublyLinkedList<GraphNode> nodeList = new DoublyLinkedList<GraphNode>();
 		
 		//would use a queue, but Java lacks a standard queue class
 		//Stack vs. queue makes no difference here, really
 		Stack<GraphNode> nextNodes = new Stack<GraphNode>(); 
 															
 		
 		for(GraphNode n : nodeCollection)
 		{
 			unaccountedParents.put(n, n.getParents().size());
 			if(0 == n.getParents().size())
 				nextNodes.push(n);
 		}
 		
 		while(!nextNodes.isEmpty())
 		{
 			GraphNode n = nextNodes.pop();
 			nodeList.insertTail(n);
 			for(GraphNode child : n.getChildren())
 			{
 				int unPar = unaccountedParents.get(child);
 				unPar--;
 				if(0 == unPar)
 					nextNodes.push(child);
 				unaccountedParents.put(child, unPar);
 			}
 		}
 		
 		return nodeList;
 	}
 	
 	//Returns an iterator that iterates through all the nodes
 	//in this DirectedGraph
 	public Iterator<GraphNode> iterator()
 	{
 		return nodes.values().iterator();
 	}
}