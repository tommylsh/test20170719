import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class MQAnaylsis {

	boolean debug = false;
	String debugName="'SXXXXsST'";
	
	interface Foo
	{
	   public void run();	
	}
	
	class Queue
	{
		boolean cont = false ;
		String name ;
		String type ;
		Map<String , String> map = new LinkedHashMap<String, String>();
			
		void parse(String str)
		{
			this.cont = false ;
			
//			System.out.println(str);
			
			str = str.trim() ;
			
			if (str.isEmpty()) return ;
//			if (!token.hasMoreTokens()) return ;
			
//			String chk = token.nextToken();
//			
//			if (!token.hasMoreTokens())
//			{
//				if (chk.equals("+"))
				if (str.trim().equals("+"))
				{
					this.cont = true ;
					return ;
				}
				else
				{
					this.cont = false ;
				}
//			}
				
			int bdx = str.indexOf("(") ;
			int edx = str.indexOf(")") ;
			int sdx = str.indexOf(" ") ;
			int idx = str.indexOf("'") ;
			if (debugName.equals(name))
			{
				System.out.println(bdx);
				System.out.println(edx);
				System.out.println(idx);
			}	
			if (bdx > -1)
			{
			
				idx = str.indexOf("'",bdx) ;
				if (debugName.equals(name))
				{
					System.out.println(bdx);
					System.out.println(edx);
					System.out.println(idx);
				}					
				if (idx > -1 && (idx < edx || edx < 0 ))
				{
					idx = str.indexOf("'",idx+1) ;
					if (debugName.equals(name))
					{
						System.out.println(bdx);
						System.out.println(edx);
						System.out.println(idx);
					}	

					if (idx > -1)
					{
						edx = str.indexOf(")",idx) ;
						sdx = str.indexOf(" ",idx) ;
					}
					if (debugName.equals(name))
					{
						System.out.println(bdx);
						System.out.println(edx);
						System.out.println(idx);
					}						
				}
				if (edx > -1)
				{
					sdx = str.indexOf(" ",edx) ;
				}
			}
			if (edx > -1)
			{
				idx = edx ;
			}
			else
			{
				idx = sdx ;
			}
			
			if (debugName.equals(name))
			{
				System.out.println(bdx);
				System.out.println(edx);
				System.out.println(idx);
			}	
			
//			String chk = str;
			String oth = "" ;
			String propName = str;			
			String propValue = null;			
			if (bdx > -1 && edx > -1)
			{
				propName = str.substring(0, bdx).trim();
				propValue = str.substring(bdx+1, edx);
				oth = str.substring(edx+1).trim();				
			}
			else if (bdx > -1 && sdx > -1)
			{
				propName = str.substring(0, bdx).trim();
				propValue = str.substring(bdx+1, sdx);
				oth = str.substring(sdx+1).trim();				
			}
			else if (idx > -1)
			{
				propName = str.substring(0, idx).trim();
				oth = str.substring(idx+1).trim();				
			}
			
			map.put(propName, propValue);
			
			if (type==null)
			{
				this.type = propName ;
				this.name = propValue ;
//				System.out.println(type + " : "+ name);
//				parse(oth);
//				return ;
			}
			if (propName.equals("CHLTYPE"))
			{
				if (this.type.equals("CHANNEL"))
				{
					this.type = propValue + " " + this.type ;
				}
			}
			if (propName.equals("USAGE"))
			{
				if (this.type.equals("QLOCAL") && propValue.equals("XMITQ"))
				{
					this.type = propValue;
				}
			}
			

//			if (name == null)
//			{
//				if (chk.startsWith("('") && chk.endsWith("')"))
//				{
//					name = chk.substring(2,chk.length()-2);
//					System.out.println(name);
//					parse(token);
//					return ;
//				}
//			}
//			int startBlanket = chk.indexOf("(") ;
//			int endBlanket = chk.indexOf(")") ;
//			if ("'MQ7PDCAL1.CAL.AUTR'".equals(name))
//			{
//				System.out.println(chk);
//				System.out.println(propName);
//				System.out.println(propValue);
//			}
			
//			if (startBlanket>-1 && endBlanket > -1)
//			{
//				String propName = chk.substring(0, startBlanket);
//				String propValue = chk.substring(startBlanket+1);
//				map.put(propName, propValue);
//			}
			
			parse(oth);
			return ;				
			
		}
	}
	
	File mqfile = null ;
	String mqmName = null ;
	List<Queue> qList = new ArrayList<Queue>();
	Map<String, Map<String, Queue>> qMap = new LinkedHashMap<String, Map<String, Queue>>();
//	LinkedHashMap map = new LinkedHashMap();
	LinkedHashMap<String, ArrayList<String>> propListMap = new LinkedHashMap<String, ArrayList<String>>();
	List<String> rqList = new ArrayList<String>();

	public MQAnaylsis(File newMqfile) throws IOException
	{
		this.mqfile = newMqfile ;
		
		mqmName = this.mqfile.getName();
		mqmName = mqmName.substring(0,mqmName.toLowerCase().indexOf(".mqsc"));

		
//		FileInputStream fis = new FileInputStream(mqfile) ;
//		BufferedInputStream bis = new BufferedInputStream(fis);
		FileReader fr = new FileReader(mqfile);
		BufferedReader br = new BufferedReader(fr);
		String st = null ;
		String str = "" ;
		String unclassify = "";
		Queue q = null ;
		long n = 0 ;
		while ( (st = br.readLine()) != null)
		{
			n ++ ;
			if (!st.startsWith("*"))
			{
				if (st.endsWith("-"))
				{
					str += st.substring(0, st.length()-1) ;
					continue ;							
				}
				str += st ;

				if (q == null)
				{
//					System.out.println(str);
					if (str.startsWith("DEFINE "))
					{
//						System.out.println(str);
						
//						StringTokenizer token = new StringTokenizer(str);
//						token.nextToken();
						q = new Queue();
						q.parse(str.substring(6));
					}
					else if (!str.trim().isEmpty())
					{
						unclassify += n + ": " +str + System.lineSeparator();
					}
				}
				else
				{
					q.parse( str);
				}
				if (q != null)
				{
					
					if (!q.cont)
					{
						qList.add(q);
						
						if (q.map.containsKey("RQMNAME"))
						{
							String rq = q.map.get("RQMNAME");
							if (!rqList.contains(rq))
							{
								rqList.add(rq);
							}
						}

						
						String lastPropName = null ;
						ArrayList<String> propList = propListMap.get(q.type);
						if (propList == null)
						{
							propList = new ArrayList<String>();
							propListMap.put(q.type, propList);
						}
						Map<String ,Queue> thisMap = qMap.get(q.type);
						if (thisMap == null)
						{
							thisMap = new LinkedHashMap<String, Queue>();
							qMap.put(q.type, thisMap);
						}
						if (thisMap.containsKey(q.name))
						{
							System.err.println("Duplicae name : "+q.name);
							System.exit(-1);
						}
						else
						{
							thisMap.put(q.name, q);
						}
						
						for (String propName : q.map.keySet())
						{
							if (propList.contains(propName))
							{
								lastPropName = propName ;
								continue ;
							}
//							if (propName.equals("NOTRIGGER"))
//							{
//								System.out.println(propName +" - lastPropName :"+ lastPropName);
//							}
							
							
							if (lastPropName == null)
							{
								propList.add(propName);
							}
							else
							{
								int idx = propList.indexOf(lastPropName) ;
								propList.add(idx+1,propName);
							}
							
							lastPropName = propName ;
						}
						q = null ;
					}
				}
					
			}
			
			str="";
		}
		br.close();
		fr.close();
		System.out.println(unclassify);
	}
	
	void getRQ()
	{
		
		for (String rq : rqList)
		{
			System.out.println(rq);
		}
	}
	
	void transform(File outputDir) throws IOException
	{
		
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		
		
//		FileInputStream fis = new FileInputStream(myFile); // Finds the workbook instance for XLSX file XSSFWorkbook myWorkBook = new XSSFWorkbook (fis); // Return first sheet from the XLSX workbook XSSFSheet mySheet = myWorkBook.getSheetAt(0);
//
//		Read more: http://www.java67.com/2014/09/how-to-read-write-xlsx-file-in-java-apache-poi-example.html#ixzz4XVYvUHfL
		
//		System.out.println("Queue : " + qList.size());
//		for (Queue q : qList)
//		{
//			System.out.println(q.name + " : " +q.type);
//		}
		for (String type : propListMap.keySet())
		{
			Sheet sh = wb.createSheet(type);
			Row row = sh.createRow(0);
			
			ArrayList<String> propList = propListMap.get(type);
//			System.out.println("====="+type+"===========");
//			System.out.print("Name\t");
			int cellnum=0;
			for (String propName : propList)
			{
//				System.out.print(propName+"\t");
				Cell cell = row.createCell(cellnum++);
//                String address = new CellReference(cell).formatAsString();
                cell.setCellValue(propName);
			}
//			System.out.println();
			int rownum=1;
			for (Queue q : qList)
			{
				if (q.type.equals(type))
				{
//					System.out.print(q.name+"\t");
					row = sh.createRow(rownum++);
					cellnum=0;
					for (String propName : propList)
					{
						String value = null ;
						if (q.map.containsKey(propName))
						{
							value = q.map.get(propName);
							if (value == null)
							{
								value = "[TRUE]" ;
							}
						}
						
						Cell cell = row.createCell(cellnum++);
		                cell.setCellValue(value);

//						System.out.print(value+"\t");
					}
//					System.out.println();
					
					if (type.equals("QLOCAL"))
					{
						if (q.map.containsKey("RQMNAME"))
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue(q.map.get("RQMNAME"));
						}					
						else
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue("");
						}
						if (q.map.containsKey("RQMNAME"))
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue(q.map.get("RQNAME"));
						}				
						else
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue("");
						}
						if (q.map.containsKey("RQMNAME_ALIAS"))
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue(q.map.get("RQMNAME_ALIAS"));
						}				
						else
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue("");
						}
						if (q.map.containsKey("RQMNAME_ALIAS"))
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue(q.map.get("RQNAME_ALIAS"));
						}				
						else
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue("");
						}
						if (q.map.containsKey("ALIAS"))
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue(q.map.get("ALIAS"));
						}				
						else
						{
							Cell cell = row.createCell(cellnum++);
			                cell.setCellValue("");
						}
					}
					
				}

			}
		}
		
		File f = new File(outputDir,this.mqmName+".xlsx");
		
        FileOutputStream out = new FileOutputStream(f);
        wb.write(out);
        out.close();

        // dispose of temporary files backing this workbook on disk
        wb.dispose();	
        wb.close();
	}
	
	
	public static void getContent(List<?> o)
	{
		TypeVariable<?>[] t =o.getClass().getTypeParameters();
        System.out.println(t);
        
        for (TypeVariable tv : t)
        {
            System.out.println(tv);
        }

//        if (genericSuperclass instanceof ParameterizedType) {
//            ParameterizedType pt = (ParameterizedType) genericSuperclass;
//            Type type = pt.getActualTypeArguments()[0];
//            
//            System.out.println(type);
//        }

	}

	public static void main(String[] args) throws IOException, InterruptedException {
		
		List<?> os = new ArrayList<String>();
		List<String> ss = new ArrayList<String>();
		List<Integer> is = new ArrayList<Integer>();
		
		getContent(os);
		getContent(ss);
		getContent(is);

		{
			TypeVariable<?>[] t =ss.getClass().getTypeParameters();
	        System.out.println(t);
	        
	        for (TypeVariable tv : t)
	        {
	            System.out.println(tv.getName());
	        }
		}
		{
			TypeVariable<?>[] t =is.getClass().getTypeParameters();
	        System.out.println(t);
	        
	        for (TypeVariable tv : t)
	        {
	            System.out.println(tv);
	        }
		}
		System.exit(0);

		BlockedBufferedOutputStream out = new BlockedBufferedOutputStream(10);
		
		Thread t = new Thread()
		{
			public void run()
			{
				try 
				{

					FileInputStream fi = new FileInputStream("mqsc\\MQ71DMZP2.MQSC");
//					System.out.print("mqsc\\MQ71DMZP2.MQSC");

//					int i = fi.read();
					byte[] b = new byte[10];
					int i =fi.read(b);
//					System.out.println(i);
					while (i != -1)
					{
//						System.out.print((char)i);
//						for (byte bb: b)
//						{
//							System.out.print((char)bb);
//						}
						out.write(b,0,i);
						//i = fi.read();
						i =fi.read(b);
//						System.out.println(i);
						
					}
//					System.out.println("THREAD SinisH");
//					byte[] b = new byte[100000];
//					
//					for (byte bb : b)
//					{
//						out.write(bb);
//					}
					fi.close();
//					System.out.print("mqsc\\MQ71DMZP2.MQSC");
//					out.done =true;
					out.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		t.start();
//		System.exit(0);
		
		InputStream in = out.in;
//		System.out.print("PRE");
		byte[] b = new byte[10];
		int i = in.read(b);
//		int i = in.read();
//		System.out.print("start sss" + (char)i);
//		System.out.println("START");
		while (i != -1)
		{
			for (byte bb: b)
			{
				System.out.print((char)bb);
			}	
//			System.out.println(" - "+i);
//			System.out.print((char)i);
			b = new byte[10];
			i = in.read(b);
//			 i = in.read();
			
		}
		in.close();

		t.join();
		
		//System.out.println(out.dbgMsg);
		System.exit(0);
		
//		String file ="MQ71DMZP2";
		FileFilter filter = new FileFilter()
		{
			public boolean accept(java.io.File f)
			{
				if (f.getName().toLowerCase().endsWith(".mqsc"))
				{
					return true ;
				}
				return false ;
			}
		};
		File d = new File("mqsc");
		File o = new File("xlsx");
		List<String> allrq = new ArrayList<String>();
		List<MQAnaylsis> qaList = new ArrayList<MQAnaylsis>();
		for (File f : d.listFiles(filter))
		{
			System.out.println("========"+f.getName());			
//			File f = new File("MQ71DMZP2.MQSC");
			MQAnaylsis qa = new MQAnaylsis(f);
//			qa.getRQ();
			for (String rq : qa.rqList)
			{
				System.out.println(rq);			
				if (!allrq.contains(rq))
				{
					allrq.add(rq);
				}
			}
			
			qaList.add(qa);
			
//			qa.transform(o);			
		}
		
		for (MQAnaylsis qa : qaList)
		{
			for ( Queue lq : qa.qMap.get("QLOCAL").values())
			{
	//			 Map<String, Queue> lq = qa.qMap.get(lqName) ;
				List<String> alliasList = new ArrayList<String>();
				for ( Queue lq2 : qa.qMap.get("QALIAS").values())
				{
					
					if (qa.mqmName.equals("MQ71DMZP1"))
					{

					}

					
					if (lq.name.equals(lq2.map.get("TARGET")))
					{
						if (lq.name.equals("'CPA.AIRCOM.AOC'"))
						{
							System.out.println("LQ: " +lq.name);							
							System.out.println("LQ2: " +lq2.name);							
							System.out.println("LQ2 TARGET: " +lq2.map.get("TARGET"));							
						}
						alliasList.add(lq2.name);
						if (lq.map.containsKey("ALIAS"))
						{
							lq.map.put("ALIAS", lq.map.get("ALIAS") +"," + lq2.name);
						}
						else
						{
							lq.map.put("ALIAS", lq2.name);
						}
						if (lq.name.equals("'CPA.AIRCOM.AOC'"))
						{
							System.out.println("LQ ALIAS: " +lq.map.get("ALIAS"));							
						}
					}
				}
				 for (MQAnaylsis qa2 : qaList)
				 {
					if (qa2.qMap.containsKey("QREMOTE"))
						
					for ( Queue rq  : qa2.qMap.get("QREMOTE").values())
					{

						String rqm = rq.map.get("RQMNAME");
						String rlq = rq.map.get("RNAME");
						if (qa.mqmName.equals("MQ70PD1") && qa2.mqmName.equals("MQ71DMZP1"))
						{
							if (lq.name.equals("'AIRCOM.ULTRA.CMC'"))
							{
								if (alliasList.contains(rlq))
									
								for (String alias : alliasList)
								{
									System.out.println("ALIAS " + alias);
									System.out.println("rlq " + rlq);
								}
								
							}
						}

						if (qa.mqmName.equals("MQ70PD1") && qa2.mqmName.equals("MQSP1A"))
						{

							if (lq.name.equals("'AIRPATH.EFF.FLIGHTPLAN.EN'"))
							{
								if (rlq.equals("'AIRPATH.EFF.FLIGHTPLAN.EN'"))
								{
									System.out.println("LQ: " +lq.name);							
									System.out.println("rlq: " +rlq);
									System.out.println("LQM: " +qa.mqmName);							
									System.out.println("rqm: " +rqm);
									System.out.println("rqm: " +rqm);
									System.out.println("rqm: " +rqm);
								}	
							}
						}

						
						if (qa.mqmName == null)
						{
							System.out.println(qa.mqfile + " is null QM");
						}
						if (lq.name == null)
						{
							System.out.println(qa.mqfile + " is null LQ Name");
						}
						if (("'"+qa.mqmName+"'").equals(rqm) )
						{
							if (lq.name.equals(rlq))
							{
								if (lq.map.containsKey("RQMNAME"))
								{
									lq.map.put("RQMNAME", lq.map.get("RQMNAME") +"," + qa2.mqmName);
									lq.map.put("RQNAME", lq.map.get("RQNAME") +"," + rlq);
								}
								else
								{
									lq.map.put("RQMNAME", qa2.mqmName);
									lq.map.put("RQNAME", rlq);
								}
							}
							if (alliasList.contains(rlq))
							{
								if (lq.map.containsKey("RQMNAME_ALIAS"))
								{
									lq.map.put("RQMNAME_ALIAS", lq.map.get("RQMNAME_ALIAS") +"," + qa2.mqmName);
									lq.map.put("RQNAME_ALIAS", lq.map.get("RQNAME_ALIAS") +"," + rlq);
								}
								else
								{
									lq.map.put("RQMNAME_ALIAS", qa2.mqmName);
									lq.map.put("RQNAME_ALIAS", rlq);
								}
							}
						}
					}

				 }
				
			}
		}
		
		for (MQAnaylsis qa : qaList)
		{
			qa.transform(o);
		}
		

		System.out.println("========");			
		for (String rq : allrq)
		{
			System.out.println(rq);
		}
	
	     Foo f = (Foo) Proxy.newProxyInstance(Foo.class.getClassLoader(),
                 new Class[] { Foo.class },
                 new InterfaceHandler());
	     
	     f.run();

	}
	
	 public static class InterfaceExcutionContext {
			
		 protected Object proxy = null ;
		 protected Method method = null ;
		 protected Object[] args = null ;
		 protected Map<String, Object> map = null ;

		 
		 public Object execute(Object obj)
		 {
			 return null;
		 }
	 }

	 private static class InterfaceHandler implements InvocationHandler {
	//        private Object invokee;

	  //      public SimpleInvocationHandler(Object invokee) {
	   //         this.invokee = invokee;
	    //    }
		 
		 private Converter[] outBoundCoverter ;		 
		 private Transportor[] transportor ;		 
		 private Converter[] inBoundCoverter ;
		 


	        public Object invoke(Object proxy, Method method, Object[] args)
	                throws Throwable {

        	    InterfaceExcutionContext context = new InterfaceExcutionContext();

	        	context.proxy = proxy;
	        	context.method = method;
	        	context.args = args;	
	        	context.map = new HashMap<String, Object>();
	        	
	        	Object obj = args.length > 0 ? args[0] : null ;
	        	for (Converter c : outBoundCoverter)
	        	{
	        		obj = c.convert(context, obj);
	        	}
	        	for (Transportor t : transportor)
	        	{
	        		obj = t.transport(context, obj);
	        	}
	        	for (Converter c : inBoundCoverter)
	        	{
	        		obj = c.convert(context, obj);
	        	}
	        	return obj;

	        }
	 }
	 
 
	 private static class Converter {
		 
		 public Object convert(InterfaceExcutionContext context, Object obj)
		 {
			 return null;
		 }
	 }
	 private static class Transportor {
		 
		 public Object transport(InterfaceExcutionContext context, Object obj)
		 {
			 return null;
		 }
	 }
	
}