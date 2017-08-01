package com.maxim.dao;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

public class BeanEnumPropertySqlParameterSource<T,K> extends BeanPropertySqlParameterSource {

	protected final T obj;
	protected final BeanWrapper beanWrapper;
	protected final JdbcResultSetExtrator<T,K> extrctor;

	public BeanEnumPropertySqlParameterSource(T object, JdbcResultSetExtrator<T,K> extrctor) {
		super(object);
		this.obj = object ;
		this.extrctor = extrctor ;
		this.beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
	}
	
	
	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		
		return this.extrctor.getValue(obj, paramName);
//		try {
//			Object obj = this.beanWrapper.getPropertyValue(paramName);
//			if (obj == null)
//			{
//				return null ;
//			}
//			if (obj.getClass().isEnum())
//			{
//				Enum<?> e = (Enum<?>) obj ;
//				String colname	= this.extrctor.propertyMap.get(paramName);
//				if (colname != null)
//				{
//					EnumType type	= this.extrctor.enumMap.get(colname);
//					if (type.equals(EnumType.ORDINAL))
//					{
//						return  e.ordinal();
//					}
//					else
//					{
//						return  e.name();
//					}
//				}
//				return  e.name();
//			}
//			else
//			{
//				return obj;
//			}
//		}
//		catch (NotReadablePropertyException ex) {
//			throw new IllegalArgumentException(ex.getMessage());
//		}
	}
	/**
	 * Derives a default SQL type from the corresponding property type.
	 * @see org.springframework.jdbc.core.StatementCreatorUtils#javaTypeToSqlParameterType
	 */
	@Override
	public int getSqlType(String paramName) {
		return this.extrctor.getSqlType(paramName);
//		int sqlType = super.getSqlType(paramName);
//		if (sqlType != TYPE_UNKNOWN) {
//			return sqlType;
//		}
//		Class<?> propType = this.beanWrapper.getPropertyType(paramName);
//		if (propType.isEnum())
//		{
//			propType = String.class ;
//			String colname	= this.extrctor.propertyMap.get(paramName);
//			if (colname == null)
//			{
//				System.out.println(paramName);
//				System.out.println(this.extrctor.propertyMap);
//			}
//			if (colname != null)
//			{
//				EnumType type	= this.extrctor.enumMap.get(colname);
//				if (type == null)
//				{
//					System.out.println(colname);
//					System.out.println(this.extrctor.enumMap);
//				}
//				if (type.equals(EnumType.ORDINAL))
//				{
//					propType = Integer.TYPE;
//				}
//				else
//				{
//					propType = String.class ;
//				}
//			}
//			
//		}
//		return StatementCreatorUtils.javaTypeToSqlParameterType(propType);
	}
}
