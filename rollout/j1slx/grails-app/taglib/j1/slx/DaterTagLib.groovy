package j1.slx

import java.text.SimpleDateFormat;

class DaterTagLib {
	def formateDateObject = { attrs, body ->
		if(attrs.date == null){
			out << body() << "--"
		}
		else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				out << body() << sdf.format(new Date(attrs.date));
			
		}
		
	}
}
