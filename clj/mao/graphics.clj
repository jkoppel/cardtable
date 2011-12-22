(ns mao.graphics
  (:import (java.awt.geom AffineTransform)
              (javax.swing JFrame JPanel BorderFactory)
              (java.awt Color Dimension)
              (java.awt.event MouseAdapter))
   (:use (mao images draggable controls)
           (darmani utils)  
           (clojure.contrib seq-utils)))

(defstruct gui-components :table :chat :hand)

(def *max-icons* 5)

(defmulti draw (fn [ & [comp]] (comp :type)))

(defmethod draw :mao.draggable/card [comp g]
  (let [cent (centroid comp)
         trans (affine-transform (.rotate (comp :rotation) (cent :x) (cent :y))
                                         (.translate (comp :x) (comp :y)))]
     (.drawImage g (comp :image) trans nil)))
      

;;;;I ought to experiment with different placements of cards relative the manipulator
(defn draw-manipulator [man g]
  (let [x (man :x)
         y (man :y)
         positioned-cards (map #(-> % (assoc :x x)
                                                   (assoc :y y))
                                          (man :held))]
      (doseq [card positioned-cards]
         (draw card g))
         
      (when (man :dragged)
          (let [dragged (man :dragged)
                 dragged (assoc dragged :x (+ (man :x) (dragged :x)))
                 dragged (assoc dragged :y (+ (man :y) (dragged :y)))]
              (draw dragged g)))

      (draw-img-at g (man :image) (point-subtract (struct point x y) (man :hotspot)))

      (let [ic-x x
	    ic-y (+ y (.getHeight (man :image) nil))]
	(if (<= (count (man :held)) *max-icons*)
	  (loop [ic-x ic-x crds (reverse (man :held))]
	    (when (seq crds)
	      (draw-img-at g ((first crds) :icon) (struct point ic-x ic-y))
	      (recur (+ ic-x (.getWidth ((first crds) :icon) nil)) (rest crds))))
	  (do 
	    (draw-img-at g (misc-imgs :card-pile) (struct point ic-x ic-y))
	    (.drawString g
			 (str "x" (count (man :held)))
			 (int (+ ic-x (.getWidth (misc-imgs :card-pile) nil)))
			 (int (+ ic-y (.getHeight (misc-imgs :card-pile))))))))))

(defmethod draw :mao.draggable/table [tab manips g]
  (let [sorted (sort-by :z @(tab :elements))]
    (doseq [drag sorted]
      (draw drag g)))
  (doseq [man-ref manips]
    (when (= (@man-ref :surface) :table)
      (draw-manipulator @man-ref g))))

(defmethod draw :mao.draggable/hand [hand manips id g]
  (let [sorted (sort-by :z @(hand :elements))]
    (doseq [drag sorted]
      (draw drag g)))
  (doseq [man-ref manips]
    (when (and (= (@man-ref :id) id) (= (@man-ref :surface) :hand))
      (draw-manipulator @man-ref g))))

(defn draggable-surface-panel [env]
  (let [prox (proxy [JPanel] []
                   (paintComponent [g]
                       (proxy-super paintComponent g)
                       (draw (:table env) @(:manipulators env) g)))]
    (.setPreferredSize prox (Dimension. 700 400))
    (.setBorder prox (BorderFactory/createLineBorder Color/RED 2))
    (remove-cursor prox)
    prox))

;passing in the handize-arrangement! function is quite a bit hackish. Curse circular dependencies
(defn hand-panel [env id update-fn]
  (let [prox (proxy [JPanel] []
                   (paintComponent [g]
		       (update-fn (@(:hands env) id))
                       (proxy-super paintComponent g)
                       (draw (@(:hands env) id) @(:manipulators env) id g)))]
    (.setPreferredSize prox (Dimension. 700 120))
    (.setBorder prox (BorderFactory/createLineBorder Color/BLUE 2))
    (.setBackground prox (Color. 0 128 128))
    (remove-cursor prox)
    prox))

(defn init-controls [env panel surface]
  (doseq [man @(env :manipulators)]
    (when (= (:type @man) :native)
      (let [controls (manipulator-controls man surface env)]
	(.addMouseMotionListener panel controls)
	(.addMouseListener panel controls)
	(.addMouseWheelListener panel controls)))))