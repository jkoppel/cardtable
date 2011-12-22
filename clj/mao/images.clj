;;;;BEWARE: img-paths are hardcoded ATM
(ns mao.images
  (:import (java.io File)
              (javax.imageio ImageIO)
	      (java.awt Toolkit Point)
	      (java.awt.image MemoryImageSource))
  (:use (clojure.contrib combinatorics seq-utils)
          (darmani utils)))

(def *deck-length* 52)

;(def def-img-path "/Users/jimmy/projects/clojure/mao/images/")
(def def-img-path "/afs/andrew.cmu.edu/usr8/jkoppel/private/mao/images/")

;;;Until preferences on this are created
(def manipulator-colors (ref (cycle [:deep-red-manipulator :pink-manipulator :blue-manipulator])))

(defn next-manipulator-color! []
  (dosync
   (let [c (first @manipulator-colors)]
     (alter manipulator-colors next)
     c)))

;Again, until preferences on this are created
(defn manipulator-color-for [id]
  (let [mapping {1 :deep-red-manipulator,
		 2 :pink-manipulator,
		 0 :blue-manipulator}]
    (mapping (mod id 3))))







(def misc-filenames (array-map :blue-back "b1fv",
                                                     :blue-back-icon "b1fv_icon",
                                                     :red-back "b2fv",
                                                     :red-back-icon "b2fv_icon",
                                                     :deep-red-manipulator "hand_red_1",
                                                     :pink-manipulator "hand_pink_1",
                                                     :blue-manipulator "hand_blue_1",
                                                     :card-pile "card_pile",
                                                     :blank "blank"))
                                                     
(def manipulator-hotspots {:deep-red-manipulator (struct point 8 0),
			   :pink-manipulator (struct point 8 0),
			   :blue-manipulator (struct point 8 0)})

(def std-imgs (vec (concat (mapcat
			    (fn [i]
			      (let [name (File. (str def-img-path (+ i 1)))]
				(list (ImageIO/read (File. (str name ".png")))
				      (ImageIO/read (File. (str name "_icon.png"))))))
			    (range *deck-length*))
			   (map #(ImageIO/read (File. (str def-img-path % ".png")))
				(vals misc-filenames)))))

(let [offset (* 2 *deck-length*)]
  (def misc-indices ( -> (reduce (fn [hsh name]
				   (-> (assoc hsh name (hsh :idx))
				       (assoc :idx (+ (hsh :idx) 1))))
				 {:idx offset}
				 (keys misc-filenames))
			 (dissoc :idx))))

(defn misc-imgs [key]
  (std-imgs (misc-indices key)))

(let [hsh (reduce (fn [h n]
		    (assoc h (std-imgs n) (std-imgs (+ n 1))))
		  {}
		  (map #(* % 2) (range *deck-length*)))
      hsh (assoc hsh (misc-imgs :red-back) (misc-imgs :red-back-icon))
      hsh (assoc hsh (misc-imgs :blue-back) (misc-imgs :blue-back-icon))]
  (def img->icon hsh))

;I don't really like the use of this; this was created initially as a workaround during the
;time I was desperate to get the thing working to some degree online at any cost, before I embarked on the refactoring campaign
(def img->idx 
     (loop [i 0 h {}]
       (if (>= i (count std-imgs))
	 h
	 (recur (+ i 1) (assoc h (std-imgs i) i)))))

(def suits [:clubs :spades :hearts :diamonds])
(def suit-indices (index-map suits))

(def ranks [:ace :king :queen :jack :10 :9 :8 :7 :6 :5 :4 :3 :2])
(def rank-indices (index-map ranks))

(def all-cards (cartesian-product ranks suits))

(defn card->idx [rank suit]
   (+ (* (rank-indices rank) (count suits)) (suit-indices suit)))
   
(defn card->img [rank suit]
  (std-imgs (* 2 (card->idx rank suit))))
  
(defn card->icon [rank suit]
 (std-imgs (+ 1 (* 2 (card->idx rank suit)))))


(def transparent-cursor
     (let [src (int-array (* 16 16) 0)
	   img (.createImage  (Toolkit/getDefaultToolkit) (MemoryImageSource. 16 16 src 0 16))]
       (.createCustomCursor (Toolkit/getDefaultToolkit) img (Point. 0 0) "invisibleCursor")))

(defn remove-cursor [comp]
  (.setCursor comp transparent-cursor))

;Again, hackish
(defmethod print-dup java.awt.Image [img w]
  (.write w (str "#=(mao.images/std-imgs " (img->idx img) ")")))