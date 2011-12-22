(ns mao.client
    (:import (java.awt.geom AffineTransform)
              (javax.swing JFrame JPanel JMenu JMenuItem JMenuBar JOptionPane)
              (java.awt Color BorderLayout)
              (java.awt.event KeyAdapter InputEvent ActionListener WindowAdapter)
	      (java.util Collections))
    (:use  (mao images online draggable graphics draggable-online controls chat)
            (darmani utils)  
            (clojure.contrib seq-utils) :reload-all))

(defn shuffled-deck []
  (let [s (javaize all-cards)]
    (Collections/shuffle s)
    (map #(apply make-card %) (dejavaize s))))
	
(def tab (make-draggable-env [] (ref [])))
(def gui (struct gui-components (ref nil) (ref nil) (ref nil)))

;Not robust -- doesn't reset counters and stuff
(defn be-host [tab gui frame]
  (host tab gui)
  (dosync
   (ref-set (:elements (:table tab)) [])
   (send (:adjacencies (:table tab)) (fn [_] {}))
   (doseq [c (shuffled-deck)]
     (add-card! (tab :table) c))
   (ref-set (:manipulators tab) [(ref (make-and-use-native-manipulator (next-manipulator-color!) (next-player-id!)))])
   (ref-set (:hands tab) {@*native-id* (make-hand-surface [])})
   (ref-set (:hand gui) (hand-panel tab @*native-id* handize-arrangement!)))
  (.add frame @(:hand gui) BorderLayout/SOUTH)
  (init-controls tab @(:table gui) :table)
  (init-controls tab @(:hand gui) :hand)
  (let [[panel chat] (new-chatbox online-handler)]
    (.add frame panel BorderLayout/EAST)
    (.updateUI panel)
    (.repaint frame)
    (dosync
     (ref-set (:chat gui) chat))))

(defn be-client [host tab gui frame]
  (dosync
   (ref-set (tab :manipulators) nil))
  (connect host tab gui)
  (while (nil? @(tab :manipulators)))
  (dosync
   (ref-set (:hand gui) (hand-panel tab @*native-id* handize-arrangement!)))
  (.add frame @(:hand gui) BorderLayout/SOUTH)
  (init-controls tab @(:table gui) :table)
  (init-controls tab @(:hand gui) :hand)
  (let [[panel chat] (new-chatbox online-handler)]
    (.add frame panel BorderLayout/EAST)
    (.updateUI panel)
    (.repaint frame)
    (dosync
     (ref-set (:chat gui) chat))))
  


(define-draggable-env-handlers!)
(define-chat-handlers!)

(let [frame (JFrame. "Card-Player Client")
      host-button (JMenuItem. "Host")
      conn-button (JMenuItem. "Connect")
      menu (JMenu. "File")
      menubar (JMenuBar.)
      pan (draggable-surface-panel tab)]
  (dosync
   (ref-set (:table gui) pan))
  (.addActionListener conn-button (proxy [ActionListener] []
				    (actionPerformed [e]
						     (be-client (JOptionPane/showInputDialog frame "Enter where to connect to") tab gui frame))))
  (.addActionListener host-button (proxy [ActionListener] []
				    (actionPerformed [e]
						     (be-host tab gui frame))))
  (.add menu host-button)
  (.add menu conn-button)
  (.add menubar menu)
  (.setJMenuBar frame menubar)
  
  
  (.setBackground (.getContentPane frame) (Color. 0 128 0))
  (.setBackground pan (Color. 0 128 0))
  (.add frame pan BorderLayout/WEST)
  (.setSize frame 1024 768)
  (.updateUI pan)
  (.setVisible frame true)
  (.addWindowListener frame (proxy [WindowAdapter] []
			      (windowClosing [e]
					     (when @online-handler
					        (.close (@online-handler :socket)))))))