/**
 *  The {@code Edge} class represents a weighted edge in an 
 *  {@link EdgeWeightedGraph}. Each edge consists of two integers
 *  (naming the two vertices) and a real-value weight. The data type
 *  provides methods for accessing the two endpoints of the edge and
 *  the weight. The natural order for this data type is by
 *  ascending order of weight.
 *  <p>
 *  For additional documentation, see <a href="https://algs4.cs.princeton.edu/43mst">Section 4.3</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class Edge implements Comparable<Edge> { 

    private final int v;
    private final int w;
    private final int distance;
    private final int price;

    /**
     * Initializes an edge between vertices {@code v} and {@code w} of
     * the given {@code weight}.
     *
     * @param  v one vertex
     * @param  w the other vertex
     * @param  weight the weight of this edge
     * @throws IllegalArgumentException if either {@code v} or {@code w} 
     *         is a negative integer
     * @throws IllegalArgumentException if {@code weight} is {@code NaN}
     */
    public Edge(int v, int w, int distance, int price) {
        if (v < 0) throw new IllegalArgumentException("vertex index must be a nonnegative integer");
        if (w < 0) throw new IllegalArgumentException("vertex index must be a nonnegative integer");
        if (distance <0 || price <0) throw new IllegalArgumentException("price and distance must be nonnegative");
        this.v = v;
        this.w = w;
        this.distance = distance;
        this.price = price;
    }
    
    /**
     * Returns the distance of this edge
     * 
     * @return the distance of this edge
     */
    public int getDistance() {
    	return distance;
    }
    
    /**
     * Returns the price of this edge
     * 
     * @return the price of this edge
     */
    public int getPrice() {
    	return price;
    }

    /**
     * Returns either endpoint of this edge.
     *
     * @return either endpoint of this edge
     */
    public int either() {
        return v;
    }

    /**
     * Returns the endpoint of this edge that is different from the given vertex.
     *
     * @param  vertex one endpoint of this edge
     * @return the other endpoint of this edge
     * @throws IllegalArgumentException if the vertex is not one of the
     *         endpoints of this edge
     */
    public int other(int vertex) {
        if      (vertex == v) return w;
        else if (vertex == w) return v;
        else throw new IllegalArgumentException("Illegal endpoint");
    }

    /**
     * Compares two edges by weight.
     * Note that {@code compareTo()} is not consistent with {@code equals()},
     * which uses the reference equality implementation inherited from {@code Object}.
     *
     * @param  that the other edge
     * @return a negative integer, zero, or positive integer depending on whether
     *         the weight of this is less than, equal to, or greater than the
     *         argument edge
     */
    @Override
    public int compareTo(Edge that) {
        return Integer.compare(this.distance, that.distance);
    }
    
    /**
     * Compares the argument price with this edge's price
     * 
     * @param  p	price to be compared to
     * @return a negative integer, zero, or positive integer depending on whether
     *         the price of this is less than, equal to, or greater than the
     *         argument price
     */
    public int comparePrice(int p) {
    	if(p>price) return -1;
    	if(p==price) return 0;
    	else return 1;
    }
}