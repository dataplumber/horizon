// Copyright 2008, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
//
// $Id: CleanupTest.java 1955 2008-09-20 21:41:06Z shardman $

package gov.nasa.podaac.distribute.subscriber.test;

import static org.junit.Assert.assertNull;
import gov.nasa.horizon.distribute.subscriber.api.Subscriber;
import gov.nasa.horizon.distribute.subscriber.model.Dataset;
import gov.nasa.horizon.distribute.subscriber.plugins.BasicSubscriber;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class FirstTest {

        @Test
        public void testCleanupDataset() {
        
		System.out.println("Running Test Class");

	}
        
        @Test
        public void testSubscriber() {
        System.out.println("Testing Basic Subscriber");
        
        Subscriber sub = new BasicSubscriber();
        sub.list(new Dataset("EUR-L2P-NAR16_SST"), new Date());
		

	}
}
