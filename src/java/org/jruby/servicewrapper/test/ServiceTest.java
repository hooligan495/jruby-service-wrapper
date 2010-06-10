package org.jruby.servicewrapper.test;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.jruby.servicewrapper.ServiceLauncher;

public class ServiceTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testStartFailsWithoutRequiredFields() {
		String[] args= new String[4];
		args[0] = "-fn";
		args[1] = "ruby_service";
		args[2] = "-cn";
		args[3] = "RubyService";
		ServiceLauncher l = new ServiceLauncher();
		assertEquals(new Integer(-1), l.start(args));
	}
	@Test
	public void testNameIsSetAfterStarting() {
		String name = "RubyService";
		ServiceLauncher l = new ServiceLauncher();
		l.initialize("RubyService","bin/ruby/ruby_service",name);
		assertEquals(name, l.getServiceName());
	}
	
	@Test
	public void testStartFakeService() {
		String[] args= new String[4];
		args[0] = "-fn";
		args[1] = "ruby_service";
		args[2] = "-cn";
		args[3] = "RubyService";
		args[4] = "-cn";
		args[5] = "Test service";
		ServiceLauncher l = new ServiceLauncher();
		assertNull(l.start(args));
	}

	
	@Test
	public void testStop() {
		fail("Not yet implemented");
	}

	@Test
	public void testControlEvent() {
		fail("Not yet implemented");
	}

}
