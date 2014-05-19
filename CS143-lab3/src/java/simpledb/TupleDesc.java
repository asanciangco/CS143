package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     * */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         * */
        public final Type fieldType;
        
        /**
         * The name of the field
         * */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
        // some code goes here
        // DONE
        if (_itemArray == null)
		throw new NullPointerException();
        
        // Return utility iterator specific to TupleDesc.
        return new Iterator<TDItem>() {
	    private int index = 0;
	    
	    public boolean hasNext() {
		return index < _itemArray.length;
	    }
	    
	    public TDItem next() {
		if (!hasNext())
		    throw new NullPointerException();
		return _itemArray[index++];
	    }
	    
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
        };
    }

    // Member variables
    private static final long serialVersionUID = 1L;
    private TDItem[] _itemArray;
    
    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *            array specifying the names of the fields. Note that names may
     *            be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
        // DONE
        _itemArray = new TDItem[typeAr.length];
        for (int i = 0; i < typeAr.length; i++) {
	    _itemArray[i] = new TDItem(typeAr[i], fieldAr[i]);
        }
        
        // calculate size
        
        
    }
    
    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *            array specifying the number of and types of fields in this
     *            TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
        // DONE
        _itemArray = new TDItem[typeAr.length];
        for (int i = 0; i < typeAr.length; i++) {
	    _itemArray[i] = new TDItem(typeAr[i], ""); // possibly want null
        }
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        // DONE
        return _itemArray.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *            index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
        // DONE
        if (i < 0 || i >= _itemArray.length) // might be wrong
	    throw new NoSuchElementException();
	else
	    return _itemArray[i].fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *            The index of the field to get the type of. It must be a valid
     *            index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *             if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // some code goes here
        if (i < 0 || i >= _itemArray.length) // might be wrong
	    throw new NoSuchElementException();
	else
	    return _itemArray[i].fieldType;
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *            name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *             if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
        // DONE
        for (int i = 0; i < _itemArray.length; i++) {
	    if (_itemArray[i].fieldName.equals(name))
		return i;
        }
        throw new NoSuchElementException();
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
        // DONE
        int tempSize = 0;
        for (int i = 0; i < _itemArray.length; i++) {
	    tempSize += _itemArray[i].fieldType.getLen();
        }
        return tempSize;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // some code goes here
        // DONE
        int len1 = td1.numFields();
        int len2 = td2.numFields();
        
        // Arrays that will hold info from both TupleDescs
        Type[]   types = new Type[len1 + len2];
        String[] names = new String[len1 + len2];
        
        // Populate arrays with data from td1 THEN td2
        for (int i = 0; i < len1; i++) {
	    types[i] = td1.getFieldType(i);
	    names[i] = td1.getFieldName(i);
        }
        for (int j = 0; j < len2; j++) {
	    types[len1 + j] = td2.getFieldType(j);
	    names[len1 + j] = td2.getFieldName(j);
        }
        
        // create new TupleDesc object and return it
        return new TupleDesc(types, names);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *            the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here
        // DONE
        if (o == null || !(o instanceof TupleDesc))
	    return false;
        
        TupleDesc td = (TupleDesc) o;
        
        // return false if they are different lengths
        if (td.getSize() != getSize() || td.numFields() != numFields())
	    return false;
        
        for (int i = 0; i < numFields(); i++) {
	    if (td.getFieldType(i) != getFieldType(i))
		return false;
        }
        
        return true;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
	throw new UnsupportedOperationException("unimplemented");
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        // DONE
        String s = "";
        for (int i = 0; i < numFields(); i++) {
	    s += _itemArray[i].toString() + ", ";
        }
        return s;
    }
}
