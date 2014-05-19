package simpledb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Tuple maintains information about the contents of a tuple. Tuples have a
 * specified schema specified by a TupleDesc object and contain Field objects
 * with the data for each field.
 */
public class Tuple implements Serializable {

    private static final long serialVersionUID = 1L;

    private TupleDesc 	_td;
    private Field[]	_fields;
    private RecordId	_rid;
    /**
     * Create a new tuple with the specified schema (type).
     * 
     * @param td
     *            the schema of this tuple. It must be a valid TupleDesc
     *            instance with at least one field.
     */
    public Tuple(TupleDesc td) {
        // some code goes here
        // DONE

        // check if td is valid: not null and has at least 1 field
        if (td == null || td.numFields() < 1)
	    throw new NullPointerException();
	    
	// initialize member variables
        _td = td;
        _fields = new Field[td.numFields()];
    }

    /**
     * @return The TupleDesc representing the schema of this tuple.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        // DONE
        return _td;
    }

    /**
     * @return The RecordId representing the location of this tuple on disk. May
     *         be null.
     */
    public RecordId getRecordId() {
        // some code goes here
        // DONE
        return _rid;
    }

    /**
     * Set the RecordId information for this tuple.
     * 
     * @param rid
     *            the new RecordId for this tuple.
     */
    public void setRecordId(RecordId rid) {
        // some code goes here
        // DONE
        _rid = rid;
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
        // DONE
        if (i < 0 || i >= _fields.length)
	    throw new NullPointerException();
	
	_fields[i] = f;
    }

    /**
     * @return the value of the ith field, or null if it has not been set.
     * 
     * @param i
     *            field index to return. Must be a valid index.
     */
    public Field getField(int i) {
        // some code goes here
        // DONE
	if (i < 0 || i >= _fields.length)
	    throw new NullPointerException();
        
        return _fields[i];
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
        // some code goes here
        // DONE
        String s = "";
        for (int i = 0; i < _fields.length; i++) {
	    s += _fields[i].toString() + ((i < _fields.length - 1) ? "\t" : "");
        }
        s += "\n";
        
        return s;
    }
    
    /**
     * @return
     *        An iterator which iterates over all the fields of this tuple
     * */
    public Iterator<Field> fields()
    {
        // some code goes here
        // DONE
        if (_fields == null)
		throw new NullPointerException();
        
        // Return utility iterator specific to TupleDesc.
        return new Iterator<Field>() {
	    private int index = 0;
	    
	    public boolean hasNext() {
		return index < _fields.length;
	    }
	    
	    public Field next() {
		if (!hasNext())
		    throw new NullPointerException();
		return _fields[index++];
	    }
	    
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
        };
    }
    
    /**
     * reset the TupleDesc of thi tuple
     * */
    public void resetTupleDesc(TupleDesc td)
    {
        // some code goes here
        // DONE
        _td = td;
        _fields = null; // Should this be here?
    }
}
