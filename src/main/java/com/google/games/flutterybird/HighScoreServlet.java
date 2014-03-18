package com.google.games.flutterybird;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.games.flutterybird.HighScore;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides an API endpoint for viewing and saving high scores.
*
*   GET /highscore
*   POST /highscore?score=[integer]
*
* @author joannasmith@google.com (Joanna Smith)
*/
public class HighScoreServlet extends HttpServlet {
  private final Logger logger = Logger.getLogger(HighScoreServlet.class.getName());
  private Gson gson = new Gson();

  /**
   * Exposed as `GET /highscore`.
   *     Returns the list of all time high scores and the list of high scores from the past
   *     hour.
   *
   * @throws IOException if the response fails to fetch its writer
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    HighScoreService hsService = new HighScoreService();

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    HighScoreSnapshot currentHighScores = hsService.getHighScoreSnapshot(datastoreService);

    // TODO(joannasmith): Consider extracting these into a ScoreResponse object for easier JSONing
    resp.setContentType("text/json");
    resp.getWriter().print("({");
    resp.getWriter().print("\'all\': " + gson.toJson(currentHighScores.getAllTime()));
    resp.getWriter().println(",");
    resp.getWriter().print("\'hour\': " + gson.toJson(currentHighScores.getPastHour()));
    resp.getWriter().print("})");
  }

  /**
   * Exposed as `POST /highscore?score=[integer]`.
   *     Saves the provided score, along with a timestamp and the name of the authenticated user
   *     in the AppEngine User Service. If there is no authenticated user, the user's name is
   *     saved as "Anonymous" instead.
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    // If the provided score parameter isn't an integer, ignore.
    int gameScore = 0;
    try {
      gameScore = Integer.valueOf(req.getParameter("score"));
    } catch (NumberFormatException e) {
      return;
    }

    HighScoreService hsService = new HighScoreService();

    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();
    hsService.addNewScore(datastoreService, userService, gameScore);
  }
}
