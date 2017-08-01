package com.maxim.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class JdbcEntityDAO<T,K> 
implements BaseEntityDAO<T,K> 
{

	protected Class<T> dtoClass = null ;
	protected Class<K> keyClass = null ;
	protected JdbcResultSetExtrator<T,K> extrator = null ;

//	protected NamedParameterJdbcTemplate nameJdbcTempalte = null ; 
//	protected JdbcTemplate jdbcTempalte = null ;
//	
//	public abstract void setDatasource(DataSource datasource) ;
	
//	protected abstract JdbcTemplate getJdbcTemplate() ;
	protected abstract NamedParameterJdbcTemplate getNamedJdbcTemplate() ;

    protected JdbcEntityDAO(Class<T> dtoClass, Class<K> keyClass) {
        this.dtoClass = dtoClass;
        this.keyClass = keyClass;
        this.extrator = new JdbcResultSetExtrator<T, K>(dtoClass, keyClass) ;
    }	
    
	@SuppressWarnings("unchecked")
	protected JdbcEntityDAO() {
    	Type genericSuperclass = this.getClass().getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            Type type = pt.getActualTypeArguments()[0];
            dtoClass = (Class<T>) type;
            type = pt.getActualTypeArguments()[1];
            keyClass = (Class<K>) type;
        }
//        this.typeParameterClass = typeParameterClass;
        this.extrator = new JdbcResultSetExtrator<T, K>(dtoClass, keyClass);
    }  
	
	@SuppressWarnings("unchecked")
	protected Class<T> getDataClass()
	{
		Class<T> dataClass = null;
    	Type genericSuperclass = this.getClass().getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            Type type = pt.getActualTypeArguments()[0];
            dataClass = (Class<T>) type;
        }
        
        return dataClass;
    }
	
	@Override
	public T findByKey(K key) {
		String sql = this.extrator.selectByKeySql ;
		SqlParameterSource source = this.extrator.getKeyParameterSource(key);
		try
		{
			T obj = this.getNamedJdbcTemplate().queryForObject(sql, source, this.extrator.mapper);
			
			return obj ;
        }catch (EmptyResultDataAccessException e)
        {
            return null;    	
        }		
		
	}

	public T find(T obj) {
		String sql = this.extrator.selectByKeySql ;
		SqlParameterSource source = this.extrator.getParameterSource(obj);
		try
		{
			return this.getNamedJdbcTemplate().queryForObject(sql, source, this.extrator.mapper);
        }catch (EmptyResultDataAccessException e)
        {
            return null;    	
        }		
		
	}
	
	public void insert(T obj) {
		String sql = this.extrator.insertSql ;
		SqlParameterSource source = this.extrator.getParameterSource(obj);
		
		if (this.extrator.generatedKeysColumnNames == null)
		{
			this.getNamedJdbcTemplate().update(sql, source);
		}
		else
		{
			KeyHolder holder = new GeneratedKeyHolder();
			this.getNamedJdbcTemplate().update(sql, source, holder, this.extrator.generatedKeysColumnNames);
			
			this.extrator.updateGeneratedKeysColumnNames(holder.getKeys(),obj);
		}
	}
	
	public void batchInsertWithoutReturnGenKey(List<T> objs) {
		if (objs.size() <= 0)
		{
			return ;
		}
		String sql = this.extrator.insertSql ;
        SqlParameterSource[] sources = this.extrator.getParameterSource(objs); 
//		ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
//		String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, sources[0]);
//
//		for (int i = 0; i < sources.length; i++) {
//			Object[] values = NamedParameterUtils.buildValueArray(parsedSql, sources[i], null);
//			int[] columnTypes = NamedParameterUtils.buildSqlTypeArray(parsedSql, sources[i]);
//
//			for (Object value : values){
//				
//				System.out.println(value);
//			}
//			for (int columnType : columnTypes){
//				
//				System.out.println(columnType);
//			}
//			break;
//		}        
		this.getNamedJdbcTemplate().batchUpdate(sql, sources);
	}
	public void batchInsert(List<T> objs) {
		if (objs.size() <= 0)
		{
			return ;
		}
		String sql = this.extrator.insertSql ;
		SqlParameterSource[] sources = this.extrator.getParameterSource(objs);
		if (this.extrator.generatedKeysColumnNames == null)
		{
			this.getNamedJdbcTemplate().batchUpdate(sql, sources);
		}
		else
		{
			ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
			String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, sources[0]);
			
			KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
			PreparedStatementCreator psc = new PreparedStatementCreator()
			{
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sqlToUse, extrator.generatedKeysColumnNames);
					
					return ps;
				}
			};
			
			ResultSetExtractor<List<T>> resultSetExtractor = new ResultSetExtractor<List<T>>()
			{
				public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
					for (T obj : objs)
					{
						if (rs.next()) {
							extrator.updateGeneratedKeysColumnNames(rs, obj);
						}
					}
					return objs;
				}
			};
			
			PreparedStatementCallback<Integer> psb = new PreparedStatementCallback<Integer>() {
				@Override
				public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException {
					for (int i = 0; i < sources.length; i++) {
						Object[] values = NamedParameterUtils.buildValueArray(parsedSql, sources[i], null);
						int[] columnTypes = NamedParameterUtils.buildSqlTypeArray(parsedSql, sources[i]);
						setStatementParameters(values, ps, columnTypes);
						ps.addBatch();
					}
					int[] returnInts =  ps.executeBatch();
					
					int sum = IntStream.of(returnInts).sum();
					
					List<Map<String, Object>> generatedKeys = generatedKeyHolder.getKeyList();
					generatedKeys.clear();
					ResultSet keys = ps.getGeneratedKeys();
					if (keys != null) {
						try {
							resultSetExtractor.extractData(keys);
						}
						finally {
							JdbcUtils.closeResultSet(keys);
						}
					}
					return sum;
				}
				
				protected void setStatementParameters(Object[] values, PreparedStatement ps, int[] columnTypes) throws SQLException {
					int colIndex = 0;
					for (Object value : values) {
						colIndex++;
						if (value instanceof SqlParameterValue) {
							SqlParameterValue paramValue = (SqlParameterValue) value;
							StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
						}
						else {
							int colType;
							if (columnTypes == null || columnTypes.length < colIndex) {
								colType = SqlTypeValue.TYPE_UNKNOWN;
							}
							else {
								colType = columnTypes[colIndex - 1];
							}
							StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
						}
					}
				}
			};				
			this.getNamedJdbcTemplate().getJdbcOperations().execute(psc, psb);
		}
	}
	
	public void batchUpdate(List<T> objs) {
		if (objs.size() <= 0)
		{
			return ;
		}
		String sql = this.extrator.updateByKeySql ;
		SqlParameterSource[] sources = this.extrator.getParameterSource(objs);
		this.getNamedJdbcTemplate().batchUpdate(sql, sources);
	}
	
	public void batchDelete(List<T> objs) {
		if (objs.size() <= 0)
		{
			return ;
		}
		String sql = this.extrator.deleteByKeySql ;
		SqlParameterSource[] sources = this.extrator.getParameterSource(objs);
		this.getNamedJdbcTemplate().batchUpdate(sql, sources);
	}
	
	@Override
	public T update(T obj) {
		String sql = this.extrator.updateByKeySql ;
		SqlParameterSource source = this.extrator.getParameterSource(obj);
		this.getNamedJdbcTemplate().update(sql, source);
		return obj;
	}
	
	@Override
	public T deleteByKey(K key) {
		T obj = this.findByKey(key);
		
		String sql = this.extrator.deleteByKeySql ;
		SqlParameterSource source = this.extrator.getKeyParameterSource(key);
		this.getNamedJdbcTemplate().update(sql, source);

        return obj;
	}
	
	@Override
	public void delete(T obj) {
		String sql = this.extrator.deleteByKeySql ;
		SqlParameterSource source = this.extrator.getParameterSource(obj);
		this.getNamedJdbcTemplate().update(sql, source);
	}
	
	@Override
	public List<T> findAll() {
		String sql = this.extrator.selectAllSql;
		return this.getNamedJdbcTemplate().query(sql, this.extrator.mapper);
	}
    

	@Override
    public void mergeInsert(T obj) {
		this.find(obj);
		
		insert(obj);
    }

	@Override
    public void mergerDelete(T obj) {
		this.find(obj);
		
		delete(obj);
    }
    




}
