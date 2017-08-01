import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BlockedBufferedOutputStream extends OutputStream 
{
    /**
     * The buffer where data is stored.
     */
    protected byte buf[];

    /**
     * The number of valid bytes in the buffer.
     */
    protected int count;

    /**
     * The mark position of the bytes has read
     */
    protected int mark;
    
    /**
     * The total Number have written.
     */
    protected int totalCount;
    
    protected boolean done ;
    
    protected InputStream in ;
    
    
    /**
     * Creates a new byte array output stream. The buffer capacity is
     * initially 32 bytes, though its size increases if necessary.
     */
	public BlockedBufferedOutputStream()
	{
		this(8192);
		
	}
	
    /**
     * Creates a new byte array output stream, with a buffer capacity of
     * the specified size, in bytes.
     *
     * @param   size   the initial size.
     * @exception  IllegalArgumentException if size is negative.
     */
    public BlockedBufferedOutputStream(int size) 
    {
	        if (size < 0) {
	            throw new IllegalArgumentException("Negative initial size: "
	                                               + size);
	        }
	        buf = new byte[size];
	        this.in = new BlockInputStream();
    }
    
    /**
     * Writes the specified byte to this byte array output stream.
     *
     * @param   b   the byte to be written.
     */
    String dbgMsg="";
    public synchronized void write(int b) {
    	
//    	synchronized( lock)
//    	{
//    	
//        System.out.println("out write enter");
        
    	
        int newcount = count + 1;
//		System.out.println("write 1: "+ newcount);
      
  //      dbgMsg+=      "write 1: "+ newcount+"\n";
        if (newcount >= buf.length) {
        	checkAndWaitForSpace();
        	newcount = count + 1;
        }
//		System.out.println("write 2: "+ newcount);
//        dbgMsg+=      "write 2: "+ newcount+"\n";

  //      System.out.println("out write" + count);
        buf[count] = (byte)b;
        count = newcount;
        totalCount++;
        this.notify();
//    	}

//        synchronized (in)
//        {
//        	in.notify();
//        }
//        System.out.println("out write done");
    }
	 
	 
    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this byte array output stream.
     *
     * @param   b     the data.
     * @param   off   the start offset in the data.
     * @param   len   the number of bytes to write.
     */
    public synchronized void write(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        int newcount = count + len;
        while (newcount > buf.length) {
        	int block=buf.length-count;
        	if (count < buf.length)
        	{
        		System.arraycopy(b, off, buf, count, block);
        		count = buf.length;
            	off=off+block;
            	len=len-block;
            	totalCount += block ;
            	this.notify();
        	}
        	checkAndWaitForSpace();
        	newcount = count + len;
        }
        if (len > 0)
        {
	        System.arraycopy(b, off, buf, count, len);
	        count = newcount;
	    	totalCount += len ;
	    	this.notify();
        }
//        synchronized (in)
//        {
//        	in.notify();
//        }
  }
    
    /**
     * Closing a <tt>ByteArrayOutputStream</tt> has no effect. The methods in
     * this class can be called after the stream has been closed without
     * generating an <tt>IOException</tt>.
     * <p>
     *
     */
    public synchronized void close() throws IOException {
    	
//    	synchronized (lock)
//    	{
//    	System.out.println("CLOSE");
    	this.done = true ;
    	this.notify();
//    	}
    }

    
//    /**
//     * Writes the complete contents of this byte array output stream to
//     * the specified output stream argument, as if by calling the output
//     * stream's write method using <code>out.write(buf, 0, count)</code>.
//     *
//     * @param      out   the output stream to which to write the data.
//     * @exception  IOException  if an I/O error occurs.
//     */
//    public synchronized void writeTo(OutputStream out) throws IOException {
//        out.write(buf, 0, count);
//    }
//    
    protected void checkAndWaitForSpace()
    {
//		System.out.println("checkAndWaitForSpace : "+ mark);
//		System.out.println("checkAndWaitForSpace : "+ count);
    	if (mark > 0)
    	{
 //   		byte[] newBuf = new byte[buf.length];
    		System.arraycopy(buf, mark, buf, 0, count-mark);
 //   		buf=newBuf;
    		count -= mark;
    		mark=0;
//    		System.out.println("checkAndWaitForSpace 2: "+ count);
//    		System.out.println("checkAndWaitForSpace 2: "+ mark);
    	}
    	while (count >= buf.length)
    	{
    		try 
    		{
//        		System.out.println("checkAndWaitForSpace wait: ");
				this.wait();
			} 
    		catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
//    		System.out.println("checkAndWaitForSpace 4: "+ count);
//    		System.out.println("checkAndWaitForSpace 4: "+ mark);
       	    if (mark > 0)
        	{
   //     		byte[] newBuf = new byte[buf.length];
        		System.arraycopy(buf, mark, buf, 0, count-mark);
     //   		buf=newBuf;

//        		System.arraycopy(buf, 0, buf, mark, count-mark);
        		count -= mark;
        		mark=0;
//        		System.out.println("checkAndWaitForSpace 5: "+ count);
//        		System.out.println("checkAndWaitForSpace 5: "+ mark);
        	}
        }
    	
    }

    class BlockInputStream extends InputStream
    {
//    	protected BlockedBufferedOutputStream out ;
//    	
//    	public BlockInputStream(BlockedBufferedOutputStream out)
//    	{
//    		this.out = out ;
//    	}

        /**
         * Reads the next byte of data from this input stream. The value
         * byte is returned as an <code>int</code> in the range
         * <code>0</code> to <code>255</code>. If no byte is available
         * because the end of the stream has been reached, the value
         * <code>-1</code> is returned.
         * <p>
         * This <code>read</code> method
         * cannot block.
         *
         * @return  the next byte of data, or <code>-1</code> if the end of the
         *          stream has been reached.
         */
        public synchronized int read() 
        {
//        	System.out.println("read");
        	synchronized(BlockedBufferedOutputStream.this)
        	{
//            	System.out.println("BlockInputStream done " + done);
//            	System.out.println("BlockInputStream read " + mark);
//            	System.out.println("BlockInputStream read " + count);
	        	if (mark < count)
	        	{
	        		try
	        		{
	        			//return buf[mark++] & 0xff ;
	        			return buf[mark++] ;
	        		}
	        		finally
	        		{
	        			BlockedBufferedOutputStream.this.notify();
	        		}
	        	}
	        	if (done)
	        	{
	        		return -1;
	        	}
	        	else
	        	{
	        		try 
	        		{
//	                   	System.out.println("BlockInputStream read wait " );

	        			BlockedBufferedOutputStream.this.wait();
//	                   	System.out.println("BlockInputStream read wake " );
	    				return this.read();
	    			} 
	        		catch (InterruptedException e) {
	    				throw new RuntimeException(e);
	    			}
	        	}
        	}
        }
        
        /**
         * Reads up to <code>len</code> bytes of data into an array of bytes
         * from this input stream.
         * If <code>pos</code> equals <code>count</code>,
         * then <code>-1</code> is returned to indicate
         * end of file. Otherwise, the  number <code>k</code>
         * of bytes read is equal to the smaller of
         * <code>len</code> and <code>count-pos</code>.
         * If <code>k</code> is positive, then bytes
         * <code>buf[pos]</code> through <code>buf[pos+k-1]</code>
         * are copied into <code>b[off]</code>  through
         * <code>b[off+k-1]</code> in the manner performed
         * by <code>System.arraycopy</code>. The
         * value <code>k</code> is added into <code>pos</code>
         * and <code>k</code> is returned.
         * <p>
         * This <code>read</code> method cannot block.
         *
         * @param   b     the buffer into which the data is read.
         * @param   off   the start offset in the destination array <code>b</code>
         * @param   len   the maximum number of bytes read.
         * @return  the total number of bytes read into the buffer, or
         *          <code>-1</code> if there is no more data because the end of
         *          the stream has been reached.
         * @exception  NullPointerException If <code>b</code> is <code>null</code>.
         * @exception  IndexOutOfBoundsException If <code>off</code> is negative,
         * <code>len</code> is negative, or <code>len</code> is greater than
         * <code>b.length - off</code>
         */
        public synchronized int read(byte b[], int off, int len) {
        	return this.read(b, off, len, 0);
        }
        public synchronized int read(byte b[], int off, int len, int read) {
            if (b == null) {
                throw new NullPointerException();
            } else if (off < 0 || len < 0 ) {
                throw new IndexOutOfBoundsException();
            }
            
//            System.out.println("READ LEM :" +len);
//            System.out.println("READ mark :" +mark);
//            System.out.println("READ count :" +count);
//            System.out.println("READ off :" +off);
            int thisLen = len ;
        	boolean full = false;
//        	boolean cont = false;
        	synchronized(BlockedBufferedOutputStream.this)
        	{
                if (mark < count) 
                {
                    if (mark + len > count) 
                    {
                    	thisLen = count - mark ;
//                    	cont = true ;
                    }
//                    System.out.println("READ thisLen :" +thisLen);
                    System.arraycopy(buf, mark, b, off, thisLen);
               		off += thisLen;
               		read +=thisLen;
               		len -=thisLen;
                    mark += thisLen;
                    BlockedBufferedOutputStream.this.notify();
                }
                else
                {
                	full = true ;
                }
                
                if (done)
                {
                	if (read > 0)
                	{
                		return read ;
                	}
                	else
                	{
                		if (full)
                		{
                			return -1 ;
                		}
                		else
                		{
                			return 0 ;
                		}
                	}
                }
                else
                {
	        		try 
	        		{
//	                   	System.out.println("BlockInputStream read [] wait " );

	        			if (len>0)
	        			{
	        				BlockedBufferedOutputStream.this.wait();
	        				return this.read(b, off, len, read);
	        			}
	        			else
	        			{
	        				return read ;
	        			}
	    			} 
	        		catch (InterruptedException e) {
	    				throw new RuntimeException(e);
	    			}
                }
        	}
        }
    	
    }

}
