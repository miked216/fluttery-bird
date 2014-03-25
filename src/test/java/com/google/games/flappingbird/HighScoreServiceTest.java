package com.google.games.flutterybird;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import com.google.games.flutterybird.HighScoreServlet;
import com.google.games.flutterybird.HighScoreSnapshot;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig(),
          new LocalUserServiceTestConfig())
        .setEnvIsLoggedIn(true)
        .setEnvIsAdmin(true)
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
  public void testNUnitSetUp() {
    assertThat(0, is(0));
  }

  @Test
  public void testGetHighScoreSnapshot_NoData() throws IOException {
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    HighScoreService hss = new HighScoreService();

    HighScoreSnapshot snapshot = hss.getHighScoreSnapshot(ds);
    // Test there is no data, and thus none is returned.
    assertThat(snapshot.getAllTime().size(), is(0));
    assertThat(snapshot.getPastHour().size(), is(0));
  }

  @Test
  public void testGetHighScoreSnapshot_SomeData() throws IOException {
    HighScore highScore = new HighScore(100, new Date(), "Steve");
    HighScoreService hss = new HighScoreService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    hss.addNewScore(datastore, highScore);

    // Assert the high score is returned.
    HighScoreSnapshot snapshot = hss.getHighScoreSnapshot(datastore);
    assertThat(snapshot.getAllTime().size(), is(1));
    assertThat(snapshot.getPastHour().size(), is(1));
  }

  @Test
  public void testGetHighScoreSnapshot_NonAdmin() throws IOException {
    HighScore highScore = new HighScore(100, new Date(), "Duke");
    HighScoreService hss = new HighScoreService();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    // Make the faux user a non-admin and verify the user service throws.
    helper.setEnvIsAdmin(false);
    
    try {
      hss.addNewScore(datastore, highScore);
      fail("High score service did not throw an exception!");
    } catch (RuntimeException ex) {
      // Expected. Super glad I fixed that bug! Otherwise Chris' demo
      // would have been ruined.
    }
  }
}
