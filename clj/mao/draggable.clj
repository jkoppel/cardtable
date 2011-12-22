;WARNING: Known (well, expected -- never witnessed) bug, virtually impossible to reproduce:
;                 There are a few calls to (await ...) right before a transaction. If that transaction is restarted and relevant details
;                 are changed, bad stuff can happen. Solution: wait until Clojure has support for executing code upon transaction restart.
;                 There is a high chance this bug will never strike until then.
;
;Major time-savings: for consecutive rotations, memoize aboves
;
;Right now my priorities are to just hack out the War online test, hack together hands, then debut Mao Online.
;Then I can start fantasizing how I'l turn everything into the super modifiable customizable robust secure totally killer-app Scrounj engine.
;
;Oh God, my message handlers are horribly mixed up between sending and receiving. Eek!
;
;Code so bad I'd call it broken: Look where clients are initializing their controls! Can't init in handler because of circular dependency
;Also, the prepping/deprepping for serialization is fugly.
;
;First priority once I get basic connection working: No ref of vector of refs for storing manipulators! Well, at least I *think* that's a bad thing. Ask? The outside ref is modified so rarely, and a conj does not affect any of them
;
;More refactoring: message definitions (common properties), message structure construction, message handling
;Yet more: Better data structure for message archives
;
;Also need: guarding against the case where messages are sent in between handing the state to a new player and adding its connection
;
;
;
;I was trying to figure out how to make the adjacencies agent a watcher and remove all refs from inside the draggable surface so as
;to make everything play nice in transactions; I think I could use transactions rather than freezing for adding new players.
;I was also thinking validators could be used to detect conflicts.
;
;Also need: proper shutdown procedure
;
;Misc usability note: When I flip over cards in my hand, their position relative the manipulator should flip about some axis.
; --- that is, their orientation should be reflected over the x-axis

(ns mao.draggable
   (:use (mao images online)
	 (darmani utils)
	 (clojure set)
	 (clojure.contrib seq-utils)
	 (clojure.contrib.generic functor))
   (:import  (java.awt Rectangle)
	     (java.awt.event MouseWheelListener InputEvent)
	     (javax.swing.event MouseInputAdapter)))

(def *drop-chaos* 10)

;Easier if it's integral; ergo in degrees; will convert
(def *drop-rot-chaos* 9)
(def *rotation-unit* (/ Math/PI 45))

(derive ::card ::draggable)

(defstruct table-surface :type :elements :adjacencies)
(defstruct draggable-env :type :table :hands :manipulators)

(defn make-table-surface [elements]
  (struct table-surface ::table (ref elements) (agent {})))

(defn make-hand-surface [elements]
  (struct table-surface ::hand (ref elements) (agent {})))

(defn make-draggable-env [elements manipulators]
  (struct draggable-env ::draggable-env (make-table-surface elements) (ref {}) manipulators))

(defcounter max-z inc-z! 0)
(defcounter max-id next-id! 0)


;Rotation in radians
(defstruct draggable-component :id :type :x :y :rotation :image :icon :z)
         
(defmulti covered-shape :type)
      
(defmulti point-in (fn [& [comp]] (comp :type)))

(defmethod covered-shape ::card [card]
  (let [width (.getWidth (card :image) nil)
	height (.getHeight (card :image) nil)
	x1 (card :x)
	y1 (card :y)
	x2 (+ width x1)
	y2 (+ height y1)]
    (rotated-rectangle x1 y1 x2 y2 (card :rotation))))

(defmethod point-in ::card [card point]
  (let [rect (covered-shape card)]
     (.contains rect (point :x) (point :y))))
     
(defn top-card [surface point]
  (last (sort-by :z (filter #(point-in % point) @(surface :elements)))))

; Rotation in radians
(defstruct card-component :id :type :x :y :rotation :image :back :icon :rank :suit :face-up :z)

(defn make-card [rank suit]
  (struct card-component (next-id!) ::card 0 0 0 
                    (card->img rank suit) (misc-imgs :red-back) (card->icon rank suit) rank suit true
                                                 (inc-z!)))
                                                 
(defn flipped [card]
  (if (card :face-up)
     (-> card (assoc :face-up false)
	 (assoc :image (card :back))
	 (assoc :icon (img->icon (card :back))))
     (-> card (assoc :face-up true)
	 (assoc :image (card->img (card :rank) (card :suit)))
	 (assoc :icon (card->icon (card :rank) (card :suit))))))

(defn rotated [card amt]
  (assoc card :rotation (+ (card :rotation) amt)))

(defn make-rand-card []
  (let [rank (rand-elt ranks)
         suit  (rand-elt suits)]
      (make-card rank suit)))

(defmulti centroid :type)

(defmethod centroid ::card [comp]
  (let [width (.getWidth (comp :image) nil)
         height (.getHeight (comp :image) nil)]
      (struct point (+ (comp :x) (quot width 2))
                        (+ (comp :y) (quot height 2)))))
                        

(defstruct adj-graph-node :above :below)

(defn adjacencies-add [adjs draggables added]
  (let [draggables (sort-by :z draggables)
         shape (covered-shape added)
         ID (:id added)]
    (loop [xs draggables graph adjs aboves #{} belows #{}]
              (if (seq xs)
                 (let [d (first xs)]
                   (if (and (not (= ID (:id d)))
                               (intersects shape (covered-shape d)))
                      (if (< (:z d) (:z added))
                        (recur (rest xs) 
                                 (update-in graph [(:id d) :above] conj ID)
                                 aboves
                                 (conj belows (:id d)))
                        (recur (rest xs)
                                 (update-in graph [(:id d) :below] conj ID)
                                 (conj aboves (:id d))
                                 belows))
                     (recur (rest xs) graph aboves belows)))
                 (assoc graph ID (struct adj-graph-node aboves belows))))))

(defn adjacencies-rem [adjs removed]
  (-> (fmap (fn [el]
                     (if (number? el)
                        el
                        (fmap #(disj % (:id removed)) el)))
                adjs)
        (dissoc (:id removed))))

(defn component-for [graph id & [exclusions]]
  ;default value of opt arg
  (let [exclusions (if exclusions
                            exclusions
                            #{})
         adjs (union ((graph id) :above) ((graph id) :below))
         news (difference adjs exclusions)
         component-so-far (union adjs exclusions #{id})]
      (reduce (fn [comp node]
                     (component-for graph node comp))
                  component-so-far
                  (seq news))))

;(defn aboves [graph id]
;  (conj (apply union (map (partial aboves graph) (seq ((graph id) :above))))
;	id))

(defn aboves [graph id & [exclusions]]
  ;default value of opt arg
  (let [exclusions (if exclusions
                            exclusions
                            #{})
         adjs ((graph id) :above)
         news (difference adjs exclusions)
         component-so-far (union adjs exclusions #{id})]
      (reduce (fn [comp node]
                     (aboves graph node comp))
                  component-so-far
                  (seq news))))
  
     

(defn add-card! [surface to-add]
  (alter (surface :elements) conj to-add)
  (send (surface :adjacencies) adjacencies-add @(surface :elements) to-add))


(defn remove-card! [surface to-grab]
    (alter (surface :elements) #(remove (fn [x] (= (:id x) (:id to-grab)))
					%))
    (send (surface :adjacencies) adjacencies-rem to-grab)
    to-grab)

;Convention: cards held by manipulators will have positions set at (0,0)
;Cards dragged will have positions set at their offset from the manipulator x,y

(def *native-id* (atom nil))

;:surface is either :table or :hand for now
(defstruct manipulator :type :x :y :surface :held :dragged :image :hotspot :id)

(defn make-manipulator [type color player-id]
     (struct manipulator type 0 0 :table [] nil
	     (misc-imgs color)
	     (manipulator-hotspots color)
	     player-id))

(defn make-native-manipulator [color player-id]
  (make-manipulator :native color player-id))

(defn make-alien-manipulator [color player-id]
  (make-manipulator :alien color player-id))

(defn make-and-use-native-manipulator [color player-id]
  (reset! *native-id* player-id)
  (make-native-manipulator color player-id))

(defn move-manipulator [man-ref p surface-indic]
  (dosync
   (alter man-ref #(-> % (assoc :x (:x p))
		       (assoc :y (:y p))))
   (alter man-ref #(assoc % :surface surface-indic))))

(defn pick-up [man-ref env pile flip straightening]
  (let [surface (if (= (@man-ref :surface) :table)
		  (env :table)
		  ((env :hands) (@man-ref :id)))]
    (if pile
      (await (surface :adjacencies)))
    (dosync
     (let [p (struct point (@man-ref :x) (@man-ref :y))
	   touched-card (top-card surface p)]
       (when touched-card
	 (let [cards (if pile
		       (let [comp (component-for @(surface :adjacencies) (:id touched-card))
			     cards-in-comp (filter #(comp (:id %)) @(surface :elements))]
			 (doseq [c cards-in-comp]
			   (remove-card! surface c))
			 (sort-by :z cards-in-comp))
		       [(remove-card! surface touched-card)])]
	   (when (not (empty? (filter identity cards)))
	     (let [cards (map #(-> % (assoc :x 0)
				   (assoc :y 0))
			      cards)
		   cards (if flip
			   (reverse (map flipped cards))
			 cards)
		   cards (if straightening
			   (map #(assoc % :rotation 0) cards)
			   cards)]
	       (alter man-ref assoc :held (concat cards (@man-ref :held)))))))))))
  
(defn put-down [man-ref env pile flip carefully rand-seed]
  (let [surface (if (= (@man-ref :surface) :table)
		  (env :table)
		  ((env :hands) (@man-ref :id)))]
    (dosync
     (let [rng (seeded-rng rand-seed)
	   cards (if pile
		   (:held @man-ref)
		   [(first (:held @man-ref))])]
       (when (not (empty? (filter identity cards)))
	 (alter man-ref assoc :held (if pile
				      []
				    (rest (:held @man-ref))))
	 (let [cards (if flip
		       (reverse (map flipped cards))
		       cards)
	       cards (if carefully
		       (map #(-> % (assoc :x (@man-ref :x))
				 (assoc :y (@man-ref :y)))
			    cards)
		       (map #(-> % (assoc :x (+ (:x @man-ref) (seeded-rand-range (- *drop-chaos*) *drop-chaos* rng)))
				 (assoc :y (+ (:y @man-ref) (seeded-rand-range (- *drop-chaos*) *drop-chaos* rng)))
				 (assoc :rotation (+ (:rotation %) (degrees->rad (seeded-rand-range (- *drop-rot-chaos*) *drop-rot-chaos* rng)))))
			    cards))
	       cards (map #(assoc % :z (inc-z!)) cards)]
	   (doseq [c cards]
	     (add-card! surface c))))))))
  
(defn do-flip [man-ref env pile]
  (let [surface (if (= (@man-ref :surface) :table)
		  (env :table)
		  ((env :hands) (@man-ref :id)))]
    (dosync
     (if (empty? (@man-ref :held))
       (let [p (struct point (@man-ref :x) (@man-ref :y))
	     touched-card (top-card surface p)]
	 (when touched-card
	   (if pile
	     (await (surface :adjacencies)))
	   (let [cards (if pile
			 (component-for @(surface :adjacencies) (:id touched-card))
			 #{(:id touched-card)})
		 all @(surface :elements)
		 with-flips (map #(if (cards (:id %))
				    (flipped %)
				    %)
				 all)]
	     (ref-set (surface :elements) with-flips))))
       (alter man-ref assoc :held (reverse (map flipped (@man-ref :held))))))))
  
(defn begin-dragging [man-ref env]
  (let [surface (if (= (@man-ref :surface) :table)
		  (env :table)
		  ((env :hands) (@man-ref :id)))]
    (dosync
     (let [p (struct point (@man-ref :x) (@man-ref :y))
	   card (top-card surface p)]
       (when card
	 (remove-card! surface card)
	 (let [card (update-in card [:x] - (@man-ref :x))
	       card (update-in card [:y] - (@man-ref :y))]
	   (alter man-ref assoc :dragged card)))))))
  
(defn stop-dragging [man-ref env]
  (let [surface (if (= (@man-ref :surface) :table)
		  (env :table)
		  ((env :hands) (@man-ref :id)))]
    (dosync
     (when (@man-ref :dragged)
       (let [card (@man-ref :dragged)
	     card (update-in card [:x] + (@man-ref :x))
	     card (update-in card [:y] + (@man-ref :y))
	     card (assoc card :z (inc-z!))]
	 (alter man-ref assoc :dragged nil)
	 (add-card! surface card))))))
  
(defn do-rotate [man-ref env amt]
  (when (= (@man-ref :surface) :table)
    (let [surface (env :table)]
      (cond
;Could rotate the dragged card so that it's not longer touching the manipulator -- would look funny.
;Maybe have it rotate about point of touchingmanipulator instead? Would need to transform its location in addition to rotating
;    (@man-ref :dragged)
;      (swap! man-ref assoc :dragged (rotated (man-ref :dragged) amt))
       (not (empty? (@man-ref :held)))
         (dosync (alter man-ref assoc :held (map #(rotated % amt) (@man-ref :held))))
       true
	(do
	  (await (surface :adjacencies))
	  (dosync
	   (let [p (struct point (@man-ref :x) (@man-ref :y))
		 card (top-card surface p)]
	     (when card
	       (let [above-set (aboves @(surface :adjacencies) (card :id))
		     above-seq (sort-by :z (filter #(above-set (:id %)) @(surface :elements)))
		     raised (map #(assoc % :z (inc-z!)) above-seq)
		     rotated-card (assoc (rotated card amt) :z (:z (first raised)))
		     transformed (cons rotated-card (rest raised))
		     transformed-map (reduce (fn [hsh el] (assoc hsh (:id el) el))
					     {}
					     transformed)]
					;We just need to change the z of the aboves; no need to touch the adjacency graph save for the rotated card
		 (alter ((env :table) :elements) (fn [elts] (map #(if (above-set (% :id))
								    (transformed-map (% :id))
								    %)
								 elts)))
		 (remove-card! surface rotated-card)
		 (add-card! surface rotated-card))))))))))
  
(defn sequential-adjacencies [ids]
  (apply merge
	 (if (= (count ids) 1)
	   {(first ids) (struct adj-graph-node #{} #{})}
	   {})
	 (if (> (count ids) 1)
	   {(first ids) (struct adj-graph-node #{} #{(fnext ids)})}
	   {})
	 (if (> (count ids) 1)
	   {(last ids) (struct adj-graph-node #{(nth ids (- (count ids) 2))} #{})}
	   {})
	 (map (fn [a b c]
		{b (struct adj-graph-node #{a} #{c})})
	      ids
	      (next ids)
	      (nnext ids))))

(defn handize-arrangement! [hand]
  (let [cards @(:elements hand)
	cards (map #(assoc % :y 0) cards)
	cards (map #(assoc % :rotation 0) cards)
	cards (map (fn [card n] (assoc card :x (* 20 n)))
		   cards
		   (range (count cards)))]
    (dosync
     (ref-set (hand :elements) (vec cards))
     (send (:adjacencies hand) (fn [_]
				 (sequential-adjacencies (map :id cards)))))
    (await (:adjacencies hand))))
    