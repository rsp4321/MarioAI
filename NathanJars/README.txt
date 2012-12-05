Automatic Level Evolution
by Nathan Sorenson
Simon Fraser University, Canada
nds6@sfu.ca

The approach taken is a hybrid between a high-level GA and a constraint-solver engine.
The GA seeks to attain big-picture level design goals, such as providing the player
with bursts of challenge, and variety in scenery. The constraint solver is a modified
version of the JaCoP java constraint solver library that has been adjusted to suit the
purposes of this project. the constraint solver ensures that that the level is playable,
and that no level configurations occur that appear distinctly "wrong."

All required jar files are provided. The levels are generated in the class
nathansorenson.NathanSorensonLevelGenerator, which implements the LevelGenerator class.
The bulk of the program was written in the language Clojure, but it should run on
a recent JVM (1.5+) without fuss. It is recommended that the jvm be launched with a very 
large heap size.

Please note that since a GA is used, the generateLevel method will block for 50 seconds
to allow some evolution to take place.




More details of the presented algorithm has been previously published:

Sorenson, N. & Pasquier, P. (2010).
"The Evolution of Fun: Automatic Level Design through Challenge Modeling" 
Proceedings of the First International Conference on Computational Creativity (ICCCX), 
ACM Press, Lisbon, Portugal, 258 267.

Sorenson, N. & Pasquier, P. (2010).
"Towards a Generic Framework for Automated Video Game Level Creation" 
International Conference on Evolutionary Computation in Games (EvoGames), 
Istanbul, Springer, 2010.

Pre-press papers can be found under "projects" on metacreation.net
