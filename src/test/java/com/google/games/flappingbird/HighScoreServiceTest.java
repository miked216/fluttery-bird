package com.google.games.flappingbird;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import com.google.games.flappingbird.HighScoreServlet;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

public class HighScoreServiceTest {

  private static final LocalServiceTestHelper helper = new LocalServiceTestHelper(
      new LocalDatastoreServiceTestConfig().setDefaultHighRepJobPolicyUnappliedJobPercentage(100),
      new LocalUserServiceTestConfig())
      .setEnvIsLoggedIn(true)
      .setEnvAuthDomain("example.com")
      .setEnvEmail("test@example.com");

  private final Logger logger = Logger.getLogger(HighScoreServiceTest.class.getName());

  @Before
  public void setUp() throws ServletException {
    helper.setUp();
  }

  @After
  public void tearDownDatastoreHelper() {
    helper.tearDown();
  }

  @Test
  public void testNUnitSetUp() throws ServletException, IOException {
    assertThat(0, is(0));
  }
}
