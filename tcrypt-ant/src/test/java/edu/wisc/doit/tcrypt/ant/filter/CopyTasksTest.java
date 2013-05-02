package edu.wisc.doit.tcrypt.ant.filter;

import java.io.File;
import java.net.URL;

import org.apache.tools.ant.BuildFileTest;

/**
 * See http://ant.apache.org/manual/tutorial-writing-tasks.html#TestingTasks
 */
public class CopyTasksTest extends BuildFileTest {
    public CopyTasksTest(String name) {
        super(name);
    }
    
    public void setUp() throws Exception {
        // initialize Ant
        final URL buildResource = this.getClass().getResource("/build.xml");
        final File buildFile = new File(buildResource.toURI());
        configureProject(buildFile.getAbsolutePath());
    }
    public void testNothing() {
        
    }

//    public void testWithout() {
//        executeTarget("use.without");
//        assertEquals("Message was logged but should not.", getLog(), "");
//    }
//
//    public void testMessage() {
//        // execute target 'use.nestedText' and expect a message
//        // 'attribute-text' in the log
//        expectLog("use.message", "attribute-text");
//    }
//
//    public void testFail() {
//        // execute target 'use.fail' and expect a BuildException
//        // with text 'Fail requested.'
//        expectBuildException("use.fail", "Fail requested.");
//    }
//
//    public void testNestedText() {
//        expectLog("use.nestedText", "nested-text");
//    }
//
//    public void testNestedElement() {
//        executeTarget("use.nestedElement");
//        assertLogContaining("Nested Element 1");
//        assertLogContaining("Nested Element 2");
//    }
}
