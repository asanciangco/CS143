package simpledb;

import java.util.*;

/**
 * Filter is an operator that implements a relational select.
 */
public class Filter extends Operator {

    private static final long serialVersionUID = 1L;

    private Predicate m_predicate;
    private DbIterator m_child;
    /**
     * Constructor accepts a predicate to apply and a child operator to read
     * tuples to filter from.
     * 
     * @param p
     *            The predicate to filter tuples with
     * @param child
     *            The child operator
     */
    public Filter(Predicate p, DbIterator child) {
        // some code goes here
    	m_predicate=p;
    	m_child=child;
    }

    public Predicate getPredicate() {
        // some code goes here
        return m_predicate;
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return m_child.getTupleDesc();
    }

    public void open() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
    	m_child.open();
    	super.open();
    }

    public void close() {
        // some code goes here
    	super.close();
    	m_child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
    	m_child.rewind();
    }

    /**
     * AbstractDbIterator.readNext implementation. Iterates over tuples from the
     * child operator, applying the predicate to them and returning those that
     * pass the predicate (i.e. for which the Predicate.filter() returns true.)
     * 
     * @return The next tuple that passes the filter, or null if there are no
     *         more tuples
     * @see Predicate#filter
     */
    protected Tuple fetchNext() throws NoSuchElementException,
            TransactionAbortedException, DbException {
        // some code goes here
    	while(m_child.hasNext()){
    		Tuple newTuple=m_child.next();
    		if(m_predicate.filter(newTuple))
    			return newTuple;
    	}
        return null;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        return new DbIterator[] { m_child };
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
    	m_child=children[0];
    }

}