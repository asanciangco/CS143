package simpledb;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {

    int _buckets[];	// Array of "buckets"
    int _width; 	// The width of each bucket, consistent
    int _count;
    int _min;
    int _max;
    
    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
    	// some code goes here
    	// DONE
    	
    	_buckets = new int[buckets];
    	_width = (int)Math.ceil(((double)max - min) / buckets);
    	_min = min;
    	_max = max;
    	_count = 0;
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
    	// some code goes here
    	// DONE
    	
    	// Find the bucket that needs to be updated
    	int bucketNumber = (v - _min) / _width;
    	bucketNumber = (bucketNumber >= _buckets.length) ? bucketNumber - 1 : bucketNumber;
    	
    	// Increment the appropriate value
    	_buckets[bucketNumber]++;
    	_count++;
    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {
    	// some code goes here
    	
    	/* THE PLAN
    	 * 	Compound operators (e.g. <= and >=) are just the sum of = and inequalities.
    	 * 	Therefore, those will recursively call estimateSelectivity() for each op
    	 *	manually and add them together. 
    	 *
    	 * OPS NEEDED TO SUPPORT
    	 *	=, >, <, LIKE.
    	 *	Everything else can be determined from these, e.g. composies, not equals, etc.
    	 */
    	
    	if (op == Predicate.Op.GREATER_THAN_OR_EQ) {
	    // this isn't exactly right
	    return estimateSelectivity(Predicate.Op.GREATER_THAN, v) + estimateSelectivity(Predicate.Op.EQUALS, v);
    	} else if (op == Predicate.Op.LESS_THAN_OR_EQ) {
	    // this isn't exactly right
	     return estimateSelectivity(Predicate.Op.LESS_THAN, v) + estimateSelectivity(Predicate.Op.EQUALS, v);
    	} else if (op == Predicate.Op.NOT_EQUALS) {
	    return 1 - estimateSelectivity(Predicate.Op.EQUALS, v);
    	} 
    	// And now for the meaty ones
    	else if (op == Predicate.Op.LESS_THAN) {
	    if (v < _min)
		return 0.0;	// Implies nothing is less than v
	    if (v > _max)
		return 1.0;	// Implies everything is less than v
	    
	    int b;		// Bucket containing v
	    int h_b;		// Height of bucket b
	    double b_f;		// Fraction of all tuples that are in b
	    double b_part;	// Fraction of b that is > v
	    int b_left;		// Left most value of bucket b
	    double selectivity;	// Estimated selectivity to return.
	    
	    // Get the bucket number
	    b = (v - _min) / _width;
	    b = (b >= _buckets.length) ? b - 1 : b;
	    // Get the fraction of count in b
	    h_b = _buckets[b];
	    b_f = (double)h_b / _count;
	    // Set the left most value of bucket b
	    b_left = _min + (b * _width); 
	    // Calculate b_part
	    b_part = ((double)v - b_left) / _width;
	    
	    selectivity = b_part * b_f;
	    
	    // Sum up the contributions of all the other buckets.
	    for (int i = b - 1; i >= 0; i--) {
		selectivity += (double)_buckets[i] / _count;
	    }
	    
	    return selectivity;
    	} else if (op == Predicate.Op.GREATER_THAN) {
	    if (v < _min)
		return 1.0;	// Implies everything is greater than v
	    if (v > _max)
		return 0.0;	// Implies nothing is greater than v
    	
	    int b;		// Bucket containing v
	    int h_b;		// Height of bucket b
	    double b_f;		// Fraction of all tuples that are in b
	    double b_part;	// Fraction of b that is > v
	    int b_right;	// Right most value of bucket b
	    double selectivity;	// Estimated selectivity to return.
	    
	    // Get the bucket number
	    b = (v - _min) / _width;
	    b = (b >= _buckets.length) ? b - 1 : b;
	    // Get the fraction of count in b
	    h_b = _buckets[b];
	    b_f = (double)h_b / _count;
	    // Set the right most value of bucket b
	    b_right = _min + ((b + 1) * _width); 
	    // Calculate b_part
	    b_part = ((double)b_right - v) / _width;
	    
	    selectivity = b_part * b_f;
	    
	    // Sum up the contributions of all the other buckets.
	    for (int i = b + 1; i < _buckets.length; i++) {
		selectivity += (double)_buckets[i] / _count;
	    }
	    
	    return selectivity;
	    
    	} else if (op == Predicate.Op.EQUALS) {
	    // If v doens't equal ANY value, that is, it's outside the range
	    if (v < _min || v > _max)
		return 0.0;
	    
	    // Get the bucket number
	    int b = (v - _min) / _width;
	    b = (b >= _buckets.length) ? b - 1 : b;
	    
	    int h = _buckets[b];
	    
	    return ((double)h / _width) / _count;
	    
    	} else if (op == Predicate.Op.LIKE) {
	    estimateSelectivity(Predicate.Op.EQUALS, v);
    	}
    	
        return -1.0;
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
        // DONE
        
        double num = 0.0, den = 0.0;
        for (int i = 0; i < _buckets.length; i++) {
	    num += _buckets[i] * _buckets[i];
        }
        den = _count * _count;
        
        return num / den;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {

        // some code goes here
        // DONE
        return "IntHistogram(#buckets=" + _buckets.length + ", #tups=" + _count + ", width=" + _width + ", min=" + _min + ", max=" + _max + ")\n";
    }
}
