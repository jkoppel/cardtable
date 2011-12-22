(ns mao.draggable-online
   (:use (mao online draggable images)
	 (darmani utils)
	 (clojure set)
	 (clojure.contrib seq-utils)
	 (clojure.contrib.generic functor))
   (:import  (java.awt Rectangle)
	     (java.awt.event MouseWheelListener InputEvent)
	     (javax.swing.event MouseInputAdapter)))

;Ought to find a better way to abstract out common properties
(defstruct move-action :nat-id :serv-id :type :player :x :y :surface)
(defstruct rotate-action :nat-id :serv-id :type :player :x :y :surface :theta)
(defstruct flip-action :nat-id :serv-id :type :player :x :y :surface :pile)
(defstruct pick-up-action :nat-id :serv-id :type :player :x :y :surface :pile :flip :straightening)
(defstruct put-down-action :nat-id :serv-id :type :player :x :y :surface :pile :flip :carefully :rand-seed)
(defstruct begin-drag-action :nat-id :serv-id :type :player :surface :x :y)
(defstruct end-drag-action :nat-id :serv-id :type :player :surface :x :y)

(def type->fn {:move nil, ;move-manipulator always called by handler
	       :rotate do-rotate,
	       :flip do-flip,
	       :pick-up pick-up,
	       :put-down put-down,
	       :begin-drag begin-dragging,
	       :end-drag stop-dragging})

(def type->struct-def {:move move-action,
		       :rotate rotate-action,
		       :flip flip-action,
		       :pick-up pick-up-action,
		       :put-down put-down-action,
		       :begin-drag begin-drag-action,
		       :end-drag end-drag-action})

(defn action-performed-handler [action env gui]
  (let  [prop-seq (vals action)
	 prop-seq (nnext prop-seq) ;skipping IDs
	 type (first prop-seq)
	 func (type->fn type)
	 prop-seq (rest prop-seq)
	 player (first prop-seq)
	 prop-seq (rest prop-seq)
	 x (first prop-seq)
	 prop-seq (rest prop-seq)
	 y (first prop-seq)
	 prop-seq (rest prop-seq)
	 surface-indic (first prop-seq)
	 prop-seq (rest prop-seq)
	 man-ref (first (filter #(= (:id @%) player)
				@(:manipulators env)))]
    (move-manipulator man-ref (struct point x y) surface-indic)
    (when func
      (apply func man-ref env prop-seq))
    (.repaint @(:table gui))
    (.repaint @(:hand gui))))

;;;Redundancy checking is a bit flimsy; needs be generalized. Heck, message handling as a whole could use some macro-love
;;;It is important messages are archived *after* propagation -- that way, the max id is indicative of what has already gone out
(defn define-draggable-env-handlers! []
  ;;;Action-performed handler
  (swap! client-handler-constructors conj
	 (fn [env gui]
	   {(*prefixes* :action-performed) (fn [action client]
					     (when (not (@msg-archive action))
					       (archive action)
					       (action-performed-handler action env gui)))}))
  
  (swap! server-handler-constructors conj
	 (fn [env gui]
	   {(*prefixes* :action-performed) (fn [action server]
					     (let [;action (add-server-id action)
						   ;server ids are messing up archiving ATM
						   action action]
					       (archive action)
					       (propagate server (*prefixes* :action-performed) action)
					       (action-performed-handler action env gui)))}))

  ;;;Create-new-player handler
  (swap! client-handler-constructors conj
	 (fn [env _]
	   {(*prefixes* :create-new-player) (fn [id client]
					      (dosync
					       (let [new-manipulator (make-alien-manipulator (manipulator-color-for id) id)
						     new-hand (make-hand-surface [])]
						 (alter (env :manipulators) conj (ref new-manipulator))
						 (alter (env :hands) assoc id new-hand))))}))

  (swap! server-handler-constructors conj
	 (fn [env _]
	   {(*prefixes* :create-new-player) (fn [info server]
					      (dosync
					       (let [id (info :id)
						     new-manipulator (make-alien-manipulator (manipulator-color-for id) id)
						     new-hand (make-hand-surface [])]
						 (alter (env :manipulators) conj (ref new-manipulator))
						 (alter (env :hands) assoc id new-hand)))

					       (propagate server (*prefixes* :create-new-player) (info :id))
					       (let [env-data (assoc env :manipulators
								     (ref (map ref (map #(if (= (:id %) (info :id))
										      (assoc % :type :native)
										      (assoc % :type :alien))
											(map deref @(env :manipulators))))))]
						 (send-message-to (info :conn) (*prefixes* :game-state-transfer) {:env env-data,
														  :max-z @max-z,
														  :max-id @max-id
														  :id (info :id)})))}))

  ;Game-state received
  (swap! client-handler-constructors conj
	 (fn [env pan]
	   {(*prefixes* :game-state-transfer) (fn [data client]
						(reset! *native-id* (data :id))
						(dosync
						 (ref-set max-z (data :max-z))
						 (ref-set max-id (data :max-id))
						 (ref-set (env :manipulators) @((data :env) :manipulators))
						 (ref-set ((env :table) :elements) @(((data :env) :table) :elements))
						 (ref-set (env :hands) @((data :env) :hands))
						 (send ((env :table) :adjacencies) (fn [_] @(((data :env) :table) :adjacencies))))
					;(init-controls env pan)
)})))