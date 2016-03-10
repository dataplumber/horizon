/*****************************************************************************
 * Copyright (c) 2007 Jet Propulsion Laboratory,
 * California Institute of Technology.  All rights reserved
 *****************************************************************************/

package gov.nasa.horizon.common.api.util;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

/**
 * @author Atsuya Takagi {Atsuya.Takagi@jpl.nasa.gov}
 * @version $Id: TransformerUtility.java 244 2007-10-02 20:12:47Z axt $
 * 
 */
public class TransformerUtility {
	protected TransformerUtility() {
	}

	public static Transformer getTransformer() {
		Transformer transformer = null;

		try {
			TransformerFactory transformerFactory =
					TransformerFactory.newInstance();
			//transformerFactory.setAttribute("indent-number", 0);

			transformer = transformerFactory.newTransformer();
		} catch (Exception exception) {
		   exception.printStackTrace();
		}

		return transformer;
	}
}
