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
    	// DONE
    	
        if(v < _min) {
	    switch (op) {
		case EQUALS:
		case LESS_THAN:
		case LESS_THAN_OR_EQ:
		    return 0.0;
		case NOT_EQUALS:
		case GREATER_THAN:
		case GREATER_THAN_OR_EQ:
		    return 1.0;
	    }
	}
	if(v > _max) {
	    switch (op) {
		case NOT_EQUALS:
		case LESS_THAN:
		case LESS_THAN_OR_EQ:
		    return 1.0;
		case EQUALS:
		case GREATER_THAN:
		case GREATER_THAN_OR_EQ:
		    return 0.0;
	    }
	}
	if(v == _min) {
	    if(op == Predicate.Op.LESS_THAN_OR_EQ || op == Predicate.Op.LESS_THAN) {
		return 0;
	    }
	}

	if(v == _max) {
	    if(op == Predicate.Op.GREATER_THAN_OR_EQ || op == Predicate.Op.GREATER_THAN) {
		return 0;
	    }
	}
	
	// meaty stuff
	int begin = loc(v);
	if (op == Predicate.Op.EQUALS)
	    return (_buckets[loc(v)] / (double) _width) / (double) _count;
	    
	if (op == Predicate.Op.NOT_EQUALS)
	    return (double) 1 - estimateSelectivity(Predicate.Op.EQUALS, v);
	    
	if (op == Predicate.Op.GREATER_THAN || op == Predicate.Op.GREATER_THAN_OR_EQ) {
	    int sum = 0;
	    if (begin < _min)
		return 1.0;
	    for (int i = begin; i < _buckets.length; i++)
		sum += _buckets[i];
	    return sum / (double) _count;
	}
	
	if (op == Predicate.Op.LESS_THAN || op == Predicate.Op.LESS_THAN_OR_EQ) {
	    int sum = 0;
	    if (begin > _max)
		return 1.0;
	    for (int i = 0; i < begin + 1; i++)
		sum += _buckets[i];
	    return sum / (double) _count;
	}
	return 1 / (double) 10;
    }
    
     private int loc(int value) {
	int ret = (value - _min) / _width;
	return (ret == _buckets.length) ? _buckets.length - 1 : ret;
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
