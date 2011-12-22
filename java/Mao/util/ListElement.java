/**
 *ListElements store a hash of references to their containing
 *		ListNodes; this can reduce the complexity of list removals
 *from O(n) to O(1)
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 26/2/09
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.util;
 
 import java.util.*;
 
 //Mastery Aspects:
 //HL: Polymorphism
 //SL: 2,3,8,9,10
 
 public interface ListElement<T extends ListElement<T>>
 {
 	//Gets the ListNode in lst contianing this element
 	public DoublyLinkedList<T>.ListNode
 					 getContainingListNode(DoublyLinkedList<T> lst);
 	
 	//Sets the ListNode in lst contianing this element
 	public void setContainingListNode(DoublyLinkedList<T> lst,
 								 DoublyLinkedList<T>.ListNode node);
 	
 	//Called to say that lst no longer contains this element
 	public void removeContainingList(DoublyLinkedList<T> lst);
 	
 	//Returns a Set of all DoublyLinkedLists which contian
 	//this element
 	public Set<DoublyLinkedList<T>> getContainingLists();
 }