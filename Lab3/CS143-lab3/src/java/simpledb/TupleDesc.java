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

    /*I will create an array of TDItem objects here as the variable of class TupleDesc*/
    private TDItem[] tdArr;
    
    
    /**
     * @return
     *        An iterator which iterates over all the field TDItems
     *        that are included in this TupleDesc
     * */
    public Iterator<TDItem> iterator() {
    	/* I will first change the array into a list, and then use iterator directly*/
    	List<TDItem> tdList = Arrays.asList(tdArr);
    	Iterator<TDItem> iter = tdList.iterator();

        return iter;
    }

    private static final long serialVersionUID = 1L;

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
    	
    	/*I will store the typeAr and fieldAr in an array of TDItem objects*/
    	/*First I will check if they are of the same length*/
    	
    	/*need to check if typeAr is empty or not?*/
    	
    	if(typeAr.length == fieldAr.length)
    	{
    		tdArr = new TupleDesc.TDItem[typeAr.length];
    		for(int i=0; i<tdArr.length;i++)						//populate the array
    			tdArr[i] = new TDItem(typeAr[i], fieldAr[i]);		//using the TDItem constructor to insert the values
    	}
    	else
    	{
    		//temporarily use console out put
    		System.out.println("Type arr and Field arr are of different length!");
    	}
    	
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
    	
    	/*need to check if typeAr is empty or not?*/
    	
    	tdArr = new TupleDesc.TDItem[typeAr.length];
    	for(int i=0; i<tdArr.length;i++)							//populate the array
			tdArr[i] = new TDItem(typeAr[i], "");					//using the TDItem constructor in insert types and empty strings
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
    	
    	/*I will directly use the length of the TDItem array*/
    	return this.tdArr.length;  
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
    	
    	/* I will first compare the numFields() and the int i to see if i is valid
    	 * then get the field name*/
    	if(i > this.numFields() - 1)				// -1 because the one is the length and the other one is the index number
    		throw new NoSuchElementException("Invalid index number, No such element!");
    	else
    		return this.tdArr[i].fieldName; 
        	
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
    	/*Same as the one above*/
    	if(i > this.numFields() -1)
    		throw new NoSuchElementException("Invalid index number, No such element!");
    	else
    		return this.tdArr[i].fieldType;  
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
    	
    	/* Do I need to make sure that all the elements in the fieldName[] is unique? */
    	/*
    	Iterator<TDItem> theIterator = this.iterator();
        int index = -1;
        boolean found = false;

        while(theIterator.hasNext()){
            index++;
            TDItem theItem = theIterator.next();
            String theName = theItem.fieldName;
            if(theName != null && theName.equals(name)){
                found = true;
                break;
            }
        }

        if(!found)
            throw new NoSuchElementException(name);

        return index;
    */
    	
    	if(name == null)
    		throw new NoSuchElementException("Null string is invalid");
    	
    	for(int i=0; i<tdArr.length; i++)
    	{
    		if(tdArr[i].fieldName != null && tdArr[i].fieldName.equals(name))
    			return i;
    	}
    	throw new NoSuchElementException("Did not find a match with that name!");
    	
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
    	
    	int size = 0;
    	for(TDItem o:tdArr)
    	{
    		size += o.fieldType.getLen();
    	}
        return size;
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
    	/* I will first make two arrays: type and name extracted from td1 and td2
    	 * then using the TupleDesc constructor to construct the new TupleDesc object
    	 * BUT DO I NEED TO MAKE SURE THAT THEY ARE NOT EMPTY? */   	
    	
    	Type[] tArr = new Type[td1.numFields() + td2.numFields()];
    	String[] sArr = new String[td1.numFields() + td2.numFields()];
    	
    	for(int i=0; i<sArr.length; i++)							//fill in every element for these 2 arrays
    	{
    		if(i<td1.numFields())
    		{
    			tArr[i] = td1.tdArr[i].fieldType;
    			sArr[i] = td1.tdArr[i].fieldName;
    		}
    		else
    		{
    			tArr[i] = td2.tdArr[i-td1.numFields()].fieldType;
    			sArr[i] = td2.tdArr[i-td1.numFields()].fieldName;
    		}
    	}
    	
    	TupleDesc td3 = new TupleDesc(tArr, sArr);					//using constructor to construct the merged TupleDesc obj
    	return td3;
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
    	/* I will first make sure that the object instance is a tupledesc object and then do the type cast*/
    	
    	if(o instanceof TupleDesc)							//if they are of same class
    	{
    		TupleDesc td2 = (TupleDesc)o;
    		if(td2.numFields() != this.numFields())
    			return false;								//test they are of same length
    		for(int i=0; i<td2.numFields(); i++)
    		{
    			if(td2.getFieldType(i) != this.getFieldType(i))
    				return false;							//test if each entry has the same type
    		}
    		return true;
    	}
    	else
    		return false;
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
    	
    	String result = "";
    	for(int i=0; i<tdArr.length; i++)
    	{
    		String tmp = "";
    		if(i == tdArr.length-1)
    			tmp = tdArr[i].fieldType + "[" + i + "](" + tdArr[i].fieldName + "[" + i + "])";
    		else
    			tmp = tdArr[i].fieldType + "[" + i + "](" + tdArr[i].fieldName + "[" + i + "]),";
    		
    		result+=tmp;
    	}
        return result;
    }
}




