package simpledb;

import java.util.*;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;

    /*
     * list of private variables
    */
    private int gbField;
    private int aField;
    private Type gbFieldType;
    private Op opItself;
    private HashMap<Field, IntField> grouptoCount;			// map a single group name to the number of it's entry for every group
    private TupleDesc td;									// the newly created tupledesc
    
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
    	if(what != Op.COUNT)
    		throw new IllegalArgumentException("String Aggregator only supports COUNT");	
    	gbField = gbfield;
    	aField = afield;
    	opItself = what;
    	gbFieldType = gbfieldtype;
    	grouptoCount = new HashMap<Field, IntField>();
    	td = null;
    	
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */

    public void mergeTupleIntoGroup(Tuple tup) {
    	
    	// test if the aggregate field is null
    	if(tup.getField(aField) == null)
    		return;
    	
    	if(td == null)
    	 	td = createTd(tup);
    	     	
    	Field tmpGroup;
    	IntField tmpCount;
    	if(gbField == Aggregator.NO_GROUPING)
    		tmpGroup = new IntField(Aggregator.NO_GROUPING);
    	else
    		tmpGroup = tup.getField(gbField);
    	
    	// test if the group exists in the hashmap
    	if(grouptoCount.get(tmpGroup) == null)
    		tmpCount = new IntField(1);
    	else
    		tmpCount = new IntField(grouptoCount.get(tmpGroup).getValue() + 1);				// update the count number for that group
    	
    	
    	grouptoCount.put(tmpGroup, tmpCount);												// update the count number for that group
    	
    }
    /**
     *  Create a schema for the aggregate results of the tuples
     *  @param tup is the input tuple that need to be extracted the aggregate field and group field
     *  @return a TupleDesc object that stores the schema for the aggregate results
     */
    public TupleDesc createTd(Tuple tup)
    {
    	// use the TupleDesc constructor
    	
    	String[] tmpStr;
    	Type[] tmpType;
    	
    	String aggField = tup.getTupleDesc().getFieldName(aField);
    	String groupField = tup.getTupleDesc().getFieldName(gbField);
    	Type groupType = tup.getTupleDesc().getFieldType(gbField);
    	
    	// test if there is a grouping or not
    	if(gbField == Aggregator.NO_GROUPING)
    	{
    		tmpStr = new String[]{aggField};
    		tmpType = new Type[]{Type.INT_TYPE};
    	}
    	else
    	{
    		tmpStr = new String[]{groupField, aggField};		// first entry should be the group, second should be the agg
    	    tmpType = new Type[]{groupType, Type.INT_TYPE};
    	}
    	
    	TupleDesc tmpTd = new TupleDesc(tmpType, tmpStr);
    	return tmpTd;
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
    	
    	 //
    	 // 			-----------------
    	 //				| count	|		|		// if there is no grouping for a tuple
    	 //				-----------------
    	 // 			| group	| count	|		// if there is a grouping for a tuple
    	 //				-----------------
    	 //
    	
    	Tuple tmpTup;
    	ArrayList<Tuple> tmpArr = new ArrayList<Tuple>();
    	
    	for(Field groupField : grouptoCount.keySet())
    	{
        	tmpTup = new Tuple(td);

    		if(((IntField)groupField).getValue() == Aggregator.NO_GROUPING)			// meaning there is no grouping
    			tmpTup.setField(0, grouptoCount.get(groupField));
    		else
    		{
    			tmpTup.setField(0, groupField);
    			tmpTup.setField(1, grouptoCount.get(groupField));
    		}
    			
    		tmpArr.add(tmpTup);					// pop up the arr with tuples
    	}
    	
    	TupleIterator tI = new TupleIterator(td, tmpArr);
    	return tI;
    	
        //throw new UnsupportedOperationException("please implement me for lab2");
    }


}
