package simpledb;

import java.util.*;

/**
 * The Aggregation operator that computes an aggregate (e.g., sum, avg, max,
 * min). Note that we only support aggregates over a single column, grouped by a
 * single column.
 */
public class Aggregate extends Operator {

    private static final long serialVersionUID = 1L;
    private DbIterator _child;
    private int _afield;
    private int _gfield;
    private Type _gfieldtype;
    private Aggregator.Op _aop;
    private Aggregator _aggr;
    private DbIterator _result;
    
    
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
	// some code goes here
	// Done?
	
	_child = child;
	_afield = afield;
	_gfield = gfield;
	_aop = aop;
	if(_gfield == Aggregator.NO_GROUPING)
	    _gfieldtype = null;
	else
	    _gfieldtype = _child.getTupleDesc().getFieldType(_gfield);
	  
	if(_child.getTupleDesc().getFieldType(_afield) == Type.STRING_TYPE)
	    _aggr = new StringAggregator(_gfield, _gfieldtype, _afield, _aop);
	else
	    _aggr = new IntegerAggregator(_gfield, _gfieldtype, _afield, _aop);
    }

    /**
     * @return If this aggregate is accompanied by a groupby, return the groupby
     *         field index in the <b>INPUT</b> tuples. If not, return
     *         {@link simpledb.Aggregator#NO_GROUPING}
     * */
    public int groupField() {
	// some code goes here
	// Done
	return _gfield;
    }

    /**
     * @return If this aggregate is accompanied by a group by, return the name
     *         of the groupby field in the <b>OUTPUT</b> tuples If not, return
     *         null;
     * */
    public String groupFieldName() {
	// some code goes here
	// Done
	if(_gfield == Aggregator.NO_GROUPING)
	  return null;
	return _child.getTupleDesc().getFieldName(_gfield);
    }

    /**
     * @return the aggregate field
     * */
    public int aggregateField() {
	// some code goes here
	// Done
	return _afield;
    }

    /**
     * @return return the name of the aggregate field in the <b>OUTPUT</b>
     *         tuples
     * */
    public String aggregateFieldName() {
	// some code goes here
	// Done
	return _child.getTupleDesc().getFieldName(_afield);
    }

    /**
     * @return return the aggregate operator
     * */
    public Aggregator.Op aggregateOp() {
	// some code goes here
	// Done
	return _aop;
    }

    public static String nameOfAggregatorOp(Aggregator.Op aop) {
	return aop.toString();
    }

    public void open() throws NoSuchElementException, DbException,
	    TransactionAbortedException {
	// some code goes here
	// Done
	super.open();
	_child.open();
	while(_child.hasNext())
	  _aggr.mergeTupleIntoGroup(_child.next());
	_result = _aggr.iterator();
	_result.open();
    }

    /**
     * Returns the next tuple. If there is a group by field, then the first
     * field is the field by which we are grouping, and the second field is the
     * result of computing the aggregate, If there is no group by field, then
     * the result tuple should contain one field representing the result of the
     * aggregate. Should return null if there are no more tuples.
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
	// some code goes here
	//
	if(_result.hasNext())	
	  return _result.next();
	return null;
    }

    public void rewind() throws DbException, TransactionAbortedException {
	// some code goes here
	// Result
	
	_result.rewind();
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
	// some code goes here
	// Done
	
	Type[] t;
	String[] s;
	if(_gfield == Aggregator.NO_GROUPING)
	{
	  t = new Type[1];
	  t[0] = _child.getTupleDesc().getFieldType(_afield);
	  s = new String[1];
	  s[0] = _aop.toString() + " (" + _child.getTupleDesc().getFieldName(_afield) + ")";
	}
	else
	{
	  t = new Type[2];
	  t[0] = _child.getTupleDesc().getFieldType(_gfield);
	  t[1] = _child.getTupleDesc().getFieldType(_afield);
	  s = new String[2];
	  s[0] = _child.getTupleDesc().getFieldName(_gfield);
	  s[1] = _aop.toString() + " (" + _child.getTupleDesc().getFieldName(_afield) + ")";
	}
	
	return new TupleDesc(t,s);
    }

    public void close() {
	// some code goes here
	// Done
	_child.close();
	_result = null; // Let garbage collection take care of everything
	super.close();
    }

    @Override
    public DbIterator[] getChildren() {
	// some code goes here
	// Done
	DbIterator[] children = new DbIterator[1];
	children[0] = _child;
	return children;
    }

    @Override
    public void setChildren(DbIterator[] children) {
	// some code goes here
	// Done
	_child = children[0];
    }
    
}
