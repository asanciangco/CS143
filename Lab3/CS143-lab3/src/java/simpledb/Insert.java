package simpledb;

import java.io.IOException;

/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;
    
    private DbIterator dbIt;
    private int tableId;
    private TransactionId tranId;
    private TupleDesc td;
    private int fetchNextNum;
    
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
    public Insert(TransactionId t,DbIterator child, int tableid)
            throws DbException {
        // some code goes here
    	dbIt = child;
    	tranId = t;
    	tableId = tableid;  
    	Type[] tdType = new Type[]{Type.INT_TYPE};
    	String[] tdStr = new String[]{"Numbers of tuple inserted"};
    	td = new TupleDesc(tdType, tdStr); 	
    	fetchNextNum = 0;
    	
    }

    public TupleDesc getTupleDesc() {
        return td;
    }

    public void open() throws DbException, TransactionAbortedException {
    	try{
	    	super.open();
	        dbIt.open();
    	} catch (DbException e)
    	{
    		e.printStackTrace();
    	} catch (TransactionAbortedException e)
    	{
    		e.printStackTrace();
    	}
    }

    public void close() {
    	dbIt.close();
    	super.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        try
        {
        	dbIt.rewind();
        } catch (DbException e)
    	{
    		e.printStackTrace();
    	} catch (TransactionAbortedException e)
    	{
    		e.printStackTrace();
    	}
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
    	Tuple result = new Tuple(td);				// use to store the insertion result
    	int count = 0;								// use to keep track of numbers of tuple insertion
    	
    	if(fetchNextNum > 0)						// meaning this is not the first time calling fetchNext(), and should not return any tuples
    		return null;
    	else
    	{
    		try
    		{
		    	while(dbIt.hasNext())
		    	{
		    		try
		    		{
		    			Database.getBufferPool().insertTuple(tranId, tableId, dbIt.next());
		    		} catch(IOException e)
		    		{
		    			e.printStackTrace();
		    		}
		    		count++;
		    	}
	    	
		    	result.setField(0, new IntField(count));
		    	fetchNextNum++;
    		} catch (DbException e)
    		{
    			e.printStackTrace();
    		} catch (TransactionAbortedException e)
    		{
    			e.printStackTrace();
    		}
    	}
    	return result;
    	
    }
   

    @Override
    public DbIterator[] getChildren() {
        return new DbIterator[]{dbIt};
    }

    @Override
    public void setChildren(DbIterator[] children) {
        dbIt = children[0];
    }
}
