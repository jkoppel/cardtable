(ns mao.chat
  (:import
   (javax.swing JTextArea JButton JPanel JTextField JScrollPane BoxLayout)
   (java.awt Dimension)
   (java.awt.event KeyAdapter))
  (:use (mao online)))

(defn define-chat-handlers! []
  (swap! client-handler-constructors conj
	 (fn [_ gui]
	   {(*prefixes* :chat-sent) (fn [line _]
				      (.append @(gui :chat) (str line \newline)))}))
  
  (swap! server-handler-constructors conj
	 (fn [_ gui]
	   {(*prefixes* :chat-sent) (fn [line server]
				      (propagate server (*prefixes* :chat-sent) line)
				      (.append @(gui :chat) (str line \newline)))})))

;Returns [panel textbox]
(defn new-chatbox [online-handler]
  (let [panel (JPanel.)
	text (JTextArea. 40 80)
	scroll (JScrollPane. text)
	entry (JTextField. 80)]
    (.setLayout panel (BoxLayout. panel BoxLayout/Y_AXIS))
    (.setPreferredSize panel (Dimension. 200 768))
    (.add panel scroll)
    (.add panel entry)
    (.addKeyListener entry (proxy [KeyAdapter] []
			    (keyPressed [e]
					(when (= (.getKeyChar e) \newline)
					  (let [lin (.getText entry)]
					    (.setText entry "")
					    (send-message @online-handler (*prefixes* :chat-sent) lin))))))
    (.setEnabled text false)
    [panel text]))

