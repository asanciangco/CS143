package simpledb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import simpledb.TupleDesc.TDItem;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {

    private static final long serialVersionUID = 1L;

    //create a new private variable
    private TupleDesc tDesc;
    private Field[] fieldObj;    
    private RecordId recordId;
    
    /**
     * Create a new tuple with the specified schema (type).
     * 
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        // some code goes here
    	/* I will check the TupleDesc whether it meets the standard */
    	
    	if(td.numFields() < 1)
    		throw new IllegalArgumentException(td.toString());
    	
    	/* then just initialize all the variables*/
    	
    	tDesc = td;
    	fieldObj = new Field[td.numFields()];
    	recordId = null;
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
    	
        return tDesc;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
        return recordId;
    }

    /**
     * Set the RecordId information for this tuple.
     * 
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
    	recordId = rid;
    }

    /**
     * Change the value of the ith field of this tuple.
     * 
     * @param i
     *            index of the field to change. It must be a valid index.
     * @param f
     *            new value for the field.
     */
    public void setField(int i, Field f) {
        // some code goes here
    	/* I will check if the index is valid or not*/
    	//if(i >= fieldObj.length || i<0)
    	//	throw new IllegalArgumentException("Invalid input, either out of bound or smaller than 0");
    	
    	/* Then I will find the field according to the index and insert in the new value*/
    	//if(f.getType().equals(fieldObj[i].getType()))
    		fieldObj[i] = f;
    	//else
    	//	throw new IllegalArgumentException("Input has different type");
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     * 
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        // some code goes here
    	/* I will check if the index is out of bound first*/
    	if(i >= fieldObj.length || i<0)
    		throw new IllegalArgumentException("Invalid input, either out of bound or smaller than 0");
    	 	
        return fieldObj[i];
    }

    /**
     * Returns the contents of this Tuple as a string. Note that to pass the
     * system tests, the format needs to be as follows:
     * 
     * column1\tcolumn2\tcolumn3\t...\tcolumnN\n
     * 
     * where \t is any whitespace, except newline, and \n is a newline
     */
    public String toString() {
    	
    	String result = "";
    	for(int i=0; i<fieldObj.length; i++)
    	{
    		String tmp = "";
    		if(i == fieldObj.length-1)
    			tmp = fieldObj[i] + "\n";
    		else
    			tmp = fieldObj[i] + "\t";
    		
    		result+=tmp;
    	}
    	
    	return result;
    	
        // throw new UnsupportedOperationException("Implement this");
    }
    
    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {   	
    	/* I will first change the array into a list, and then use iterator directly*/
    	List<Field> fList = Arrays.asList(fieldObj);
    	Iterator<Field> iter = fList.iterator();

        return iter;
    }
    
    /**
     * reset the TupleDesc of thi tuple
     * */
    public void resetTupleDesc(TupleDesc td)
    {
        // some code goes here
    	tDesc = td;
    }
}
