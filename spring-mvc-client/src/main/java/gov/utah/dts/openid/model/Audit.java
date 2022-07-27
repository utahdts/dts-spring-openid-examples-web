package gov.utah.dts.openid.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
// TODO: Auto-generated Javadoc
/*
 *      id bigint identity not null,
        Transation varchar(255) not null,
        WhoId bigint not null,
        Who varchar(255) not null,
        When datetime2 not null,
        TableName varchar(255) null,
        FieldName varchar(255) null,
        Before varchar(255) null,
        Value varchar(255) null,
        Note varchar(255) null,
        primary key (id)
 */
/**
 * The Class Audit.
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "AUDIT")
public class Audit implements Serializable{
    
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 111133L;
	
	/** The Constant ACTION_UPDATE. */
	public static final String ACTION_UPDATE = "update";
	
	/** The Constant ACTION_ADD. */
	public static final String ACTION_ADD = "add";
	
	/** The Constant ACTION_DELETE. */
	public static final String ACTION_DELETE = "delete";
	
	/** The Constant ACTION_LOGIN. */
	public static final String ACTION_LOGIN = "login";
	
	/** The Constant ACTION_LOGOUT. */
	public static final String ACTION_LOGOUT = "logout";
	
	/** The id. */
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/** The audit transaction. */
	@Column(name="AuditTransaction")
	private String auditTransaction;
	
	/** The who. */
	@Column(name="Who")
    private String who;

	/** The who id. */
	@Column(name="WhoId")
    private long whoId;
	
	/** The audit when. */
	@Column(name="auditWhen")
    private Timestamp auditWhen;
	
	/** The table name. */
	@Column(name="TableName")
    private String tableName;
	
	/** The record id. */
	@Column(name="RecordId")
    private String recordId;
	
	/** The field name. */
	@Column(name="fieldName")
    private String fieldName;
	
	/** The value before. */
	@Column(name="valueBefore")
    private String valueBefore;
	
	/** The value. */
	@Column(name="value")
    private String value;
	
	/** The action. */
	@Column(name="action")
    private String action;
	
	/** The note. */
	@Column(name="note")
    private String note;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * Gets the who.
	 *
	 * @return the who
	 */
	public String getWho() {
		return who;
	}

	/**
	 * Sets the who.
	 *
	 * @param who the new who
	 */
	public void setWho(String who) {
		this.who = who;
	}

	/**
	 * Gets the who id.
	 *
	 * @return the who id
	 */
	public long getWhoId() {
		return whoId;
	}

	/**
	 * Sets the who id.
	 *
	 * @param whoId the new who id
	 */
	public void setWhoId(long whoId) {
		this.whoId = whoId;
	}

	/**
	 * Gets the table name.
	 *
	 * @return the table name
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * Sets the table name.
	 *
	 * @param tableName the new table name
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * Gets the field name.
	 *
	 * @return the field name
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Sets the field name.
	 *
	 * @param fieldName the new field name
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}


	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the action.
	 *
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 *
	 * @param action the new action
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * Gets the note.
	 *
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * Sets the note.
	 *
	 * @param note the new note
	 */
	public void setNote(String note) {
		this.note = note;
	}

	/**
	 * Gets the audit when.
	 *
	 * @return the audit when
	 */
	public Timestamp getAuditWhen() {
		return auditWhen;
	}

	/**
	 * Sets the audit when.
	 *
	 * @param auditWhen the new audit when
	 */
	public void setAuditWhen(Timestamp auditWhen) {
		this.auditWhen = auditWhen;
	}

	/**
	 * Gets the audit transaction.
	 *
	 * @return the audit transaction
	 */
	public String getAuditTransaction() {
		return auditTransaction;
	}

	/**
	 * Sets the audit transaction.
	 *
	 * @param auditTransaction the new audit transaction
	 */
	public void setAuditTransaction(String auditTransaction) {
		this.auditTransaction = auditTransaction;
	}

	/**
	 * Gets the value before.
	 *
	 * @return the value before
	 */
	public String getValueBefore() {
		return valueBefore;
	}

	/**
	 * Sets the value before.
	 *
	 * @param valueBefore the new value before
	 */
	public void setValueBefore(String valueBefore) {
		this.valueBefore = valueBefore;
	}

	/**
	 * Gets the record id.
	 *
	 * @return the record id
	 */
	public String getRecordId() {
		return recordId;
	}

	/**
	 * Sets the record id.
	 *
	 * @param recordId the new record id
	 */
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	

	

}