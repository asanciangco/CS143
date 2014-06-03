package simpledb;

import java.io.*;
import java.util.*;

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

	/**
	 * The physical file associated with this HeapFile instance.
	 */
	private final File m_file;

	/**
	 * The description of the tuples stored in this HeapFile.
	 */
	private final TupleDesc m_tupleDesc;

	/**
	 * A unique ID for this HeapFile, implemented as suggested in the Javadoc
	 * for {@link #getId() getId}.
	 */
	private final int m_tableid;

	/**
	 * Constructs a heap file backed by the specified file.
	 * 
	 * @param f
	 *            the file that stores the on-disk backing store for this heap
	 *            file.
	 */
	public HeapFile(File f, TupleDesc td) {
		// some code goes here
		m_file = f;
		m_tableid = f.getAbsoluteFile().hashCode();
		m_tupleDesc = td;
	}

	/**
	 * Returns the File backing this HeapFile on disk.
	 * 
	 * @return the File backing this HeapFile on disk.
	 */
	public File getFile() {
		// some code goes here
		return m_file;
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
		return m_tableid;
	}

	/**
	 * Returns the TupleDesc of the table stored in this DbFile.
	 * 
	 * @return TupleDesc of this DbFile.
	 */
	public TupleDesc getTupleDesc() {
		// some code goes here
		return m_tupleDesc;
	}

	// see DbFile.java for javadocs
	public Page readPage(PageId pid) {
		// some code goes here
		HeapPageId hpid = (HeapPageId) pid; // cast to HeapPageId
		BufferedInputStream bis = null; // The stream to read file from
		try {
			bis = new BufferedInputStream(new FileInputStream(m_file));
			byte pageBuf[] = new byte[BufferPool.getPageSize()];

			// Total number of bytes in the pages to skip.
			long bytesToSkip = hpid.pageNumber() * BufferPool.getPageSize();

			// Attempt to skip these bytes in the file.
			long bytesSkipped = bis.skip(bytesToSkip);

			if (bytesSkipped != bytesToSkip) { // Verify we skipped correct # of
												// bytes.
				throw new IllegalArgumentException(
						"Unable to seek to correct place in heapfile");
			}

			// Read the page into pageBuf.
			int bytesRead = bis.read(pageBuf, 0, BufferPool.getPageSize());
			if (bytesRead == -1) {
				throw new IllegalArgumentException("Read past end of table.");
			}
			if (bytesRead < BufferPool.getPageSize()) {
				throw new IllegalArgumentException("Unable to read "
						+ BufferPool.getPageSize() + " bytes from heapfile.");
			}
			Debug.log(1, "HeapFile.readPage: read page %d", hpid.pageNumber());
			return new HeapPage(hpid, pageBuf);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		} finally { // Close the file on success or error
			try {
				if (bis != null) {
					bis.close();
				}
			} catch (IOException ioe) {
				// Ignore failures closing the file
			}
		}
	}

	// see DbFile.java for javadocs
	public void writePage(Page page) throws IOException {
		// some code goes here
		// not necessary for lab1
		RandomAccessFile myFile=new RandomAccessFile(m_file,"rw");
        long offset= page.getId().pageNumber()*BufferPool.PAGE_SIZE;
        byte[] data=page.getPageData();
        myFile.seek(offset);
        myFile.write(data);
        page.markDirty(false,null);
	}

	/**
	 * Returns the number of pages in this HeapFile.
	 */
	public int numPages() {
		// some code goes here

		// The # of pages is the size of the physical file in bytes /
		// The size of each page in bytes.
		return (int) (m_file.length() / BufferPool.getPageSize());
	}

	// see DbFile.java for javadocs
	public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
			throws DbException, IOException, TransactionAbortedException {
		// some code goes here
		// not necessary for lab1
		ArrayList<Page> pagesLists = new ArrayList<Page>();
		HeapPageId pid;
		HeapPage page;
		// traverse through files to find empty page
		for (int i = 0; i < numPages(); i++) {
			pid = new HeapPageId(getId(), i);
			page = (HeapPage) Database.getBufferPool().getPage(tid, pid,
					Permissions.READ_WRITE);
			if (page.getNumEmptySlots() > 0) {// if the page has spots, add
												// tuple
				page.insertTuple(t);
				page.markDirty(true, tid);
				pagesLists.add(page);
				return pagesLists;
			}
		}
		// no pages have spots, add new page, the pageNumber is current size
		pid = new HeapPageId(getId(), numPages());
		page = new HeapPage(pid, HeapPage.createEmptyPageData());
		writePage(page);//write a empty page to file
        page=(HeapPage)Database.getBufferPool().getPage(tid,pid,Permissions.READ_WRITE);//access through bufferpool
		page.insertTuple(t);
		pagesLists.add(page);
		return pagesLists;
	}

	// see DbFile.java for javadocs
	public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t)
			throws DbException, TransactionAbortedException {
		// some code goes here
		// not necessary for lab1
		ArrayList<Page> changedPages=new ArrayList<Page>();
		 if(t.getRecordId()!=null && t.getRecordId().getPageId().getTableId()==getId()){
			 //get page through bufferpool
	            HeapPage page=(HeapPage)Database.getBufferPool().getPage(tid,t.getRecordId().getPageId(),Permissions.READ_WRITE);
	            page.deleteTuple(t);
	            page.markDirty(true,tid);
	            changedPages.add(page);
	            return changedPages;
	        } else {
	            throw new DbException("tuple cannot be deleted or is not a member of the file");
	        }
	}

	// see DbFile.java for javadocs
	public DbFileIterator iterator(TransactionId tid) {
		// some code goes here
		return new HeapFileIterator(this, tid);
	}

	/**
	 * Helper class that implements the Java Iterator for tuples on a HeapFile
	 */
	class HeapFileIterator extends AbstractDbFileIterator {

		/**
		 * An iterator to tuples for a particular page.
		 */
		Iterator<Tuple> m_tupleIt;

		/**
		 * The current number of the page this class is iterating through.
		 */
		int m_currentPageNumber;

		/**
		 * The transaction id for this iterator.
		 */
		TransactionId m_tid;

		/**
		 * The underlying heapFile.
		 */
		HeapFile m_heapFile;

		/**
		 * Set local variables for HeapFile and Transactionid
		 * 
		 * @param hf
		 *            The underlying HeapFile.
		 * @param tid
		 *            The transaction ID.
		 */
		public HeapFileIterator(HeapFile hf, TransactionId tid) {
			m_heapFile = hf;
			m_tid = tid;
		}

		/**
		 * Open the iterator, must be called before readNext.
		 */
		public void open() throws DbException, TransactionAbortedException {
			m_currentPageNumber = -1;
		}

		@Override
		protected Tuple readNext() throws TransactionAbortedException,
				DbException {

			// If the current tuple iterator has no more tuples.
			if (m_tupleIt != null && !m_tupleIt.hasNext()) {
				m_tupleIt = null;
			}

			// Keep trying to open a tuple iterator until we find one of run out
			// of pages.
			while (m_tupleIt == null
					&& m_currentPageNumber < m_heapFile.numPages() - 1) {
				m_currentPageNumber++; // Go to next page.

				// Get the iterator for the current page
				HeapPageId currentPageId = new HeapPageId(m_heapFile.getId(),
						m_currentPageNumber);

				HeapPage currentPage = (HeapPage) Database.getBufferPool()
						.getPage(m_tid, currentPageId, Permissions.READ_ONLY);
				m_tupleIt = currentPage.iterator();

				// Make sure the iterator has tuples in it
				if (!m_tupleIt.hasNext())
					m_tupleIt = null;
			}

			// Make sure we found a tuple iterator
			if (m_tupleIt == null)
				return null;

			// Return the next tuple.
			return m_tupleIt.next();
		}

		/**
		 * Rewind closes the current iterator and then opens it again.
		 */
		public void rewind() throws DbException, TransactionAbortedException {
			close();
			open();
		}

		/**
		 * Close the iterator, which resets the counters so it can be opened
		 * again.
		 */
		public void close() {
			super.close();
			m_tupleIt = null;
			m_currentPageNumber = Integer.MAX_VALUE;
		}
	}

}