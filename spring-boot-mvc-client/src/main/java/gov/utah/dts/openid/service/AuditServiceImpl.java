package gov.utah.dts.openid.service;

import gov.utah.dts.openid.Constants;
import gov.utah.dts.openid.model.Audit;
import gov.utah.dts.openid.repository.AuditRepository;
import gov.utah.dts.openid.repository.MemberRepository;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class AuditServiceImpl.
 */

@Service
public class AuditServiceImpl implements AuditService {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
	
	/** The member repo. */
	@Autowired
	private MemberRepository memberRepo;
	
	/** The audit repo. */
	@Autowired
	private AuditRepository auditRepo;

	/* (non-Javadoc)
	 * @see gov.utah.lfa.service.AuditService#createAuditRow(java.util.Date, gov.utah.lfa.service.AppUserDetails, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public String createAuditRow(Date when, AppUserDetails who, String field, String table, String value, String beforeValue, String transaction, String action, String note, String recordId){
		if (Constants.dbAudit==true){
			Timestamp whenT = new Timestamp(when.getTime());
			dbAuditLog(whenT,who,field,table,value,beforeValue,transaction,action,note,recordId);
		}
		String whenString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(when);
		String info = "Changed "+field+" to: "+value;
		if (Audit.ACTION_ADD.equals(action)){
			info = " Added Fee ";
		}
		return "<p>" + whenString + " " + who.getName() + " " + info + "</p>";
	}

	/* (non-Javadoc)
	 * @see gov.utah.lfa.service.AuditService#dbAuditLog(java.sql.Timestamp, gov.utah.lfa.service.AppUserDetails, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void dbAuditLog(Timestamp when, AppUserDetails user, String field, String table, String value, String beforeValue, String transaction, String action, String note, String recordId){
		Audit audit = new Audit();
		audit.setAction(limit255(action));
		audit.setAuditWhen(when);
		audit.setValueBefore(limit255(beforeValue));
		audit.setFieldName(limit255(field));
		audit.setNote(limit255(note));
		audit.setTableName(limit255(table));
		audit.setRecordId(limit255(recordId));
		audit.setAuditTransaction(limit255(transaction));
		audit.setValue(limit255(value));
		audit.setWho(limit255(user.getName()));
		audit.setWhoId(user.getId());
		auditRepo.save(audit);
	}

	/* (non-Javadoc)
	 * @see gov.utah.lfa.service.AuditService#getTransaction()
	 */
	public String getTransaction(){
		return java.util.UUID.randomUUID().toString();
	}

	//sorry just 255 char in audit...
	/**
	 * Limit255.
	 *
	 * @param original the original
	 * @return the string
	 */
	private String limit255(String original){
		if (original == null){
			return null;
		}
		if (original.length()<255){
			return original;
		}
		return original.substring(0,253);
	}

}
