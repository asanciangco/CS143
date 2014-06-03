package simpledb;

import java.util.*;

/**
 * The Aggregation operator that computes an aggregate (e.g., sum, avg, max,
 * min). Note that we only support aggregates over a single column, grouped by a
 * single column.
 */
public class Aggregate extends Operator {

    private static final long serialVersionUID = 1L;

    private DbIterator dbIt;
    private int aField;
    private int gbField;
    private Type gbFieldType;
    private Aggregator.Op aOp;
    private Aggregator aggItself;			// use to determine whether it's a stringAggregator or an IntegerAggregator
    private DbIterator resIt;

    /**
     * Constructor.
     * 
     * Implementation hint: depending on the type of afield, you will want to
     * construct an {@link IntAggregator} or {@link StringAggregator} to help
     * you with your implementation of readNext().
     * 
     * 
     * @param child
     *            The DbIterator that is feeding us tuples.
     * @param afield
     *            The column over which we are computing an aggregate.
     * @param gfield
     *            The column over which we are grouping the result, or -1 if
     *            there is no grouping
     * @param aop
     *            The aggregation operator to use
     */
    public Aggregate(DbIterator child, int afield, int gfield, Aggregator.Op aop) {
    		dbIt = child;
    		aField = afield;
    		gbField = gfield;
    		aOp = aop;
    		if(gbField == Aggregator.NO_GROUPING)
    			gbFieldType = null;
    		else
    			gbFieldType = dbIt.getTupleDesc().getFieldType(gbField);
    		
    		if(dbIt.getTupleDesc().getFieldType(aField).equals(Type.INT_TYPE))				// initiate the aggregator to be a strAgg or an intAgg
    			aggItself = new IntegerAggregator(gbField, gbFieldType, aField, aOp);
    		else if(dbIt.getTupleDesc().getFieldType(aField).equals(Type.STRING_TYPE))
    			aggItself = new StringAggregator(gbField, gbFieldType, aField, aOp);
    		else
    			throw new IllegalArgumentException("Not a string or int aggregator");
    }

    /**
     * @return If this aggregate is accompanied by a groupby, return the groupby
     *         field index in the <b>INPUT</b> tuples. If not, return
     *         {@link simpledb.Aggregator#NO_GROUPING}
     * */
    public int groupField() {
    	return gbField;
    }

    /**
     * @return If this aggregate is accompanied by a group by, return the name
     *         of the groupby field in the <b>OUTPUT</b> tuples If not, return
     *         null;
     * */
    public String groupFieldName() {
    	if(gbField == Aggregator.NO_GROUPING)
    		return null;
    	else
    		return dbIt.getTupleDesc().getFieldName(gbField);
    }

    /**
     * @return the aggregate field
     * */
    public int aggregateField() {
    	return aField;
    }

    /**
     * @return return the name of the aggregate field in the <b>OUTPUT</b>
     *         tuples
     * */
    public String aggregateFieldName() {
    	return dbIt.getTupleDesc().getFieldName(aField);
    }

    /**
     * @return return the aggregate operator
     * */
    public Aggregator.Op aggregateOp() {
    	return aOp;
    }

    public static String nameOfAggregatorOp(Aggregator.Op aop) {
	return aop.toString();
    }
    
    public void open() throws NoSuchElementException, DbException,
    TransactionAbortedException {
	super.open();
	dbIt.open();
	while (dbIt.hasNext()) {
		aggItself.mergeTupleIntoGroup(dbIt.next());
	}
	resIt = aggItself.iterator();
	resIt.open();
}

    /**
     * Returns the next tuple. If there is a group by field, then the first
     * field is the field by which we are grouping, and the second field is the
     * result of computing the aggregate, If there is no group by field, then
     * the result tuple should contain one field representing the result of the
     * aggregate. Should return null if there are no more tuples.
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
		if(resIt == null)
			throw new DbException("");
		if(resIt.hasNext())
			return resIt.next();
		return null;
		
    }

    public void rewind() throws DbException, TransactionAbortedException {
    	dbIt.rewind();
    	resIt.rewind();
    }

    /**
     * Returns the TupleDesc of this Aggregate. If there is no group by field,
     * this will have one field - the aggregate column. If there is a group by
     * field, the first field will be the group by field, and the second will be
     * the aggregate value column.
     * 
     * The name of an aggregate column should be informative. For example:
     * "aggName(aop) (child_td.getFieldName(afield))" where aop and afield are
     * given in the constructor, and child_td is the TupleDesc of the child
     * iterator.
     */
    public TupleDesc getTupleDesc() {
    	TupleDesc td = null;
    	Type[] types;
    	String[] fields;
    	String aggregateName = aOp.toString() 
    							+ "(" 
    							+ dbIt.getTupleDesc().getFieldName(aggregateField()) 
    							+ ")";
    	if (groupField() == Aggregator.NO_GROUPING) {
    		//only one field = aggregate column
    		types = new Type[] {dbIt.getTupleDesc().getFieldType(aggregateField())};
    		fields = new String[] {aggregateName};
    		td = new TupleDesc(types, fields);
    	}
    	else {
    		types = new Type[] {dbIt.getTupleDesc().getFieldType(groupField()),
    				dbIt.getTupleDesc().getFieldType(aggregateField())};
    		fields = new String[] {groupFieldName(), aggregateName};
    		td = new TupleDesc(types, fields);
    	}
    	return td;
    }

    public void close() {
	    super.close();
    	dbIt.close();
    	resIt.close();
    }

    @Override
    public DbIterator[] getChildren() {
	    if(dbIt == null)
    		return null;
    	else
    		return new DbIterator[] {dbIt};
    }

    @Override
    public void setChildren(DbIterator[] children) {
    	dbIt = children[0];
	}
    
}
