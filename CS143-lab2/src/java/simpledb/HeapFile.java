package simpledb;

import java.io.*;
import java.util.*;
import java.nio.channels.FileChannel;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    private File	_file;
    private TupleDesc	_td;
    private int		_numPages;

    public class HeapFileIterator implements DbFileIterator {
	// Remember that heapPages have their own iterators...
	
	// keeps track of which page iterator is going through
	int 		_pageIndex = 0;
	boolean 	_open = false;
	TransactionId 	_tid;
	
	Iterator<Tuple>	_it;
	
	public HeapFileIterator(TransactionId tid) {
	    _tid = tid;
	}
    
	public void open() throws DbException, TransactionAbortedException {
	    if (_open)
		return;
	
	    _open = true;
	    
	     // Get the first page
	    HeapPage p = (HeapPage)(Database.getBufferPool().getPage(
		    _tid,
		    new HeapPageId(getId(), _pageIndex),
		    Permissions.READ_WRITE));
		   
	    // Set the _it iterator to the iterator from the first page
	    _it = p.iterator();
	}
	
	public boolean hasNext() throws DbException, TransactionAbortedException {
	    if (!_open)
		return false;
	    
	    if (_it == null)
		return false;
	
	    if (_it.hasNext())
		return true;
	    
	    if (_pageIndex < (numPages() - 1))
		return true;
	    
	    return false;
	}
	
	public Tuple next() throws DbException, 
				    TransactionAbortedException,
				    NoSuchElementException {
	    if (hasNext()) {
		if (_it.hasNext())
		    return _it.next();
		else if (!_it.hasNext() && _pageIndex < (numPages() - 1)) {
		    // If we've reached the end of the page, set the iterator to an
		    // iterator of the next page
		    HeapPage p = (HeapPage)Database.getBufferPool().getPage(
			    _tid,
			    new HeapPageId(getId(), ++_pageIndex),
			    Permissions.READ_WRITE);  
		    _it = p.iterator();
		    
		    // Sanity check
		    if (!_it.hasNext())
			throw new NoSuchElementException();
			
		    // return the first object of the new iterator
		    return _it.next();
		}
	    }
	    throw new NoSuchElementException();
	}
	public void rewind() throws DbException, TransactionAbortedException {
	    close();
	    _pageIndex = 0;
	    open();
	}
	
	public void close(){
	    _open = false;
	}
	
    }
    
    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        // DONE
        _file = f;
        _td = td;
        _numPages = numPages();
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        // DONE
        return _file;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        // DONE
        return _file.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        // DONE
        return _td;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        // DONE
        int pageSize = BufferPool.getPageSize();
        
        try {
	    // Open inputStream for file
	    InputStream is = new BufferedInputStream(new FileInputStream(_file));
	    
	    // Skip to the appropriate poing in the stream, i.e. the page offset we want
	    is.skip(pid.pageNumber() * pageSize);
	    
	    // Create appropriate byte array and fill it
	    byte[] b = new byte[pageSize];
	    is.read(b);
	    is.close();
	    
	    // return a new HeapPage wih associated data
	    return new HeapPage((HeapPageId)pid, b);
	    
        } catch (FileNotFoundException e) {
	    e.printStackTrace();
        } catch (IOException e) {
	    e.printStackTrace();
        }
        
        return null;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
        // DONE
        
        try {
	    // Open file and set bufferedOutputStream to correct position to write to
	    FileOutputStream fos = new FileOutputStream(_file);
	    FileChannel fc = fos.getChannel();
	    fc.position(page.getId().pageNumber() * BufferPool.getPageSize());
	    OutputStream os = new BufferedOutputStream(fos);
	    
	    byte[] data = page.getPageData();
	    os.write(data, 0, BufferPool.getPageSize());
	    os.flush();
        } catch (Exception e) {
	    throw new IOException("Error: couldn't write page. " + e);
        }
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        // DONE
        return (int)Math.ceil(_file.length() / BufferPool.getPageSize());
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        try {
	    HeapPageId pid;
	    HeapPage page;
	    ArrayList<Page> modifiedPages = new ArrayList<Page>();
	    
	    for (int i = 0; i < numPages(); i++) {
		// Create a new pid and fetch the page with that ID
		pid = new HeapPageId(getId(), i);
		page = (HeapPage)Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
		
		// Find page with room to insert
		if (page.getNumEmptySlots() > 0) {
		
		    page.insertTuple(t);
		    
		    modifiedPages.add(page);
		    return modifiedPages;
		}
	    }
	    // If no page has any openenings,
	    // create a new one and push it onto the disk
	    pid = new HeapPageId(getId(), numPages());
	    page = new HeapPage(pid, HeapPage.createEmptyPageData());
	    writePage(page);
	    
	    // retrieve that page again to fiddle with and add the tuple
	    page = (HeapPage)(Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE));
	    page.insertTuple(t);
	    
	    modifiedPages.add(page);
	    
	    return modifiedPages;
        } catch (Exception e) {
	    throw new DbException("Error: File could not insert tuple. " + e);
        }
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        // not necessary for lab1
        try {
	    PageId pid = t.getRecordId().getPageId();
	    
	    // Sanity check
	    if ((pid.getTableId() != getId()) || (pid.pageNumber() >= numPages()))
		throw new DbException("Error: tuple not found");
	    
	    HeapPage page = (HeapPage)Database.getBufferPool().getPage(tid, pid, Permissions.READ_WRITE);
	    page.deleteTuple(t);
	    
	    ArrayList<Page> modifiedPages = new ArrayList<Page>();
	    modifiedPages.add(page);
	    return modifiedPages;
        } catch (Exception e) {
	    throw new DbException("Error: File could not delete tuple. " + e);
        }
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        // DONE
        return new HeapFileIterator(tid);
    }

}

