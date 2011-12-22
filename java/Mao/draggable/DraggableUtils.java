/**
 *For Mao online; contains miscellaneous methods used in the draggable support system
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 3/09/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.draggable;
 
 import mao.util.*;
 import java.util.*;
 import java.util.zip.*;
 import java.awt.*;
 import java.awt.image.*;
 
 /*
  *Master aspects shown:
  *HL: Encapsulation, parsing a data stream, hierarchical composite data structures
  *SL: 1,2,3,4,6,7,8,9,10,14
  */
 
 public class DraggableUtils
 {
 	
 	/*Helper class for checksum -- used to sort GraphNodes representing 
 	 *DraggableComponents by ID*/
 	private static class IDComparator implements
 					Comparator<DirectedGraph<DraggableComponent>.GraphNode>
 	{
 		public int compare(DirectedGraph<DraggableComponent>.GraphNode a,
 								DirectedGraph<DraggableComponent>.GraphNode b)
 		{
 			return b.getElement().getID()-a.getElement().getID();
 		}
 	}
 		
 	
 	/*
 	 *Helper method for checksum -- used due to type issues
 	 *(Java gave me lots of syntax errors constructing arrays
 	 *of DirectedGraph<DraggableComponent>.GraphNode. I suspect it may be due to a
 	 *parser bug -- it did not understand me
 	 *when I tried to initialize DirectedGraph<DraggableComponent>.GraphNode arrays,
 	 *though simply changing that to DirectedGraph.GraphNode arrays removed the problem.
 	 *
 	 *This uses type system voodoo is needed to get around this
 	 */
 	 private static DirectedGraph<DraggableComponent>.GraphNode[] toArray(
 	 	DoublyLinkedList<DirectedGraph<DraggableComponent>.GraphNode> lst)
 	 {
 	 	DirectedGraph.GraphNode[] arr = new DirectedGraph.GraphNode[lst.size()];
 	 	int i = 0;
 	 	for(DirectedGraph<DraggableComponent>.GraphNode node : lst)
 	 	{
 	 		arr[i] = node;
 	 		i++;
 	 	}
 	 	return arr;
 	 }
 	 
 	
 	/*A checksum is a number computer from a longer stream of bits which changes drastically
 	 *if the informations differs by even a little. This method computes the checksum of an
 	 *adjacency graph of draggable components. If there is even a small difference between
 	 *the relevant part of the table on two different computers, then the checksums will differ,
 	 *and we will know a conflict has occured, and can revert the conflicting changes.
 	 *
 	 *There are several types of checksums. We will use the cylic redundancy check,
 	 *which is implemented
 	 *in the java.util.zip.CRC32 class
 	 *
 	 *The mechanism for computing a checksum is as follows:
 	 *
 	 *We take the list of all DraggableComponents in the graph, and sort by ID.
 	 *Then, for each, we update the checksum using the serialized form of the DraggableComponent,
 	 *the number of DraggableComponents above it, the number below it, the IDs of the
 	 *DraggableComponents above it,
 	 *and the IDs of the DraggableComponents below it.
 	 */
 	public static long checksum(DirectedGraph<DraggableComponent> g)
 	{
 		DoublyLinkedList<DirectedGraph<DraggableComponent>.GraphNode> lst = g.linearizedView();
 		
 		if(lst.size()==0)
 			return 1;
 		
 		
 		
 		DirectedGraph<DraggableComponent>.GraphNode[] lstArr = toArray(lst);
 		Arrays.sort(lstArr, new IDComparator());
 		
 		CRC32 checksum = new CRC32();
 		
 		for(DirectedGraph<DraggableComponent>.GraphNode node : lstArr)
 		{
 			checksum.update(node.getElement().serialize());
 			DoublyLinkedList<DirectedGraph<DraggableComponent>.GraphNode> parents =
 																		 node.getParents();
 			DoublyLinkedList<DirectedGraph<DraggableComponent>.GraphNode> children =
 																		node.getChildren();
 			checksum.update(parents.size());
 			checksum.update(children.size());
 			
 			DirectedGraph<DraggableComponent>.GraphNode[] parArr = toArray(lst);
 			Arrays.sort(parArr, new IDComparator());
 			
 			
 			for(DirectedGraph<DraggableComponent>.GraphNode par : parArr)
 			{
 				/*
 				 *CRC32's update method demands input is in bytes
 				 */
 				byte[] b = new byte[4];
 				BinaryUtils.storeAsBytes(par.getElement().getID(), b, 0, 4);
 				
 				checksum.update(b);
 			}
 			
 			DirectedGraph<DraggableComponent>.GraphNode[] chilArr = toArray(lst);
 			Arrays.sort(chilArr, new IDComparator());
 			
 			
 			for(DirectedGraph<DraggableComponent>.GraphNode child : chilArr)
 			{
 				/*
 				 *CRC32's update method demands input is in bytes
 				 */
 				byte[] b = new byte[4];
 				BinaryUtils.storeAsBytes(child.getElement().getID(), b, 0, 4);
 				
 				checksum.update(b);
 			}
 		}
 		
 		//I believeCRC32 only returns longs with the first 4 bytes set, and therefore only positive.
 		//Just to be safe, I'll cover the case where it does return -1, which is used
 		//elsewhere in the program
 		//as a special return value;
 		
 		if(checksum.getValue()==-1)
 			return -2;
 		else
 			return checksum.getValue();
 	}
 	
 	/*
 	 *Creates a blank cursor, which is used to remove the mouse cursor for the table,
 	 *as the manipulator
 	 *acts as a de facto cursor.
 	 *
 	 *However, this is not actually used in the current version, as the BufferedImage in the cursor
 	 *is not serializable, which causes problems.
 	 */
 	public static Cursor getBlankCursor()
 	{
 		return Toolkit.getDefaultToolkit().createCustomCursor(
 			new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB), new Point(), "blank");
 	}
 }