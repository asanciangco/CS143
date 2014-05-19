package simpledb;
import java.util.*;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    private int _gbfield;
    private Type _gbfieldtype;
    
    private HashMap<Field,Integer> _groups;
    private int _count;
    
    private TupleDesc _td;
    
    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        
        _gbfield = gbfield;
        _gbfieldtype = gbfieldtype;
        
        _groups = new HashMap<Field,Integer>(); //Use for groups aggregates
        _count  = 0; //Use for ungrouped aggregates
        
        Type[] t;
        String[] s;
        
        if(_gbfieldtype == null) {
	    t = new Type[1];
	    s = new String[1];
	    
	    t[0] = Type.INT_TYPE;
	    s[0] = "Aggregate";
        } else {
	    t = new Type[2]; 
	    s = new String[2];
	    
	    t[0] = _gbfieldtype;
	    s[0] = "Group Field"; 
	    
	    t[1] = Type.INT_TYPE;
	    s[1] = "Aggregate";
        }
        
        _td = new TupleDesc(t, s);
        
        if(what != Op.COUNT)
	  throw new IllegalArgumentException();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
        
        if(_gbfieldtype == null) {
	    _count++;
	    return;
	}
	
	Field myField = tup.getField(_gbfield);
	
	if(_groups.containsKey(myField)) {
	    _groups.put(myField, _groups.get(myField) + 1);
	} else {
	    _groups.put(myField, new Integer(1));
	}
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        ArrayList<Tuple> myTuples = new ArrayList<Tuple>();
        if(_gbfieldtype == null)
        {
	    Tuple onlyTup = new Tuple(_td);
	    onlyTup.setField(0, new IntField(_count));
	    myTuples.add(onlyTup);
        }
        else
        {
	    Iterator it = _groups.entrySet().iterator();
	    while(it.hasNext())
	    {
		Map.Entry<Field, Integer> e = (Map.Entry<Field, Integer>) it.next();
		Tuple tup = new Tuple(_td);
		tup.setField(0, e.getKey());
		tup.setField(1, new IntField(e.getValue()));
		myTuples.add(tup);
	    }
        }
        return new TupleIterator(_td, myTuples);
    }

}
