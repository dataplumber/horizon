package gov.nasa.horizon.distribute.subscriber.plugins;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.nasa.horizon.distribute.subscriber.api.Subscriber;
import gov.nasa.horizon.inventory.model.ProductType;
import gov.nasa.horizon.inventory.model.ProductReference;
import gov.nasa.horizon.inventory.model.ProductArchive;
import gov.nasa.horizon.inventory.api.*;

public class BasicSubscriber implements Subscriber {

	static Log _logger = LogFactory.getLog(BasicSubscriber.class);
	public boolean list(ProductType dataset, Date lastRunTime) {
		return true;
		/*
		// TODO Auto-generated method stub
		_logger.info("BasicSubscriber.list("+dataset.getShortName()+")");
		
		Query q = QueryFactory.getInstance().createQuery();
		
		String[] props = {"shortName"};
		boolean addedGranule = false;
		
		//get DS information
		
		_logger.info("Fetching Dataset information");
		
		
		gov.nasa.podaac.inventory.model.Dataset d = q.findDatasetByShortName(dataset.getShortName());
		if(d!=null)
		{
		    
			dataset.setId(d.getDatasetId());
			dataset.setLongName(d.getLongName());
			dataset.setDescription(d.getDescription());
			dataset.setProcessingLevel(d.getProcessingLevel());
		
		}
		
		_logger.info("Fetching granules for dataset ["+dataset.getId()+"]:" + dataset.getShortName());
		
		//get granules
		//String[] args = {"DATASET_ID","ARCHIVE_TIME"};
		String[] args = {"dataset"};
		//listGranuleId(String...whereClause)
		//for(gov.nasa.podaac.inventory.model.Granule g:q.listGranuleByProperty("dataset",d))
		List<BigDecimal> li = q.listLatestGranuleIdsByDatasetID(dataset.getId());
		
		_logger.info("running with last run time: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS").format(lastRunTime));
		
		for(BigDecimal bd: li)
		{
			//System.out.println(g.getArchiveTime().toString());
			//if(g.getArchiveTime().after(lastRunTime))
			//{
			Integer i = new Integer(bd.intValue());
				gov.nasa.podaac.inventory.model.Granule g =q.findGranuleByIdDeep(i); //make a deep copy (get archive and reference)
				
				if(g.getStatus().equals("OFFLINE"))
				{
					_logger.debug("Granule id["+g.getGranuleId()+"] status is offline. skipping granule.");
					continue;
				}
				if(g.getArchiveTime().after(lastRunTime))
					{
				_logger.info("adding granule with name: " + g.getName());
				Granule add = new Granule(g.getAccessType(),g.getCreateTime(), g.getGranuleId(), g.getName(),g.getStartTime(),g.getStatus().toString(),g.getStopTime());
				
				for(GranuleArchive ga : g.getGranuleArchiveSet())
				{
					GranuleFile gf = new GranuleFile();
					
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
				for(gov.nasa.podaac.inventory.model.GranuleReference gr : g.getGranuleReferenceSet())
				{
					if(gr.getStatus().equals("IN-PROCESS"))
					{
						//if the granule is online and the refrence is 'in-process' then we really have a problem... shouldn't ever see this one.
						_logger.debug("Reference is 'in-process'. Skipping.");
						_logger.error("Error- granule ["+g.getGranuleId()+"] is online but references are listed as 'IN-PROCESS'.");
						continue;
					}
					GranuleReference gra = new GranuleReference();
					gra.setPath(gr.getPath());
					gra.setStatus(gr.getStatus());
					gra.setType(gr.getType());
					add.addReference(gra);
				}
				dataset.addGranule(add);
				addedGranule = true;
				
			}
				else
				{
					break;
				}
		}
		return true;
		*/
	}

}
