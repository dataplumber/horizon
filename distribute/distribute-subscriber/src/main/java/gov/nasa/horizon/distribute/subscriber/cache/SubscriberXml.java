package gov.nasa.horizon.distribute.subscriber.cache;

import gov.nasa.horizon.distribute.subscriber.cache.SubscriberCache.*;
import gov.nasa.horizon.distribute.subscriber.cache.SubscriberCache.Product.*;
//import gov.nasa.horizon.distribute.subscriber.cache.SubscriberCache.Product.Files.*;
//import gov.nasa.horizon.distribute.subscriber.cache.SubscriberCache.Product.References.*;

//import gov.nasa.horizon.inventory.model.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.*;

public class SubscriberXml {

	//JAXBContext jaxbContext=JAXBContext.newInstance("generated");
	private static JAXBContext jaxbContext = null;
	
	public SubscriberXml()
	{
		//do nothing;
	}
	/*
	public static void toXml(HashSet<gov.nasa.horizon.inventory.model.Product> granuleList ,String xmlDoc)
	{
		try {
			 jaxbContext=JAXBContext.newInstance("gov.nasa.podaac.distribute.subscriber.cache");
			 Marshaller marshaller=jaxbContext.createMarshaller();
			 marshaller.setProperty(marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
			 ObjectFactory factory=new ObjectFactory(); 
			 SubscriberCache cache=(SubscriberCache)(factory.createSubscriberCache());
		
			 for(gov.nasa.horizon.inventory.model.Product g: granuleList)
			 {
				 Product granule = (Product)(factory.createSubscriberCacheProduct());
				 ProductInfo gInfo = (ProductInfo)(factory.createSubscriberCacheProductProductInfo());
				 Files gFiles = (Files)(factory.createSubscriberCacheProductFiles());
				 References gRefs = (References)(factory.createSubscriberCacheProductReferences());
				 
				 //set granule Info
				 gInfo.setId(g.getId());
				 gInfo.setName(g.getName());
				 gInfo.setStatus(g.getStatus());
				 
				 //set granule Refs
				 for(gov.nasa.horizon.distribute.subscriber.model.ProductReference ref : g.getReferenceList())
				 {
					Reference gref = (Reference) (factory.createSubscriberCacheProductReferencesReference());
					gref.setPath(ref.getPath());
					gref.setStatus(ref.getStatus());
					gref.setType(ref.getType());
					
					gRefs.getReference().add(gref); 
				 }
				 granule.setReferences(gRefs);
				 //granuleFiles
				 for(gov.nasa.horizon.distribute.subscriber.model.ProductFile file : g.getFileList())
				 {
					 File gfile = (File) (factory.createSubscriberCacheProductFilesFile());
					 
					 gfile.setName(file.getName());
					 gfile.setPath(file.getPath());
					 gfile.setStatus(file.getStatus());
					 gfile.setType(file.getType());
					 
					 gFiles.getFile().add(gfile);
				 }
				  granule.setFiles(gFiles);
				 granule.setProductInfo(gInfo);
				 cache.getProduct().add(granule);
			 }
 
			 marshaller.marshal(cache, new FileOutputStream(xmlDoc));
		
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		
	}
	
	public static HashSet<gov.nasa.horizon.distribute.subscriber.model.Product> fromXml(String fname)
	{
		JAXBContext jc = null;
		Unmarshaller u = null;
		SubscriberCache cache = null;
		try {
			jc = JAXBContext.newInstance("gov.nasa.podaac.distribute.subscriber.cache");
			u = jc.createUnmarshaller();
			URL url = new URL("file://" + fname );
		    // regions = (Regions)u.unmarshal(new FileInputStream("RegionMaster.xml"));
			cache = (SubscriberCache)u.unmarshal(url);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		HashSet<gov.nasa.horizon.distribute.subscriber.model.Product> granuleList = new HashSet<gov.nasa.horizon.distribute.subscriber.model.Product>();
		
		for(Product granule:cache.getProduct())
		{
			gov.nasa.horizon.distribute.subscriber.model.Product g = new gov.nasa.horizon.distribute.subscriber.model.Product();
			
			g.setId(granule.getProductInfo().getId());
			g.setName(granule.getProductInfo().getName());
			g.setStatus(granule.getProductInfo().getStatus());
			
			for(File f :granule.getFiles().getFile())
			{
				gov.nasa.horizon.distribute.subscriber.model.ProductFile gf = new gov.nasa.horizon.distribute.subscriber.model.ProductFile();
				gf.setName(f.getName());
				gf.setPath(f.getPath());
				gf.setStatus(f.getStatus());
				gf.setType(f.getType());
				
				g.getFileList().add(gf);
			}
			
			for(Reference r :granule.getReferences().getReference())
			{
				gov.nasa.horizon.distribute.subscriber.model.ProductReference gr = new gov.nasa.horizon.distribute.subscriber.model.ProductReference();
				
				gr.setPath(r.getPath());
				gr.setStatus(r.getStatus());
				gr.setType(r.getType());
			
				g.getReferenceList().add(gr);
			
			}
			granuleList.add(g);
		}
		
		return granuleList;

	}
	*/
	
}
