package gov.nasa.horizon.distribute.subscriber.api;

import java.io.IOException;
import java.util.List;

import gov.nasa.horizon.inventory.model.*;

public interface DataRetriever {

	/*
	 * @method get - takes a granule object and retrieves the files for each placing them in the data/dataset/granule/directory
	 */
	public List<String> get(Product product, String outputDir) throws IOException;
	
}
