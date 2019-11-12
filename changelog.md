# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [2.0.0-beta] - 2019-11

### Added

- Normalize the mouse interaction between various implementations. 
 
## [2.0-alpha] - 2018-05

### Added

- Stub for new UI modules (Swing, JavaFX, Android...) 
- Java8+ Streams to iterate through nodes edges and so on. For instance, method `Stream<Node> nodes()` from interface `org.graphstream.graph.Structure` (superinterface of `Graph`) returns a stream of nodes.

### Changed

- Serious modification of the code base in order to use Java8 Stream in place of iterators. 

### Removed

- The basic swing viewer
- `<T extends Node> Iterator<T> getNodeIterator()` (and `Edge`) 
 and `eachEdge()` iterators from  `Structure` in favore of streams.
- `<T extends Node> Iterable<? extends T> getEachNode()` (and `Edge`) iterables from `Structure` in favore of streams.
- the `addAttribute()` method from `Element` as it was no more than a confusing alias for `setAttribute()`.



 
