(ns mao.controls
   (:use (mao online draggable draggable-online)
	 (darmani utils)
	 (clojure set)
	 (clojure.contrib seq-utils)
	 (clojure.contrib.generic functor))
   (:import  (java.awt Rectangle)
	     (java.awt.event MouseWheelListener InputEvent)
	     (javax.swing.event MouseInputAdapter)))

(defn modifier-pressed [mod-mask key-mask]
  (= (bit-and mod-mask key-mask) key-mask))

;;Cheap refactoring only for below function; still need a real refactoring here
(defmacro control-message [type & extra]
  `(send-message @online-handler (*prefixes* :action-performed)
		(struct (type->struct-def ~type) (next-local-msg-id!) nil ~type ~'player (.getX ~'e) (.getY ~'e) ~'surface ~@extra)))

;Bad need of refactoring, this
(defn manipulator-controls [man-ref surface env]
  (let [player (:id @man-ref)]
    (proxy [MouseInputAdapter MouseWheelListener] []
      (mouseMoved [e]
		  (control-message :move))
      (mouseClicked [e]
		    (let [mod-mask (.getModifiers e)
			  mod-mask-ex (.getModifiersEx e)
			  ctrl (modifier-pressed mod-mask InputEvent/CTRL_MASK)
			  shift (modifier-pressed mod-mask InputEvent/SHIFT_MASK)
			  alt (modifier-pressed mod-mask InputEvent/ALT_MASK)
			  left-click (modifier-pressed mod-mask InputEvent/BUTTON1_MASK)
			  right-click (modifier-pressed mod-mask InputEvent/BUTTON3_MASK)]
		      (move-manipulator man-ref (awt-Point->point (.getPoint e)) surface)
		      
					;That's what command-click looks like on this Mac. Ought to test if that breaks anything on Windows
		      (if (and left-click right-click)
			(control-message :flip ctrl)
			(condp = (.getButton e)
			  1 (control-message :pick-up ctrl shift alt)
			  2 (control-message :flip ctrl)
			  3 (control-message :put-down ctrl shift alt (System/currentTimeMillis))))))
      (mouseDragged [e]	    
		    (when (@man-ref :dragged)
		      (control-message :move))
		    (when (and (not (@man-ref :dragged)) (empty? (@man-ref :held)))
		      (control-message :begin-drag)))
      (mouseReleased [e]
		     (when (@man-ref :dragged)
		       (control-message :end-drag)))
      (mouseWheelMoved [e]
		       (control-message :rotate (* (.getWheelRotation e) *rotation-unit*))))))

(defmethod point-in ::card [card point]
  (let [shape (covered-shape card)]
     (.contains shape (point :x) (point :y))))