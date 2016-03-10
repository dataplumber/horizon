package gov.nasa.podaac.distribute.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
 
@RunWith(Suite.class)
@Suite.SuiteClasses({
  EMSTest.class,
  DistributeRelease300Test.class
})
public class DistributeTestSuite {
    // the class remains completely empty, 
    // being used only as a holder for the above annotations
}
