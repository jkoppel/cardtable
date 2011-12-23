Description
===========

This is a graphical environment for playing card games online.
You and other players move cards freely on a virtual table. This
is capable of playing nearly any card game, but was primarily
designed for Mao, which allows players to modify the rules as a
core game mechanic, making such unrestricted motion necessary.

The interface consists of the card table and a minimalist chat window.
Your cursor is represented by a hand icon, called a "manipulator"
to distinguish from the card game concept. Players can see each other's
manipulators as they move across and interact with the table and cards.

This repo contains both the original Java version, and its Clojure rewrite.
The Clojure version is faster, contains more functionality, and a sixth as much code.

The Clojure version also contains a private area for storing cards (e.g.:
your hand). The Java version lacks this, and is therefore great for playing
War. The protocol for desynch-checking and rollbacks is partially implemented
in the Java version, and not at all in the Clojureversion, making this impractical
for playing people not in the same room unless you implement your own concurrency
policy (i.e.: adhere to a strict turn-taking order).


Controls
========

Below, a "pile" refers to either a contiguous arrangement of cards on the table, or to all cards held.

 - Left click picks up a card
 - Right click drops a card, with some chaos in where exactly it falls
 - When no cards are held, drag the mouse over a card to drag it
 - Mouse wheel rotates all cards held, or a card on the table if none
 - Middle click flips all cards held, or a card on the table if none
 - Space creates a random card
 - Pile modifier: Holding Ctrl causes an action to apply to a whole pile
 - Flip modifier: Holding Shift when picking up or dropping combines the action with a flip
 - Carefulness modifier: Holding Alt when picking up cards straightens their orientation.
   Holding Alt when dropping cards drops them precisely where they are, without drop chaos.

History
=======



I wrote the Java version my senior year of high school, in 2008-2009,
and used it to satisfy an IB Diploma requirement, meaning the Java
version is extremely well-documented, and contains its own implementation
of several data structures. I then learned Clojure for the purposes of
porting it, and worked on the Clojure version through my first semester
of college. I wrote this at a time when I was barely familiar with DAGs,
and rediscovered the topological sort, naming it "linearized view." If
you drop a pile of several hundred cards, the program will freeze as it
compares every card to determine whether it should create an adjacency.
Similar things can happen if you rotate a card onto another card
when both are not overlapping, but are part of a large contiguous clump,
as it walks the adjacency DAG to determine whether to place the rotated
card above or below the other.

I left this project in November 2009 to compete in (and win) a Facebook
app competition. The Clojure version is heavily annotated with notes,
which I've left in for authenticity. Both versions require that the images
lie in a hard-coded directory. Overall, it's incomplete, but nonetheless
quite functional, and very engaging.


The original plan called for the display to rotate as if players were seated
at a circular table. The Java version is designed to allow the view to rotate,
though this code is not currently in use.