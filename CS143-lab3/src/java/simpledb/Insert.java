package simpledb;

/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;
    private TransactionId _tid;
    private DbIterator _child;
    private int _tableId;
    private boolean called;
    private TupleDesc _td;
    /**
     * Constructor.
     * 
     * @param t
     *            The transaction running the insert.
     * @param child
     *            The child operator from which to read tuples to be inserted.
     * @param tableid
     *            The table in which to insert tuples.
     * @throws DbException
     *             if TupleDesc of child differs from table into which we are to
     *             insert.
     */
    public Insert(TransactionId t, DbIterator child, int tableid)
            throws DbException {
        // some code goes here
        // DONE
        
        _tid = t;
        _child = child;
        _tableId = tableid;
	
	Type[]   types  = new Type[1];
	String[] fields = new String[1];
	types[0]  = Type.INT_TYPE;
	fields[0] = "Count";
	_td = new TupleDesc(types, fields);
	
        if(!_child.getTupleDesc().equals(Database.getCatalog().getTupleDesc(tableid)))
	    throw new DbException("Error: Mismatching tupleDesc");
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        // DONE
        return _td;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        // DONE
        _child.open();
        super.open();
        called = false;
    }

    public void close() {
        // some code goes here
        // DONE
        super.close();
        _child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        // DONE
        _child.rewind();
    }

    /**
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     * 
     * @return A 1-field tuple containing the number of inserted records, or
     *         null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        // DONE
        if (called)
	    return null;
	
	try {
	    called = true;
	    BufferPool bp  = Database.getBufferPool();
	    int count = 0;
	    
	    Tuple t;
	    while (_child.hasNext()) {
		t = _child.next();
		bp.insertTuple(_tid, _tableId, t);
		count++;
	    }
	    Tuple ret = new Tuple(_td);
	    ret.setField(0, new IntField(count));
	    return ret;
	} catch (Exception e) {
	    throw new DbException("Error: Could not insert tuples. " + e);
	}
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        //DONE
        DbIterator[] children = {_child};
        return children;
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        // DONE
        _child = children[0];
    }
}
