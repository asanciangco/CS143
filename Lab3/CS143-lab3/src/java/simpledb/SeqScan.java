package simpledb;

import java.util.*;

import simpledb.TupleDesc.TDItem;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements DbIterator {

    private static final long serialVersionUID = 1L;
    
    private TransactionId tidItself;
    private int tableIdItself;
    private String tAlias;
    private DbFileIterator iterItself;
    /**
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     * 
     * @param tid
     *            The transaction this scan is running as a part of.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
        // some code goes here
    	tidItself = tid;
    	tableIdItself = tableid;
    	tAlias = tableAlias;
    }

    /**
     * @return
     *       return the table name of the table the operator scans. This should
     *       be the actual name of the table in the catalog of the database
     * */
    public String getTableName() {
        return Database.getCatalog().getTableName(tableIdItself);
    }
    
    /**
     * @return Return the alias of the table this operator scans. 
     * */
    public String getAlias()
    {
        // some code goes here
        return tAlias;
    }

    /**
     * Reset the tableid, and tableAlias of this operator.
     * @param tableid
     *            the table to scan.
     * @param tableAlias
     *            the alias of this table (needed by the parser); the returned
     *            tupleDesc should have fields with name tableAlias.fieldName
     *            (note: this class is not responsible for handling a case where
     *            tableAlias or fieldName are null. It shouldn't crash if they
     *            are, but the resulting name can be null.fieldName,
     *            tableAlias.null, or null.null).
     */
    public void reset(int tableid, String tableAlias) {
        // some code goes here
    	tableIdItself = tableid;
    	tAlias = tableAlias;
    }

    public SeqScan(TransactionId tid, int tableid) {
        this(tid, tableid, Database.getCatalog().getTableName(tableid));
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
    	iterItself = Database.getCatalog().getDatabaseFile(tableIdItself).iterator(tidItself);		//get the DbFile iterator from the database with tid
    	iterItself.open();
    }

    /**
     * Returns the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor. This prefix
     * becomes useful when joining tables containing a field(s) with the same
     * name.
     * 
     * @return the TupleDesc with field names from the underlying HeapFile,
     *         prefixed with the tableAlias string from the constructor.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
    	
    	TupleDesc tmpTd = Database.getCatalog().getTupleDesc(tableIdItself);			//get the original tupleDesc
    	Type[] tmpType = new Type[tmpTd.numFields()];									
    	String[] tmpStr = new String[tmpTd.numFields()];
    	
    	Iterator<TDItem> iter = tmpTd.iterator();
    	int i=0;
    	while(iter.hasNext())
    	{
    		TDItem tmpItem = iter.next();
    		tmpType[i] = tmpItem.fieldType;
    		tmpStr[i] = tAlias + "." + tmpItem.fieldName;									//for every TDItem in the TupleDesc, and the alias to the item name
    		i++;		
    	}
    	TupleDesc newTd = new TupleDesc(tmpType, tmpStr);									//construct a new tupleDesc with the new TDItem names (with alias)
    	return newTd;
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
        // some code goes here 
        return iterItself.hasNext();
    }

    public Tuple next() throws NoSuchElementException,
            TransactionAbortedException, DbException {
        // some code goes here
        return iterItself.next();
    }

    public void close() {
        // some code goes here
    	iterItself.close();
    }

    public void rewind() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
    	iterItself.rewind();
    }
}
