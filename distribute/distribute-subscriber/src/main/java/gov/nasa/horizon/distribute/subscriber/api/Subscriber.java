package gov.nasa.horizon.distribute.subscriber.api;

import gov.nasa.horizon.inventory.model.*;

import java.util.Date;

public interface Subscriber {

	/*
	 * @method list - Interface which will query for granules add to a dataset since lastRunTime
	 * @param dataset - the dataset object to which we will add granule name/information 
	 * @param lastRunTime -the start time of the subscribers last run/query
	 */
	public boolean list(ProductType dataset, Date lastRunTime) ;
	
	
}
