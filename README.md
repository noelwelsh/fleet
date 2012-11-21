Fleet
=====

Streaming algorithms for a data-driven age.

Fleet implements several streaming algorithms in Scala, and wraps up the algorithms in a service that exposes a REST API. These two parts are independent.

A streaming algorithm is an algorithm that makes a single pass over data. That is, it can only visit a data point once and much summarise that point in some way and then throw it away. Streaming algorithms typically have memory usage that is sublinear, low CPU usage, and by their nature give real-time results.


## Resources

We stand on the shoulder of giants.

- Alex Smola's [course section on data streams](http://alex.smola.org/teaching/berkeley2012/streams.html)
- Andrew McGregor's [2012 course on streaming algorithms](http://people.cs.umass.edu/~mcgregor/courses/CS711S12/index.html)
