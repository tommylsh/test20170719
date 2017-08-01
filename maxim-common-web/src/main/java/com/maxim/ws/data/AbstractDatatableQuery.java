package com.maxim.ws.data;
//
//import java.util.Collection;
//import java.util.List;
//
//public abstract class AbstractDatatableQuery implements Query {
//
//	/**
//	 * @author Steven
//	 */
//	private static final long serialVersionUID = 20130723L;
//
//	private User user;
//
//	// DataTable specific attributes
//	private Integer iDisplayStart;
//	private Integer iDisplayLength;
//	private Integer iColumns;
//	private String sColumns;
//	private List<Boolean> bSearchable;
//	private List<String> sSearch;
//	private List<Boolean> bRegex;
//	private List<Boolean> bSortable;
//	private Integer iSortingCols;
//	private List<Integer> iSortCol;
//	private List<String> sSortDir;
//	private List<String> mDataProp;
//	private String sEcho;
//	
//	public abstract	Collection<DatatableFilterMapping> getFilterMappings();
//	
//
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//
//	public Integer getiDisplayStart() {
//		return iDisplayStart;
//	}
//
//	public void setiDisplayStart(Integer iDisplayStart) {
//		this.iDisplayStart = iDisplayStart;
//	}
//
//	public Integer getiDisplayLength() {
//		return iDisplayLength;
//	}
//
//	public void setiDisplayLength(Integer iDisplayLength) {
//		this.iDisplayLength = iDisplayLength;
//	}
//
//	public Integer getiColumns() {
//		return iColumns;
//	}
//
//	public void setiColumns(Integer iColumns) {
//		this.iColumns = iColumns;
//	}
//
//	public String getsColumns() {
//		return sColumns;
//	}
//
//	public void setsColumns(String sColumns) {
//		this.sColumns = sColumns;
//	}
//
//	public List<Boolean> getbSearchable() {
//		return bSearchable;
//	}
//
//	public void setbSearchable(List<Boolean> bSearchable) {
//		this.bSearchable = bSearchable;
//	}
//
//	public List<String> getsSearch() {
//		return sSearch;
//	}
//
//	public void setsSearch(List<String> sSearch) {
//		this.sSearch = sSearch;
//	}
//
//	public List<Boolean> getbRegex() {
//		return bRegex;
//	}
//
//	public void setbRegex(List<Boolean> bRegex) {
//		this.bRegex = bRegex;
//	}
//
//	public List<Boolean> getbSortable() {
//		return bSortable;
//	}
//
//	public void setbSortable(List<Boolean> bSortable) {
//		this.bSortable = bSortable;
//	}
//
//	public Integer getiSortingCols() {
//		return iSortingCols;
//	}
//
//	public void setiSortingCols(Integer iSortingCols) {
//		this.iSortingCols = iSortingCols;
//	}
//
//	public List<Integer> getiSortCol() {
//		return iSortCol;
//	}
//
//	public void setiSortCol(List<Integer> iSortCol) {
//		this.iSortCol = iSortCol;
//	}
//
//	public List<String> getsSortDir() {
//		return sSortDir;
//	}
//
//	public void setsSortDir(List<String> sSortDir) {
//		this.sSortDir = sSortDir;
//	}
//
//	public List<String> getmDataProp() {
//		return mDataProp;
//	}
//
//	public void setmDataProp(List<String> mDataProp) {
//		this.mDataProp = mDataProp;
//	}
//
//	public String getsEcho() {
//		return sEcho;
//	}
//
//	public void setsEcho(String sEcho) {
//		this.sEcho = sEcho;
//	}
//
//}
