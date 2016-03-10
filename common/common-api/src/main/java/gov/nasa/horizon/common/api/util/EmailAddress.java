/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: EmailAddress.java 244 2007-10-02 20:12:47Z axt $
 */
public class EmailAddress {
	private String _name;
	private String _emailAddress;

	public EmailAddress(String emailAddress) {
		this._emailAddress = emailAddress.toLowerCase();
	}

	public EmailAddress(String name, String emailAddress) {
		_name = name.toLowerCase();
		_emailAddress = emailAddress.toLowerCase();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof EmailAddress)
			return ((EmailAddress)o)._emailAddress.equalsIgnoreCase(this._emailAddress);
		return false;
	}
	
	@Override
	public int hashCode() {
		return (37 * 17 + this._emailAddress.hashCode());
	}

	public String toString() {
		if (this._name != null)
			return _name + " <" + _emailAddress + ">";
		return this._emailAddress;
	}
}
