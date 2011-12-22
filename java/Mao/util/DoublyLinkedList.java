/**
 *DoublyLinkedList class for Mao online
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 11/13/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */

/*
 *Originally this class was just a normal doubly-linked list implementation
 *to satisfy the mastery requirement.
 *
 *However, it has since evolved to have some special-purpose behavior.
 *The only elements
 *that can be added to this must implement the ListElement
 *interface, which requires
 *them to track which ListNode refers to them.
 *This makes removal by object (but not
 *removal by index) and the contains method much faster.
 *This also has some set
 *behavior, in that any object can only be a member of
 *this DoublyLinkedList once.
 *
 *At one point, I extended this class to implement the 
 *java.util.List interface
 *so I could use methods in Collections on it; however, I later
 *rewrote the code not to use Collections methods on this class,
 *so there are a ton of unnecessary methods in here.
 */

package mao.util;

import java.util.*;
import java.io.Serializable;

//Mastery aspects
//HL: Recursion, encapsulation, implementing an abstract data type
//SL: 2,4,5,6,8,9,10,12

public class DoublyLinkedList<T extends ListElement<T>>
						implements List<T>, Serializable
{
	public class ListNode implements Serializable
	{
		//The element stored by this list node
		private T element; 
		
		//The next list node in the linked list
		private ListNode next; 
		
		// The previous list node in the linked list
		private ListNode previous; 
		
		//Initializes this list node using the given argumetns
		public ListNode(T element, ListNode previous, ListNode next)
		{
			this.element = element;
			this.previous = previous;
			this.next = next;
			
			element.setContainingListNode(DoublyLinkedList.this, this);
		}
		
		//Sets the element of this list node
		public void setElement(T element)
		{
			this.element.removeContainingList(DoublyLinkedList.this);
			this.element = element;
			if(null != element)
				element.setContainingListNode(
								DoublyLinkedList.this, this);
		}
		
		//Sets the next list node in the linked list
		public void setNext(ListNode next)
		{
			this.next = next;
		}
		
		//Sets the previous list node in the linked list
		public void setPrevious(ListNode previous)
		{
			this.previous = previous;
		}
		
		//Returns the element stored in this list node
		public T getElement()
		{
			return element;
		}
		
		//Returns the next node in the linked list
		public ListNode getNext()
		{
			return next;
		}
		
		//Returns the previous node in the linked list
		public ListNode getPrevious()
		{
			return previous;
		}
		
		//Recursively reverses this list in-place,
		private void recurseReverse()
		{
			ListNode oldNext = getNext();
			setNext(getPrevious());
			setPrevious(oldNext);
			if(null != oldNext)			
				oldNext.recurseReverse();
		}
		
		//Returns the DounlyLinkedList containing this ListNode
		public DoublyLinkedList<T> getContainingList()
		{
			return DoublyLinkedList.this;
		}
			
	}
	
	private ListNode head; //the first node in the list
	private ListNode tail; //the last node in the list
	private int size; //the size of the list
	
	//Initializes this to an empty list
	public DoublyLinkedList()
	{
		size = 0;
	}
	
	//Creates a list as a copy of another list using the given
	//information
	protected DoublyLinkedList(ListNode head, ListNode tail, int size)
	{
		this.head = head;
		this.tail = tail;
		this.size = size;
	}
	
	//Returns the size of this list
	public int size()
	{
		return size;
	}
	
	//Returns whether this list contains no elements
	public boolean isEmpty()
	{
		return 0 == size;
	}
	
	//Returns the first element of this list
	public T first()
	{
		if(isEmpty())
			throw new NoSuchElementException();
		return head.getElement();
	}
	
	//Returns the last element of this list
	public T last()
	{
		if(isEmpty())
			throw new NoSuchElementException();
		return tail.getElement();
	}
	
	//Returns the element at index in this list
	public T get(int index)
	{
		if(index >= size())
			throw new IndexOutOfBoundsException();
		ListNode node = head;
		for(int i = 0; i < index; i++)
			node = node.getNext();
		return node.getElement();
	}
	
	//Returns whether this list contains o
	public boolean contains(Object o)
	{
		return null != ((T)o).getContainingListNode(this);
	}
	
	//Inserts o at the front of the list
	public synchronized boolean insertHead(T o)
	{
		if(contains(o))
			return false;
		ListNode oldHead = head;
		head = new ListNode(o,null,oldHead);
		if(null == oldHead)
			tail = head;
		else
			oldHead.setPrevious(head);
		size++;
		return true;
	}
	
	//Inserts o at the back of the list
	public synchronized boolean insertTail(T o)
	{
		if(contains(o))
			return false;
		ListNode oldTail = tail;
		tail = new ListNode(o,oldTail,null);
		if(null == oldTail)
			head = tail;
		else
			oldTail.setNext(tail);
		size++;
		return true;
	}
	
	//Inserts o after index in the list
	public synchronized boolean insertAfter(T o, int index)
	{
		if(contains(o))
			return false;
		if(null == head)
		{
			insertHead(o);
		}
		else if(index + 1 == size())
		{
			insertTail(o);
		}
		else
		{
			ListNode left = head;
			for(int i = 0; i < index; i++)
				left = left.getNext();
			
			ListNode right = left.getNext();
			ListNode insertion = new ListNode(o,left,right);
			left.setNext(insertion);
			if(null != right)
				right.setPrevious(insertion);
			
			size++;
		}
		return true;
	}
	
	//Inserts o before index in the list
	public synchronized boolean insertBefore(T o, int index)
	{
		if(contains(o))
			return false;
		if(null == head)
		{
			insertHead(o);
		}
		else if(0 == index)
		{
			insertHead(o);
		}
		else
		{
			ListNode right = head;
			for(int i = 0; i < index; i++)
				right = right.getNext();
			
			ListNode left = right.getPrevious();
			ListNode insertion = new ListNode(o,left,right);
			if(null != left)
				left.setNext(insertion);
			right.setPrevious(insertion);
			
			size++;
		}
		return true;
	}
	
	//Removes the element at index fromt he list
	public synchronized T remove(int index)
	{
		if(index >= size())
			throw new IndexOutOfBoundsException();
		
		ListNode node = head;
		for(int i = 0; i < index; i++)
			node = node.getNext();
		
		ListNode left = node.getPrevious();
		ListNode right = node.getNext();
		
		if(null == left) // i.e.: node==head
			head = head.getNext();
		else
			left.setNext(right);
		
		if(null == right) //i.e.: node==tail
			tail = tail.getPrevious();
		else
			right.setPrevious(left);
		
		size--;
		
		T t = node.getElement();
		node.setElement(null);
		return t;
	}
	
	//Removes o from the list
	public synchronized boolean remove(Object o)
	{
		T t = (T)o;
		ListNode node = t.getContainingListNode(this);
		node.setElement(null);
		if(node == head)
		{
			head = node.getNext();
			if(null != head)
				head.setPrevious(null);
			else
				tail = null;
		}
		else if(node == tail)
		{
			tail = node.getPrevious();
			tail.setNext(null);
		}
		else
		{
			ListNode prev = node.getPrevious();
			ListNode next = node.getNext();
			prev.setNext(next);
			next.setPrevious(prev);
		}
		size--;
		return true;
	}
	
	//Reverses the list in-place
	public synchronized void reverse()
	{
		if(size()==0)
			return;
		head.recurseReverse();
		ListNode tmp = head;
		head = tail;
		tail = tmp;		
	}
	
	private class DoublyLinkedListIterator<U>
								implements ListIterator<U>
	{
		//The previous ListNode returned by this iterator
		private ListNode prev = null;
		//The next list node to be returned by this iterator
		private ListNode current = 
							(ListNode)DoublyLinkedList.this.head;
		//The index of current
		private int nextIndex = 0;
		
		//The last ListNode returned by a call to next or previous
		private ListNode lastReturned;
		
		//Returns whether this iterator is at the end of the list
		public boolean hasNext()
		{
			return null != current;
		}
		
		//Returns the index of the element that will be returned by next
		public int nextIndex()
		{
			return nextIndex;
		}
		
		//Iterates through the next element of this list and returns it
		public synchronized U next()
		{
			if(!hasNext())
				throw new NoSuchElementException();
			
			T t = current.getElement();
			lastReturned = current;
			prev = current;
			current = current.getNext();
			nextIndex++;
			return (U)t;
		}
		
		//Returns whether this iterator is at the front of the list
		public boolean hasPrevious()
		{
			return null != prev;
		}
		
		//Returns the index of the element that will be returned by
		//previous
		public int previousIndex()
		{
			return nextIndex - 1;
		}
		
		//Moves back in the list and returns the passed element
		public synchronized U previous()
		{
			if(!hasPrevious())
				throw new NoSuchElementException();
			
			T t = prev.getElement();
			lastReturned = prev;
			current = prev;
			prev = prev.getPrevious();
			nextIndex--;
			return (U)t;
		}
		
		//Removes the element that would be returned by prev
		//from the list
		public void remove()
		{
			synchronized(DoublyLinkedList.this)
			{
				if(null != current)
				{	
					if(null == prev)
						throw new NoSuchElementException();
					prev.setElement(null);
					prev = prev.getPrevious();
					current.setPrevious(prev);
					if(null != prev)
						prev.setNext(current);
					else
						head = current;
				}
				else
				{
					if(tail == null)
						throw new NoSuchElementException();
					else if(tail==head)
					{
						head.setElement(null);
						tail = null;
						head = null;
						prev = null;
					}
					else
					{
						tail.getPrevious().setNext(null);
						tail.setElement(null);
						tail = tail.getPrevious();
						prev = tail;				
					}
				}
				size--;
			}
			nextIndex--;
		}
		
		//Sets the element of the list node last returned to u
		public void set(U u)
		{
			lastReturned.setElement((T)u);
		}
		
		//Exists only to implement ListIterator
		public void add(U u)
		{
			throw new UnsupportedOperationException();
		}
	}
	
	//Returns an iterator for this list
	public Iterator<T> iterator()
	{
		return new DoublyLinkedListIterator<T>();
	}
	
	//Returns a list whose elements are the same as this list,
	//but in random order
	public DoublyLinkedList<T> shuffledList(long randomSeed)
	{
		Random r = new Random(randomSeed);
		DoublyLinkedList<T> shuffled = new DoublyLinkedList<T>();
		for(T t : this)
		{
			
			int pos = r.nextInt(shuffled.size()+1)-1;
			if(-1==pos)
				shuffled.insertHead(t);
			else
				shuffled.insertAfter(t,pos);
		}
		return shuffled;
	}
	
	/*
	 *Following methods are only present to implement the List
	 *interface
	 *
	 *They can be ignored
	 */
	
	public boolean add(T o)
	{
		return insertTail(o);
	}
	
	public void add(int index, T o)
	{
		insertAfter(o, index);
	}
	
	public boolean addAll(Collection<? extends T> coll)
	{
		boolean b = true;
		for(T t : coll)
			b = add(t) && b;
		return b;
	}
	
	public boolean addAll(int index, Collection<? extends T> coll)
	{
		boolean b = true;
		for(T t : coll)
		{
			b = insertAfter(t, index) && b;
			index++;
		}
		return b;
	}
	
	public void clear()
	{
		while(!isEmpty())
			remove(0);
	}
	
	public boolean containsAll(Collection<?> coll)
	{
		for(Object o : coll)
			if(!contains(o))
				return false;
		return true;
	}
	
	public int indexOf(Object o)
	{
		int idx = 0;
		for(T t : this)
		{
			if(t.equals(o))
				return idx;
			idx++;
		}
		return -1;
	}
	
	public int lastIndexOf(Object o)
	{
		//since this list is only allowed to have one copy
		return indexOf(o); 
	}
	
	public ListIterator<T> listIterator()
	{
		return new DoublyLinkedListIterator<T>();
	}
	
	public ListIterator<T> listIterator(int index)
	{
		ListIterator<T> it = listIterator();
		for(int i = 0; i < index; i++)
			it.next();
		return it;
	}
	
	public boolean removeAll(Collection<?> coll)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean retainAll(Collection<?> coll)
	{
		throw new UnsupportedOperationException();
	}
	
	public T set(int index, T element)
	{
		throw new UnsupportedOperationException();
	}
	
	public List<T> subList(int fromIdx, int toIdx)
	{
		DoublyLinkedList<T> sub = new DoublyLinkedList<T>();
		ListIterator<T> it = listIterator(fromIdx);
		for(int i = fromIdx; i < toIdx; i++)
			sub.add(it.next());
		return sub;
	}
	
	public Object[] toArray()
	{
		Object[] arr = new Object[size()];
		Iterator it = iterator();
		for(int i = 0; i < size(); i++)
		{
			arr[i] = it.next();
		}
		return arr;
	}
	
	public <U> U[] toArray(U[] a)
	{
		U[] arr = (U[])(new Object[size()]);
		Iterator it = iterator();
		for(int i = 0; i < size(); i++)
		{
			arr[i] = (U)it.next();
		}
		return arr;
	}
}