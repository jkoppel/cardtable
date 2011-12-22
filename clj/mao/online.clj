(ns mao.online
  (:use (clojure.contrib.generic functor)
	;For dekeyword
	(darmani utils))
  (:import (java.net Socket ServerSocket SocketException)
           (java.io ObjectInputStream ObjectOutputStream EOFException)))

(def *port* 8361)
(def *prefixes* {:action-performed 1,
		 :chat-message 2,
		 :create-new-player 3,
		 :game-state-transfer 4,
		 :chat-sent 5})

;New design decision: Every message will have both a source-assigned native id and a server-assigned server ID -- this allows any client
;to uniquely identify a message. For now, all messages will be archived.

(defcounter max-player-id next-player-id! 0)
(defcounter max-local-msg-id next-local-msg-id! 0)
(defcounter max-server-msg-id next-server-msg-id! 0)

(def msg-archive (ref #{}))

(defn add-server-id [msg]
  (assoc msg :serv-id (next-server-msg-id!)))

(defn archive [msg]
  (dosync
   (commute msg-archive conj msg)))

(defn send-message-to [conn prefix obj]
  (.write (conn :oos) prefix)
  (.writeObject (conn :oos) (binding [*print-dup* true] (pr-str obj)))
  (.flush (conn :oos)))

(defn propagate [server prefix obj]
  (doseq [conn (server :connections)]
    (send-message-to conn prefix obj)))

(def client-handler-constructors (atom []))
(def server-handler-constructors (atom []))

;; (defn client-handlers-for [chat-window]
;;   {(*prefixes* :chat-message) (fn [msg _] (disp-chat-line chat-window msg))})

;; (defn server-handlers-for [chat-window]
;;   {(*prefixes* :chat-message) (fn [msg server] (disp-chat-line chat-window msg)
;; 				(propagate server (*prefixes* :chat-message) msg))})


(def online-handler (atom nil))

(defstruct connection :ois :oos :socket)
(defstruct server :type :connections :socket :handlers)

(defn make-server [port handlers]
  (struct server :server #{} (ServerSocket. port) handlers))

;Currently nothing
(defn handshake [conn]
  true)

(defn receive [conn online-master]
  (.start (Thread. (fn []
		     (try
		      (while true
			(let [pre (.read (:ois conn))
			      obj (read-string (.readObject (:ois conn)))]
			  (((@online-master :handlers) pre) obj @online-master)))
		      (catch EOFException e
			(dosync
			 (swap! online-master assoc :connections (disj (@online-master :connections) conn)))))
			;call connection broken handler
		     ))))

(defmulti send-message (fn [online-master & _] (:type online-master)))

(defmethod send-message :server [server prefix msg]
  (((server :handlers) prefix) msg server))

(defmethod send-message :client [client prefix obj]
  (((client :handlers) prefix) obj client)
  (send-message-to (client :connection) prefix obj))

(defstruct player-info :id :conn)

(defn initialize-receive [socket server]
  (let [oos (ObjectOutputStream. (.getOutputStream socket))
	ois (ObjectInputStream. (.getInputStream socket))
	conn (struct connection ois oos socket)]
    (if (handshake conn)
      (let [player (struct player-info (next-player-id!) conn)]
	(send-message @server (*prefixes* :create-new-player) player)
	(swap! server assoc :connections (conj (@server :connections) conn))
	(receive conn server))
      (.close socket))))
      

(defn listen-for-connections [server]
  (.start (Thread. (fn [] 
		     (try
		      (while true
			(initialize-receive (.accept (:socket @server)) server))
		      (catch SocketException e))) )))

(defn host [env gui]
  (reset! online-handler (make-server *port* (apply merge
						    (map #(% env gui) @server-handler-constructors))))
  (listen-for-connections online-handler))

(defstruct client :type :connection :handlers)

(defn make-client [host port handlers]
  (let [socket (Socket. host port)
	ois (ObjectInputStream. (.getInputStream socket))
	oos (ObjectOutputStream. (.getOutputStream socket))
	conn (struct connection ois oos socket)]
    (struct client :client conn handlers)))

(defn connect [host env gui]
  (reset! online-handler (make-client host *port* (apply merge
							 (map #(% env gui) @client-handler-constructors))))
  (receive (@online-handler :connection) online-handler))