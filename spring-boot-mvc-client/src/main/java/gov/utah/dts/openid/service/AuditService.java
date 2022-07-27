package gov.utah.dts.openid.service;

import java.util.Date;

/**
 * The Interface AuditService.
 */
public interface AuditService {

    /**
     * Creates the audit row.
     *
     * @param when the when
     * @param who the who
     * @param field the field
     * @param table the table
     * @param value the value
     * @param beforeValue the before value
     * @param transaction the transaction
     * @param action the action
     * @param note the note
     * @param recordId the record id
     * @return the string
     */
	public String createAuditRow(Date when, AppUserDetails who, String field, String table, String value, String beforeValue, String transaction, String action, String note, String recordId);

	/**
	 * Gets the transaction.
	 *
	 * @return the transaction
	 */
	public String getTransaction();

}