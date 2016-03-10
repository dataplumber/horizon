package gov.nasa.horizon.common.api.util;

import java.io.File;

public class StringUtility {
	
	public static String cleanPaths(String root, String rel, String name){
		//ideal:
		//Root + File.separator + Rel + File.separator + Name
		String complete = null;
		if(root != null)
		{
			complete =  root;
		}
		if(rel != null)
		{
			if(complete != null)
				complete = complete + File.separator + rel;
			else
				complete= rel;
		}
		if(name != null){
			if(complete != null)
				complete = complete + File.separator + name;
			else
				complete= name;
		}
		return complete;
	}
	
}
