package simpledb;

import java.io.IOException;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;
    
    private TransactionId tranId;
    private DbIterator dbIt;
    private TupleDesc td;
    private int fetchNextNum;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        tranId = t;
        dbIt = child;   
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
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
    	Tuple result = new Tuple(td);
    	if(fetchNextNum > 0)
    		return null;
    	else
    	{ 
    		try{
	            int count = 0;
		        while(dbIt.hasNext())
		        {
		        	try
		        	{
		        		Database.getBufferPool().deleteTuple(tranId, dbIt.next());
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
