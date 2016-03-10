package gov.nasa.horizon.distribute.subscriber.plugins;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.horizon.distribute.subscriber.api.Subscriber;
import gov.nasa.horizon.inventory.model.*;
import gov.nasa.horizon.inventory.api.InventoryApi;

//import gov.nasa.horizon.inventory.api.Query;
//import gov.nasa.horizon.inventory.api.QueryFactory;
import gov.nasa.horizon.inventory.model.*;

public class IWSSubscriber implements Subscriber {

	static Log _logger = LogFactory.getLog(IWSSubscriber.class);
	public boolean list(ProductType pt, Date lastRunTime) {
		return true;
		/*
		String inventoryURL = System.getProperty("inventory.url");
		
		// TODO Auto-generated method stub
		_logger.info("BasicSubscriber.list("+pt.getIdentifier()+")");
		
		InventoryApi q = new InventoryApi(inventoryURL);
		
		String[] props = {"shortName"};
		boolean addedProduct = false;
		
		//get DS information
		
		_logger.info("Fetching ProductType information");
		
		
		gov.nasa.horizon.inventory.model.ProductType pt = null;
		try {
			d = q.getProductType(dataset.getShortName());
		} catch (InventoryException e1) {
			_logger.error("Error connecting to inventory Service. Could not retrieve dataset");
		}
		if(d!=null)
		{
		    
			dataset.setId(d.getProductTypeId());
			dataset.setLongName(d.getLongName());
			dataset.setDescription(d.getDescription());
			dataset.setProcessingLevel(d.getProcessingLevel());
		
		}
		
		_logger.info("Fetching granules for dataset ["+dataset.getId()+"]:" + dataset.getShortName());
		
		//get granules
		//String[] args = {"DATASET_ID","ARCHIVE_TIME"};
		String[] args = {"dataset"};
		//listProductId(String...whereClause)
		//for(gov.nasa.horizon.inventory.model.Product g:q.listProductByProperty("dataset",d))
		//List<BigDecimal> li = q.listLatestProductIdsByProductTypeID(dataset.getId());
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(lastRunTime.getTime());
		
		List<Integer> li = null;
		try {
			li = q.getProductIdListAll( dataset.getId(), c, null, null);
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			_logger.error("Error connecting to inventory Service. Could not retrieve list of granules.");
		}
		
		_logger.info("running with last run time: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(lastRunTime));
		
		for(Integer bd: li)
		{
			//System.out.println(g.getArchiveTime().toString());
			//if(g.getArchiveTime().after(lastRunTime))
			//{
			Integer i = bd;
				gov.nasa.horizon.inventory.model.Product g = null;
				try {
					g = q.getProductById(i);
				} catch (InventoryException e) {
					// TODO Auto-generated catch block
					_logger.error("Error connecting to inventory Service. Could not get granule.");
				} //make a deep copy (get archive and reference)
				
				if(g.getStatus().equals("OFFLINE"))
				{
					_logger.debug("Product id["+g.getProductId()+"] status is offline. skipping granule.");
					continue;
				}
//				if(g.getArchiveTime().after(lastRunTime))
//					{
				_logger.info("adding granule with name: " + g.getName());
				Product add = new Product(g.getAccessType(),new Date(g.getCreateTimeLong()), g.getProductId(), g.getName(),new Date(g.getStartTimeLong()),g.getStatus().toString(),new Date(g.getStopTimeLong()));
				
				for(ProductArchive ga : g.getProductArchiveSet())
				{
					ProductFile gf = new ProductFile();
					
					gf.setName(ga.getName());
					if(g.getRelPath() != null){
						gf.setPath(g.getRootPath() + File.separator + g.getRelPath());
					}
					else{
						gf.setPath(g.getRootPath());
					}
					
					gf.setStatus(ga.getStatus());
					gf.setType(ga.getType());
					add.addFile(gf);
				}
				
				for(gov.nasa.horizon.inventory.model.ProductReference gr : g.getProductReferenceSet())
				{
					
					if(gr.getStatus().equals("IN-PROCESS"))
					{
						//if the granule is online and the refrence is 'in-process' then we really have a problem... shouldn't ever see this one.
						_logger.debug("Reference is 'in-process'. Skipping.");
						_logger.error("Error- granule ["+g.getProductId()+"] is online but references are listed as 'IN-PROCESS'.");
						continue;
					}
					ProductReference gra = new ProductReference();
					gra.setPath(gr.getPath());
					gra.setStatus(gr.getStatus());
					gra.setType(gr.getType());
					add.addReference(gra);
				}
				dataset.addProduct(add);
				addedProduct = true;
				
//			}
		}
		return true;*/
	}

}
