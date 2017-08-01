package com.maxim.pos.common.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.IndexColumn;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.PollSchemeType;

@Entity
@Table(name = "POLL_SCHEME_INFO")
public class SchemeInfo extends AbstractEntity {

	private static final long serialVersionUID = 3707873728287097961L;

	private String pollSchemeType;
	private ClientType clientType;
	private String source;
	private String destination;
	private String destCheckSumCols;
	private String destKeyColumns;
	private String srcCheckSumCols;
	private String srcKeyColumns;
	private String delimiter;
	private boolean consistentStructure;
	private boolean isOverride;
	private boolean splitDateRequired;
	private List<SchemeTableColumn> schemeTableColumns = new ArrayList<SchemeTableColumn>();

	@Override
	@Id
	@Column(name = "POLL_SCHEME_INFO_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "schemeInfo")
	@IndexColumn(name = "SEQ")
    @Fetch(value = FetchMode.SUBSELECT)
	public List<SchemeTableColumn> getSchemeTableColumns() {
		return schemeTableColumns;
	}

	public void setSchemeTableColumns(List<SchemeTableColumn> schemeTableColumns) {
		this.schemeTableColumns = schemeTableColumns;
	}

	@Column(name = "POLL_SCHEME_TYPE", length = 20)
	public String getPollSchemeType() {
		return pollSchemeType;
	}

	public void setPollSchemeType(String pollSchemeType) {
		this.pollSchemeType = pollSchemeType;
	}

	@Column(name = "CLIENT_TYPE", length = 20)
	@Enumerated(EnumType.STRING)
	public ClientType getClientType() {
		return clientType;
	}

	public void setClientType(ClientType clientType) {
		this.clientType = clientType;
	}
	@Column(name = "SOURCE", length = 50)
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	@Column(name = "DESTINATION", length = 50)
	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	@Column(name = "DELIMITER", length = 10)
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	@Column(name = "IS_OVERRIDE")
	public boolean isOverride() {
		return isOverride;
	}

	public void setOverride(boolean isOverride) {
		this.isOverride = isOverride;
	}
	@Column(name = "IS_CONSISTENT_STRUCTURE")
	public boolean isConsistentStructure() {
		return consistentStructure;
	}

	public void setConsistentStructure(boolean consistentStructure) {
		this.consistentStructure = consistentStructure;
	}

	@Column(name = "DEST_CHECK_SUM_COLS", length = 200)
	public String getDestCheckSumCols() {
		return destCheckSumCols;
	}

	public void setDestCheckSumCols(String destCheckSumCols) {
		this.destCheckSumCols = destCheckSumCols;
	}

	@Column(name = "DEST_KEY_COLUMNS", length = 200)
	public String getDestKeyColumns() {
		return destKeyColumns;
	}

	public void setDestKeyColumns(String destKeyColumns) {
		this.destKeyColumns = destKeyColumns;
	}

	@Column(name = "SRC_CHECK_SUM_COLS", length = 200)
	public String getSrcCheckSumCols() {
		return srcCheckSumCols;
	}

	public void setSrcCheckSumCols(String srcCheckSumCols) {
		this.srcCheckSumCols = srcCheckSumCols;
	}

	@Column(name = "SRC_KEY_COLUMNS", length = 200)
	public String getSrcKeyColumns() {
		return srcKeyColumns;
	}

	public void setSrcKeyColumns(String srcKeyColumns) {
		this.srcKeyColumns = srcKeyColumns;
	}
	
	@Column(name = "SPLIT_DATE_REQUIRED")
	public boolean isSplitDateRequired() {
		return splitDateRequired;
	}

	public void setSplitDateRequired(boolean splitDateRequired) {
		this.splitDateRequired = splitDateRequired;
	}
	
}
