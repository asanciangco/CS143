package simpledb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Catalog keeps track of all available tables in the database and their
 * associated schemas.
 * For now, this is a stub catalog that must be populated with tables by a
 * user program before it can be used -- eventually, this should be converted
 * to a catalog that reads a catalog table from disk.
 * 
 * @Threadsafe
 */
public class Catalog {

    public class Table {
    
	// Member variables
	public int tableID;
	public DbFile file;
	public String primaryKey;
	public String name;
	public TupleDesc td;
	
	public Table (int i, DbFile d, String p, String n, TupleDesc t) {
	    tableID = i;
	    file = d;
	    primaryKey = p;
	    name = n;
	    td = t;
	}
    }
    // Current implementation isn't that great... it stores the tableID but doesn't
    // index based on it. Decision made for space efficiency, tradeoff is speed.
    private ArrayList<Table> _tables;
    /**
     * Constructor.
     * Creates a new, empty catalog.
     */
    public Catalog() {
        // some code goes here
        _tables = new ArrayList<Table>();
    }

    /**
     * Add a new table to the catalog.
     * This table's contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.getId() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     * @param name the name of the table -- may be an empty string.  May not be null.  If a name
     * @param pkeyField the name of the primary key field
     * conflict exists, use the last table to be added as the table for a given name.
     */
    public void addTable(DbFile file, String name, String pkeyField) {
        // some code goes here
        // DONE
        if (file == null)
	    throw new NullPointerException();
        _tables.add(new Table(file.getId(), file, pkeyField, name, file.getTupleDesc()));
    }

    public void addTable(DbFile file, String name) {
        addTable(file, name, "");
    }

    /**
     * Add a new table to the catalog.
     * This table has tuples formatted using the specified TupleDesc and its
     * contents are stored in the specified DbFile.
     * @param file the contents of the table to add;  file.getId() is the identfier of
     *    this file/tupledesc param for the calls getTupleDesc and getFile
     */
    public void addTable(DbFile file) {
        addTable(file, (UUID.randomUUID()).toString());
    }

    /**
     * Return the id of the table with a specified name,
     * @throws NoSuchElementException if the table doesn't exist
     */
    public int getTableId(String name) throws NoSuchElementException {
        // some code goes here
        // DONE
        for (Table t : _tables) {
	    if (t.name.equals(name))
		return t.tableID;
        }
        throw new NoSuchElementException();
    }

    /**
     * Returns the tuple descriptor (schema) of the specified table
     * @param tableid The id of the table, as specified by the DbFile.getId()
     *     function passed to addTable
     * @throws NoSuchElementException if the table doesn't exist
     */
    public TupleDesc getTupleDesc(int tableid) throws NoSuchElementException {
        // some code goes here
        // DONE
        for (Table t : _tables) {
	    if (t.tableID == tableid)
		return t.td;
        }
        throw new NoSuchElementException();
        //return null;
    }

    /**
     * Returns the DbFile that can be used to read the contents of the
     * specified table.
     * @param tableid The id of the table, as specified by the DbFile.getId()
     *     function passed to addTable
     */
    public DbFile getDatabaseFile(int tableid) throws NoSuchElementException {
        // some code goes here
        // DONE
        for (Table t : _tables) {
	    if (t.tableID == tableid)
		return t.file;
        }
        throw new NoSuchElementException();
        //return null;
    }

    public String getPrimaryKey(int tableid) {
        // some code goes here
        for (Table t : _tables) {
	    if (t.tableID == tableid)
		return t.primaryKey;
        }
        throw new NoSuchElementException();
        //return null;
    }

    public Iterator<Integer> tableIdIterator() {
        // some code goes here
        // DONE
        return new Iterator<Integer>() {
	    private int index = 0;
	    
	    public boolean hasNext() {
		return index < _tables.size();
	    }
	    
	    public Integer next() {
		if (!hasNext())
		    throw new NullPointerException();
		return new Integer(_tables.get(index++).tableID);
	    }
	    
	    public void remove() {
		if (!hasNext())
		    throw new NullPointerException();
		_tables.remove(index);
	    }
        };
    }

    public String getTableName(int id) {
        // some code goes here
        // DONE
        for (Table t : _tables) {
	    if (t.tableID == id)
		return t.name;
        }
        throw new NoSuchElementException();
        //return null;
    }
    
    /** Delete all tables from the catalog */
    public void clear() {
        // some code goes here
        // DONE
        _tables.clear();
    }
    
    /**
     * Reads the schema from a file and creates the appropriate tables in the database.
     * @param catalogFile
     */
    public void loadSchema(String catalogFile) {
        String line = "";
        String baseFolder=new File(new File(catalogFile).getAbsolutePath()).getParent();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(catalogFile)));
            
            while ((line = br.readLine()) != null) {
                //assume line is of the format name (field type, field type, ...)
                String name = line.substring(0, line.indexOf("(")).trim();
                //System.out.println("TABLE NAME: " + name);
                String fields = line.substring(line.indexOf("(") + 1, line.indexOf(")")).trim();
                String[] els = fields.split(",");
                ArrayList<String> names = new ArrayList<String>();
                ArrayList<Type> types = new ArrayList<Type>();
                String primaryKey = "";
                for (String e : els) {
                    String[] els2 = e.trim().split(" ");
                    names.add(els2[0].trim());
                    if (els2[1].trim().toLowerCase().equals("int"))
                        types.add(Type.INT_TYPE);
                    else if (els2[1].trim().toLowerCase().equals("string"))
                        types.add(Type.STRING_TYPE);
                    else {
                        System.out.println("Unknown type " + els2[1]);
                        System.exit(0);
                    }
                    if (els2.length == 3) {
                        if (els2[2].trim().equals("pk"))
                            primaryKey = els2[0].trim();
                        else {
                            System.out.println("Unknown annotation " + els2[2]);
                            System.exit(0);
                        }
                    }
                }
                Type[] typeAr = types.toArray(new Type[0]);
                String[] namesAr = names.toArray(new String[0]);
                TupleDesc t = new TupleDesc(typeAr, namesAr);
                HeapFile tabHf = new HeapFile(new File(baseFolder+"/"+name + ".dat"), t);
                addTable(tabHf,name,primaryKey);
                System.out.println("Added table : " + name + " with schema " + t);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println ("Invalid catalog entry : " + line);
            System.exit(0);
        }
    }
}

