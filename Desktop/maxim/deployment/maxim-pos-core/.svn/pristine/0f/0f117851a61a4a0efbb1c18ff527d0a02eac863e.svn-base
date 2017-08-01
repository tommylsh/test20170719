package com.maxim.pos.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.maxim.entity.AbstractEntity;

@Entity
@Table(name = "POLL_SCHEME_TABLE_COLUMN")
public class SchemeTableColumn extends AbstractEntity {

	private static final long serialVersionUID = 5661568046816286237L;

	private SchemeInfo schemeInfo;
	private Integer seq;
	private String fromColumn;
	private String fromColumnFormat;
	private Integer fromColumnLength;
	private String toColumn;
	private String toColumnFormat;
	private Integer toColumnLength;
	private Integer fromColumnPrecision;
	private Integer toColumnPrecision;

	@Override
	@Id
	@Column(name = "POLL_SCHEME_TABLE_COLUMN_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "POLL_SCHEME_INFO_ID", nullable = false)
	public SchemeInfo getSchemeInfo() {
		return schemeInfo;
	}

	public void setSchemeInfo(SchemeInfo schemeInfo) {
		this.schemeInfo = schemeInfo;
	}

	@Column(name = "FROM_COLUMN", length = 50)
	public String getFromColumn() {
		return fromColumn;
	}

	public void setFromColumn(String fromColumn) {
		this.fromColumn = fromColumn;
	}

	@Column(name = "TO_COLUMN", length = 50)
	public String getToColumn() {
		return toColumn;
	}

	public void setToColumn(String toColumn) {
		this.toColumn = toColumn;
	}

	@Column(name = "SEQ")
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "FROM_COLUMN_FORMAT", length = 20)
	public String getFromColumnFormat() {
		return fromColumnFormat;
	}

	public void setFromColumnFormat(String fromColumnFormat) {
		this.fromColumnFormat = fromColumnFormat;
	}

	@Column(name = "FROM_COLUMN_LENGTH", length = 20)
	public Integer getFromColumnLength() {
		return fromColumnLength;
	}

	public void setFromColumnLength(Integer fromColumnLength) {
		this.fromColumnLength = fromColumnLength;
	}

	@Column(name = "TO_COLUMN_FORMAT", length = 20)
	public String getToColumnFormat() {
		return toColumnFormat;
	}

	public void setToColumnFormat(String toColumnFormat) {
		this.toColumnFormat = toColumnFormat;
	}

	@Column(name = "TO_COLUMN_LENGTH", length = 20)
	public Integer getToColumnLength() {
		return toColumnLength;
	}

	public void setToColumnLength(Integer toColumnLength) {
		this.toColumnLength = toColumnLength;
	}

	@Column(name = "FROM_COLUMN_PRECISION")
	public Integer getFromColumnPrecision() {
		return fromColumnPrecision;
	}

	public void setFromColumnPrecision(Integer fromColumnPrecision) {
		this.fromColumnPrecision = fromColumnPrecision;
	}

	@Column(name = "TO_COLUMN_PRECISION")
	public Integer getToColumnPrecision() {
		return toColumnPrecision;
	}

	public void setToColumnPrecision(Integer toColumnPrecision) {
		this.toColumnPrecision = toColumnPrecision;
	}
	
	@Transient
	public String getFromColumnInfo(){
		return String.format("[%s]-[%s]-[%s]-[%s]", fromColumn, fromColumnFormat, fromColumnLength, fromColumnPrecision);
		
	}
	
	@Transient
	public String getToColumnInfo(){
		return String.format("[%s]-[%s]-[%s]-[%s]", toColumn, toColumnFormat, toColumnLength, toColumnPrecision);
	}

}
