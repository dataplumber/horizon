package gov.nasa.podaac.distribute.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import gov.nasa.podaac.distribute.echo.*;
import gov.nasa.podaac.distribute.echo.jaxb.granule.Spatial;
import gov.nasa.podaac.inventory.model.DatasetElement;
import gov.nasa.podaac.inventory.model.ElementDD;
import gov.nasa.podaac.inventory.model.Granule;
import gov.nasa.podaac.inventory.model.GranuleReal;
import gov.nasa.podaac.inventory.model.GranuleSpatial;

public class DistributeRelease300Test {

	@Test
	public void testGranuleSpatial(){
		
		ECHOGranuleFile egf = new ECHOGranuleFile();
		Granule g = new Granule();
		
		int[] bbIds = {28,15,8,23};
		Double val = 44.00;
		
		for(int i: bbIds){
			GranuleReal gr = new GranuleReal();
			DatasetElement de = new DatasetElement();
			ElementDD ed = new ElementDD();
			ed.setElementId(i);
			de.setDeId(i);
			de.setElementDD(ed);
			gr.setDatasetElement(de);
			gr.setValue(val);
			g.add(gr);
			val +=5;
		}
		
		Spatial spat = egf.exportSpatial(g, true);
		System.out.println(spat.toString());

		DatasetElement de = new DatasetElement();
		ElementDD ed = new ElementDD();
		ed.setElementId(24);
		de.setDeId(24);
		de.setElementDD(ed);
		
		System.out.println("Processing Spatial");
		WKTReader rdr = new WKTReader();
	    Geometry geometry = null;
	    try {
			geometry = rdr.read("POLYGON ((189 98, 83 187, 185 221, 325 168, 189 98))");
			geometry.setSRID(8307);
			GranuleSpatial gs = new GranuleSpatial(de, (Polygon)geometry);
			System.out.println("DE: "+gs.toString());
			g.add(gs);
	    } catch (com.vividsolutions.jts.io.ParseException e) {
			e.printStackTrace();
			assertEquals(true,false);
		}

	    spat = egf.exportSpatial(g, false);
		System.out.println(spat.toString());
		
		g.getGranuleRealSet().clear();
		spat = egf.exportSpatial(g, true);
		System.out.println(spat.toString());
	    
	}
}
