package simpledb;

import java.util.ArrayList;
import java.util.HashMap;

import simpledb.Aggregator.Op;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

	private static final long serialVersionUID = 1L;

	private int gbField;
	private Type gbFieldType;
	private int aField;
	private Op opItself;
	private HashMap<Field, IntField> grouptoResult;
	private HashMap<Field, Integer> countNum;
	private HashMap<Field, Integer> sumNum;

	private TupleDesc td;

	/**
	 * Aggregate constructor
	 * 
	 * @param gbfield
	 *            the 0-based index of the group-by field in the tuple, or
	 *            NO_GROUPING if there is no grouping
	 * @param gbfieldtype
	 *            the type of the group by field (e.g., Type.INT_TYPE), or null
	 *            if there is no grouping
	 * @param afield
	 *            the 0-based index of the aggregate field in the tuple
	 * @param what
	 *            the aggregation operator
	 */

	public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
		gbField = gbfield;
		gbFieldType = gbfieldtype;
		aField = afield;
		opItself = what;
		grouptoResult = new HashMap<Field, IntField>();
		countNum = new HashMap<Field, Integer>();
		sumNum = new HashMap<Field, Integer>();
	}

	/**
	 * Merge a new tuple into the aggregate, grouping as indicated in the
	 * constructor
	 * 
	 * @param tup
	 *            the Tuple containing an aggregate field and a group-by field
	 */
	
	public void mergeTupleIntoGroup(Tuple tup){
		Field tupGrpVal;				// for storing the group info
		IntField tmpResult;			// for storing the intField value for every group and every op
		
		if (td == null)
			td = createTd(tup);
		
		IntField tupAggVal = (IntField) tup.getField(aField);
		if(gbField == Aggregator.NO_GROUPING)
			tupGrpVal = new IntField(Aggregator.NO_GROUPING);
		else
			tupGrpVal = tup.getField(gbField);
		
		boolean isNewGroup = grouptoResult.get(tupGrpVal) == null;
		switch(opItself)
		{
		case COUNT:
			if(isNewGroup)
				tmpResult = new IntField(1);
			else
				tmpResult = new IntField(grouptoResult.get(tupGrpVal).getValue() + 1);
			break;
		case SUM:
			if(isNewGroup)
				tmpResult = tupAggVal;
			else
				tmpResult = new IntField(grouptoResult.get(tupGrpVal).getValue() + tupAggVal.getValue());
			break;
		case MIN:
			if(isNewGroup)
				tmpResult = tupAggVal;
			else
				tmpResult = new IntField(Math.min(grouptoResult.get(tupGrpVal).getValue(), tupAggVal.getValue()));
			break;
		case MAX:
			if(isNewGroup)
				tmpResult = tupAggVal;
			else
				tmpResult = new IntField(Math.max(grouptoResult.get(tupGrpVal).getValue(), tupAggVal.getValue()));
			break;
		case AVG:
			if(countNum.get(tupGrpVal) == null)
			{
				tmpResult = tupAggVal;
				countNum.put(tupGrpVal, 1);
				sumNum.put(tupGrpVal, tupAggVal.getValue());
			}
			else
			{
				int totalCount = countNum.get(tupGrpVal) + 1;
				int totalSum = sumNum.get(tupGrpVal) + tupAggVal.getValue();
				tmpResult = new IntField(totalSum/totalCount);
				sumNum.put(tupGrpVal, totalSum);
				countNum.put(tupGrpVal, totalCount);
			}
			break;
		default:
			tmpResult = new IntField(0);
		}
		grouptoResult.put(tupGrpVal, tmpResult);
	}
	

	
	
	/**
	 * Create a DbIterator over group aggregate results.
	 * 
	 * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
	 *         if using group, or a single (aggregateVal) if no grouping. The
	 *         aggregateVal is determined by the type of aggregate specified in
	 *         the constructor.
	 */
	
	 //
  	 // 			-----------------
  	 //				| value	|		|		// if there is no grouping for a tuple
  	 //				-----------------
  	 // 			| group	| value	|		// if there is a grouping for a tuple
  	 //				-----------------
  	 //
  	
	public DbIterator iterator() {
		Tuple tmpTup;
	   	ArrayList<Tuple> tmpArr = new ArrayList<Tuple>();
	   	
	   	for(Field groupField : grouptoResult.keySet())
	   	{
	       	tmpTup = new Tuple(td);
	        Field aggVal = grouptoResult.get(groupField);
	   		if(gbField == Aggregator.NO_GROUPING)			// meaning there is no grouping
	   			tmpTup.setField(0, aggVal);
	   		else
	   		{
	   			tmpTup.setField(0, groupField);
				tmpTup.setField(1, aggVal);
	   		}
	   			
	   		tmpArr.add(tmpTup);					// pop up the arr with tuples
	   	}
	   	
	   	TupleIterator tI = new TupleIterator(td, tmpArr);
	   	return tI;
	}

	
	public TupleDesc createTd(Tuple tup)
    {
    	// use the TupleDesc constructor
    	
    	String[] tmpStr;
    	Type[] tmpType;
    	
    	String aggField = tup.getTupleDesc().getFieldName(aField);
    	
    	// test if there is a grouping or not
    	if(gbField == Aggregator.NO_GROUPING)
    	{
    		tmpStr = new String[]{aggField};
    		tmpType = new Type[]{Type.INT_TYPE};
    	}
    	else
    	{
    		String groupField = tup.getTupleDesc().getFieldName(gbField);
        	Type groupType = tup.getTupleDesc().getFieldType(gbField);
    		tmpStr = new String[]{groupField, aggField};		// first entry should be the group, second should be the agg value
    	    tmpType = new Type[]{groupType, Type.INT_TYPE};
    	}
    	
    	TupleDesc tmpTd = new TupleDesc(tmpType, tmpStr);
    	return tmpTd;
    }
   

}
