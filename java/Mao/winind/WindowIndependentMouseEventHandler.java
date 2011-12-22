/**
 *For Mao online; interface for classes that handle events
 *			from WindowIndependentContainers
 *Author: James Koppel
 *School: Lindbergh High School
 *Date: 11/30/08
 *Computer used: Sandro
 *IDE: JCreator LE v4.0
 */
 
 package mao.winind;
 
 import javax.swing.event.*;
 import java.awt.event.*;
 
 //Master aspects:
 //HL: Inheritance, polymorphism
 //SL: 2,8,9,14
 
 public interface WindowIndependentMouseEventHandler
 				extends MouseInputListener, MouseWheelListener
 {
 	//Sets the WindowIndependentContainer this will be handling events for
 	void setParent(WindowIndependentContainer c);
 }