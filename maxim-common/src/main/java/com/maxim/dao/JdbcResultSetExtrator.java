package com.maxim.dao;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class JdbcResultSetExtrator<T,K> {

	protected Class<T> dtoClass = null ;
	protected Class<K> keyClass = null ;
	
	protected String tableName = null ;
	protected String[] generatedKeysColumnNames = null ;
	protected String[] colNames = null ;
	protected Map<String, String> propertyMap = null ;
	protected Map<String, Method> getterMap = null ;
	protected Map<String, Method> setterMap = null ;
	protected Map<String, EnumType> enumMap = null ;
	protected Map<String, Class<? extends java.util.Date>> temporalMap = null ;

	protected boolean keyPrimitive = true ;
	protected String primitiveColumn = null ;
	protected String seletAllColumns = null ;
	protected String keyClause = null ;
	protected String selectAllSql = null ;
	protected String selectByKeySql = null ;
	protected String updateByKeySql = null ;
	protected String deleteByKeySql = null ;
	protected String insertSql = null ;
	
	protected RowMapper<T> mapper = null ;
	
    protected JdbcResultSetExtrator(Class<T> dtoClass, Class<K> keyClass) {
        this.dtoClass = dtoClass;
        this.keyClass = keyClass;
        init();
    }	
    
	@SuppressWarnings("unchecked")
	protected JdbcResultSetExtrator() {
    	Type genericSuperclass = this.getClass().getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            Type type = pt.getActualTypeArguments()[0];
            dtoClass = (Class<T>) type;
            type = pt.getActualTypeArguments()[1];
            keyClass = (Class<K>) type;
        }
//        this.typeParameterClass = typeParameterClass;
        init();
    }
	
	public void init()
	{
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(dtoClass);
        Table tableAnnotaion = dtoClass.getAnnotation(Table.class);
        if (tableAnnotaion == null)
        {
        	return ;
        }
        this.tableName = tableAnnotaion.name();
        
//    	Java primitive type; any primitive wrapper type; String; java.util.Date; java.sql.Date; java.math.BigDecimal; java.math.BigInteger. 
        if (keyClass.isPrimitive() || keyClass.equals(String.class) || keyClass.equals(Date.class) || keyClass.equals(BigDecimal.class) || keyClass.equals(BigInteger.class) )
        {
        	keyPrimitive = true ;
        }
        else
        {
        	keyPrimitive = false ; 
        }
        
        List<String> generatedKeysColumnNameList = new ArrayList<String>() ; 

        propertyMap	= new HashMap<String, String>();
        getterMap	= new HashMap<String, Method>();
        setterMap	= new HashMap<String, Method>();
        enumMap		= new HashMap<String, EnumType>() ;
        temporalMap	= new HashMap<String, Class<? extends java.util.Date>>() ;
        
        List<Column> colList = new ArrayList<Column>();
        List<PropertyDescriptor> keyList = new ArrayList<PropertyDescriptor>();
        StringBuffer selectAllColunmBuffer = new StringBuffer();
        StringBuffer keyClauseBuffer = new StringBuffer();
        StringBuffer updateColunmBuffer = new StringBuffer();
        StringBuffer insertColunmBuffer = new StringBuffer();
        StringBuffer insertValueBuffer = new StringBuffer();
        
        for (PropertyDescriptor pd : pds)
        {
        	Method getter = pd.getReadMethod();
        	Column colunmAnnotaion = getter.getAnnotation(Column.class);
        	GeneratedValue generatedValue = getter.getAnnotation(GeneratedValue.class);
            Enumerated enumerated = getter.getAnnotation(Enumerated.class);
            Temporal temporal = getter.getAnnotation(Temporal.class);
            System.out.println(" colunmAnnotaion " +pd.getName());
        	if (colunmAnnotaion != null)
        	{
        		String colunmName = colunmAnnotaion.name() ;
        		colList.add(colunmAnnotaion);

                System.out.println(" colunmAnnotaion " +colunmName);

                propertyMap.put(pd.getName(), colunmName);
        		getterMap.put(colunmName, getter);
        		setterMap.put(colunmName, pd.getWriteMethod());
        		if (enumerated != null)
        		{
        			enumMap.put(colunmName, enumerated.value());
        		}
        		if (temporal != null)
        		{
        			TemporalType type = temporal.value() ;
        			if (type.equals(TemporalType.DATE))
        			{
        				temporalMap.put(colunmName, Date.class);
        			}
        			else if (type.equals(TemporalType.TIME))
        			{
        				temporalMap.put(colunmName, Time.class);
        			}
        			else if (type.equals(TemporalType.TIMESTAMP))
        			{
        				temporalMap.put(colunmName, Timestamp.class);
        			}
        		}
        		
        		selectAllColunmBuffer.append(colunmName).append(",");
        		
        		if (generatedValue == null)
        		{
	        		insertColunmBuffer.append(colunmName).append(",");
	        		insertValueBuffer.append(":").append(pd.getName()).append(",");
        		}
        		else
        		{
        			generatedKeysColumnNameList.add(colunmName);
        		}
            	Id idAnnotaion = getter.getAnnotation(Id.class);
                System.out.println(" idAnnotaion " +idAnnotaion);
            	if (idAnnotaion != null)
            	{
            		if (keyPrimitive)
            		{
            			primitiveColumn = colunmName ;
            		}
        			PropertyDescriptor pdKey = BeanUtils.getPropertyDescriptor(keyClass, pd.getName());
            		keyList.add(pdKey);
        			keyClauseBuffer.append(colunmName).append(" = :").append(pd.getName()).append(" and ");
            	}
            	else
            	{
            		updateColunmBuffer.append(colunmName).append(" = :").append(pd.getName()).append(",");
            	}
        	}
        }
        seletAllColumns = selectAllColunmBuffer.toString() ;
        seletAllColumns = seletAllColumns.substring(0, seletAllColumns.length()-1);
        String insertColumns = insertColunmBuffer.toString() ;
        insertColumns = insertColunmBuffer.substring(0, insertColumns.length()-1);
        String updateColunms = updateColunmBuffer.toString();
        updateColunms = updateColunms.substring(0, updateColunms.length()-1);
        String inserValue = insertValueBuffer.toString();
        inserValue = inserValue.substring(0, inserValue.length()-1);
        
        if (!keyList.isEmpty())
        {
	        keyClause = keyClauseBuffer.toString() ;
	        keyClause = keyClause.substring(0, keyClause.length()-5);

	        selectByKeySql	= "SELECT " + seletAllColumns + " FROM " + tableName + " WHERE " + keyClause;
	        deleteByKeySql	= "DELETE FROM " + tableName + " WHERE " + keyClause;
	        updateByKeySql	= "UPDATE " + tableName + " SET " + updateColunms + " WHERE " + keyClause;
        }
        if (!generatedKeysColumnNameList.isEmpty())
        {
        	generatedKeysColumnNames = generatedKeysColumnNameList.toArray(new String[0]);
        }

        selectAllSql	= "SELECT " + seletAllColumns + " FROM " + tableName ;
        insertSql		= "INSERT INTO " + tableName + "(" + insertColumns + ") VALUES (" + inserValue + ")";
        
        int i = 0 ;
        colNames = new String[colList.size()];
        for (Column col : colList)
        {
        	colNames[i++] = col.name();
        }
        
        mapper = new RowMapper<T>()
        		{

					@Override
					public T mapRow(ResultSet rs, int rowNum) throws SQLException {
						try {
							T obj = dtoClass.newInstance() ;
							for (String colname : colNames)
							{
								Method setter = setterMap.get(colname);
								Class<?>[] types  = setter.getParameterTypes();
								Class<?> argClass = types[0];
								if (argClass.equals(String.class))
								{
									String data = rs.getString(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(BigDecimal.class))
								{
									BigDecimal data = rs.getBigDecimal(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(Byte.class))
								{
									Byte data = rs.getByte(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(java.sql.Date.class))
								{
									java.util.Date data = rs.getTimestamp(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(Date.class))
								{
									Date data = rs.getDate(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(Double.class))
								{
									Double data = rs.getDouble(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(Integer.class))
								{
									Integer data = rs.getInt(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(Float.class))
								{
									Float data = rs.getFloat(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(Long.class))
								{
									Long data = rs.getLong(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(Short.class))
								{
									Short data = rs.getShort(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(Time.class))
								{
									Time data = rs.getTime(colname);
									setter.invoke(obj, data);
								} else if (argClass.equals(Timestamp.class))
								{
									Timestamp data = rs.getTimestamp(colname);
									setter.invoke(obj, data);
								} else 
								{
									Object data = rs.getObject(colname);
									setter.invoke(obj, data);
								}
							}
							return obj;
						} 
						catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
						return null;
						
					}
        	
        		};
        		
	}
	
	 
//	
//	@Override
//	public List<T> extractData(ResultSet rs) throws SQLException {
//
//		List<T> results = new ArrayList<T>();
//		while (rs.next()) {
//			try {
//				T obj = this.dtoClass.newInstance() ;
//				for (String colname : colNames)
//				{
//					Method setter = this.setterMap.get(colname);
//					Class<?>[] types  = setter.getParameterTypes();
//					Class<?> argClass = types[0];
//					if (argClass.equals(String.class))
//					{
//						String data = rs.getString(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(BigDecimal.class))
//					{
//						BigDecimal data = rs.getBigDecimal(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(Byte.class))
//					{
//						Byte data = rs.getByte(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(Date.class))
//					{
//						Date data = rs.getDate(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(Double.class))
//					{
//						Double data = rs.getDouble(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(Integer.class))
//					{
//						Integer data = rs.getInt(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(Float.class))
//					{
//						Float data = rs.getFloat(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(Long.class))
//					{
//						Long data = rs.getLong(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(Short.class))
//					{
//						Short data = rs.getShort(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(Time.class))
//					{
//						Time data = rs.getTime(colname);
//						setter.invoke(obj, data);
//					} else if (argClass.equals(Timestamp.class))
//					{
//						Timestamp data = rs.getTimestamp(colname);
//						setter.invoke(obj, data);
//					} else 
//					{
//						Object data = rs.getObject(colname);
//						setter.invoke(obj, data);
//					}
//				}
//				results.add(obj);
//			} 
//			catch (IllegalAccessException | InstantiationException | IllegalArgumentException | InvocationTargetException e) {
//				e.printStackTrace();
//			}
//		}
//		return results;
//	}
	public Object getValue(T obj, String paramName) throws IllegalArgumentException {
		
		String colname	= this.propertyMap.get(paramName);
		if (colname != null)
		{
			Method method	= this.getterMap.get(colname);
			
			try {
				return method.invoke(obj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
				throw new IllegalArgumentException(ex.getMessage());
			}
		}
		return null;
	}

	public int getSqlType(String paramName) {
		String colname	= this.propertyMap.get(paramName);
		if (colname != null)
		{
			Method method	= this.getterMap.get(colname);
			if (method != null)
			{
				Class<?> propType = method.getReturnType();
				if (propType.isEnum())
				{
					EnumType type	= this.enumMap.get(colname);
					if (type.equals(EnumType.ORDINAL))
					{
						propType = Integer.TYPE;
					}
					else
					{
						propType = String.class ;
					}
				}
				return StatementCreatorUtils.javaTypeToSqlParameterType(propType);
			}
		}
		return SqlParameterSource.TYPE_UNKNOWN ;
	}

	public SqlParameterSource getKeyParameterSource(K key)
	{
		if (this.keyPrimitive)
		{
			HashMap<String, K> map = new HashMap<String, K>();
			map.put(primitiveColumn, key);
			return new MapSqlParameterSource(map);
		}
		else
		{
			return new BeanPropertySqlParameterSource(key);
		}
		
	}
	
	public SqlParameterSource getParameterSource(T obj)
	{
		return new BeanEnumPropertySqlParameterSource<T,K>(obj, this);
	}
	public SqlParameterSource[] getParameterSource(List<T> objs)
	{
		SqlParameterSource[] batch = new SqlParameterSource[objs.size()];
		int i = 0 ;
		for (T obj : objs) {
			batch[i] = getParameterSource(obj);
			i++;
		}
		return batch;
	}
	
	public void updateGeneratedKeysColumnNames(Map<String, Object> keys, T obj)
	{
		for (String colname : keys.keySet())
		{
			Number value = (Number) keys.get(colname);
			Method setter = setterMap.get(colname);
			try {
				setter.invoke(obj, value);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	public void updateGeneratedKeysColumnNames(ResultSet rs, T obj) 
	{
		for (String colname : generatedKeysColumnNames)
		{
			try {
				Long value = rs.getLong(colname);
				Method setter = setterMap.get(colname);
				setter.invoke(obj, value);
			} catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
