package gov.utah.dts.openid.model;

import lombok.Data;
import java.io.Serializable;

@Data
public final class DatabaseUser implements Serializable {

	private String utahId;
	private Integer userID;
	private String role;
	private String firstName;
	private String lastName;
	private String email;
	private boolean inactive;

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

}
