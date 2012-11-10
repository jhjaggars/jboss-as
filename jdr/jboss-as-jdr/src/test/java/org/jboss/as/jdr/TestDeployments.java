package org.jboss.as.jdr;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

import org.jboss.as.jdr.commands.Deployments;
import org.jboss.as.jdr.commands.JdrEnvironment;

public class TestDeployments {

    JdrEnvironment env;
    Deployments dep;

    @Before
    public void setUp() throws Exception {
        env = new JdrEnvironment();
        dep = new Deployments();
        dep.setEnvironment(env);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDeployments() throws Exception {
        dep.execute();
    }
}
