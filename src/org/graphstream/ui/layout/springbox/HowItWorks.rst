The SpringBox layout organization
=================================

This document details the organization and functional working of the Spring-Box graph layout package.

This package, contains several force-based algorithms. It is composed of a base class named `BarnesHutLayout` that manages a set of "particles" and links between these particles. It is able to use a space decomposition technique (a quad-tree (2D) or oct-tree (3D)) to define a spatial index, and then defines for each cell of the tree a barycenter and a weitgh average for all the nodes allowing to use a Barnes-Hut technique to have a O(n log n) algorithm instead of a O(n^2).

The `BarnesHutLayout` class is an abstract one. It uses a set of `NodeParticle` and `EdgeSpring`. Your own algos must refine the `BarnesHutLayout` class as well as the `NodeParticle` class in order to provide something useful. The whole simulation is handled by the `BarnesHutLayout`, however the force computation is done in the `NodeParticle` class. It is responsible for the computation of the attraction, repulsion and gravity forces.

TODO finish this.
