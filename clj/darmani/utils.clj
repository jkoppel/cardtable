(ns darmani.utils
  (:use (clojure.contrib seq-utils def))
  (:import (java.awt.geom AffineTransform Area)
	   (java.awt Polygon Point)
           (java.util Comparator Random HashSet ArrayList HashMap)))

(defmacro defcounter [var-name inc-name start]
  `(do
     (def ~var-name (ref ~start))
     (defn ~inc-name []
       (dosync (alter ~var-name + 1)))))

;Used for next function for creating structs
(defn prototyped-struct [& args]
  (let [ents (partition 2 args)
	keys (map first ents)
	vals (map second ents)
	sdef (apply create-struct keys)]
    (apply struct sdef vals)))
    

;Hackish; only kinda works when there are extensions to the structure. Fixes a bug in current Clojure
(defmethod print-dup clojure.lang.PersistentStructMap [struct w]
  (.write w (str "#=" (pr-str `(prototyped-struct ~@(interleave (keys struct) (vals struct)))))))

;I don't know how bad of an idea it is to serialize refs and agents
(defmethod print-dup clojure.lang.Ref [r w]
  (.write w (str "#=(ref " (pr-str @r) ")")))

(defmethod print-dup clojure.lang.Agent [a w]
  (await a)
  (.write w (str "#=(agent " (pr-str @a) ")")))

(defn ref? [o]
  (isa? (class o) clojure.lang.Ref))

(defn agent? [o]
  (isa? (class o) clojure.lang.Agent))

;I thought this already existed
(defn seqable? [x]
  (isa? (class x) clojure.lang.Seqable))

;I could've sworn this already existed in clojure-contrib, but I couldn't find it after looking very hard
;Will destroy a data struct, but keep it of the same type
;Only sequentials, maps, and sets exist in my land!
;Yes, this will convert StructMaps to PersistentHashMaps. Don't rely on it for permanent code
;;;;Wait, should not be too hard to make that part go away, making this a viable thing to use
(defn polymap [keyf valf xs]
  (cond
    (vector? xs)
      (vec (map valf xs))
    (sequential? xs)
      (map valf xs)
    (set? xs)
      (set (map valf (seq xs)))
    (map? xs)
      (apply merge (map (fn [ent]
			  {(keyf (key ent)) (valf (val ent))})
			(seq xs)))
    true
      xs))

;I'll knife(-hand) a bastard who uses data structs for keys
(defn deep-polymap [keyf valf xs]
  (polymap keyf #(if (seqable? %)
		   (deep-polymap keyf valf %)
		   (valf %))
	   xs))

(def *primitives* '(Boolean Byte Character Double Float Integer Long Short))

(defstruct point :x :y)

(defn point-add [p1 p2]
  (struct point (+ (p1 :x) (p2 :x)) (+ (p1 :y) (p2 :y))))

(defn point-subtract [p1 p2]
  (struct point (- (p1 :x) (p2 :x)) (- (p1 :y) (p2 :y))))
  
(defn awt-Point->point [p]
  (struct point (.getX p) (.getY p)))

(defn point->awt-Point [p]
  (Point. (:x p) (:y p)))

(defn index-map [xs]
   "Returns a map mapping elements in xs to the index they last occur"
   (reduce (fn [hsh ent]
                   (assoc hsh (fnext ent) (first ent)))
               {}
               (indexed xs)))
               
(defmacro affine-transform [& args]
  `(let [t# (AffineTransform.)]
      (doto t# ~@args)
      t#))
      
(defn fn->comp [f]
  (proxy [Comparator] []
    (compare [a b]
         (f a b))))

(defn rand-range [low high]
  (let [x (rand-int (+ 1 (- high low)))]
    (+ x low)))

(defn seeded-rng [seed]
     (Random. seed))

(defn seeded-rand-int [high rng]
  (.nextInt rng high))

(defn seeded-rand-range [low high rng]
  (let [x (seeded-rand-int (+ 1 (- high low)) rng)]
    (+ x low)))

(defn rotated-rectangle [x1 y1 x2 y2 theta]
  (let [ps [(struct point x1 y1)
	    (struct point x2 y1)
	    (struct point x2 y2)
	    (struct point x1 y2)]
	cent-x (/ (+ x1 x2) 2)
	cent-y (/ (+ y1 y2) 2)
	rotater (affine-transform (.rotate theta cent-x cent-y))
	poly (Polygon.)
	rot-ps (map #(awt-Point->point
		          (.transform rotater (point->awt-Point %) (Point.)))
		    ps)]
    (doseq [p rot-ps]
      (.addPoint poly (:x p) (:y p)))
    poly))
    
(defn intersects [shape1 shape2]
  (let [a1 (Area. shape1)
	a2 (Area. shape2)]
    (.intersect a1 a2)
    (not (.isEmpty a1))))

(defn draw-img-at [g img p]
  (let [trans (affine-transform (.translate (p :x) (p :y)))]
    (.drawImage g img trans nil)))


(defn degrees->rad [deg]
  (/ (* deg Math/PI) 180))

;Poor workaround for Clojure data structs not being serializable
(defn javaize [obj]
  (cond
   (set? obj)
    (HashSet. obj)
   (sequential? obj)
    (ArrayList. obj)
   (map? obj)
    (HashMap. obj)
   true
    obj))

(defn dejavaize [obj]
  (cond
    (isa? (class obj) java.util.Map)
     (apply merge (map (fn [ent] {(.getKey ent) (.getValue ent)})
		       (seq obj)))
    (isa? (class obj) java.util.Set)
     (set obj)
    (isa? (class obj) java.util.List)
     (vec obj)
    true
     obj))

(defn keyword->sym [k]
  (if (keyword? k)
    (symbol (name k))
    k))

(defn  sym->keyword [s]
  (if (symbol? s)
    (keyword (name s))
    s))
 


;Is it dangerous/transiently-working to depend on testing for implementing IDeref?
;Gah, can't write because I can't find that polymorphic map function which I thought existed
;(defn deep-deref [s]
;  (

;(defmacro make-counter [name]
;  `(do
;     (def ~name (ref 0))
;     (defn 

;; (defmulti type-writer class)
;; (defmethod type-writer String [t] (memfn writeUTF))
;; (defmethod type-writer :default [t] (memfn writeObject))

;; (defn- type-writer-helper [type-name]
;;   `(defmethod type-writer ~type-name [t#] (memfn ~(symbol (str "write" type-name)))))


;; (defmacro- define-type-writers []
;;   `(do ~@(map type-writer-helper *primitives*)))

;; (define-type-writers)

;; (defmulti type-reader class)
;; (defmethod type-reader String [t] (memfn readUTF))

;; (defn- type-reader-helper [type-name]
;;   `(defmethod type-reader ~type-name [t#] (memfn ~(symbol (str "read" type-name)))))


;; (defmacro- define-type-readers []
;;   `(do ~@(map type-reader-helper *primitives*)))

;; (define-type-readers)

;; (defmacro defmessage [message-name & typed-names] 
;;   `(do
;;      (defstruct ~message-name ~@(map #(keyword (name %)) typed-names))

;;      (defn ~(symbol (str "read-" message-name)) [ois#]
;;        (struct ~message-name ~@(map (fn [v] `((type-reader (^~v :tag)) ois))
;; 				    typed-names))

;;   (defn read-struct)
;;   (defn write-struct [oos] ...))

;; ) ;for serialization