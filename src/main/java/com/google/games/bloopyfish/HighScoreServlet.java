package com.google.games.bloopyfish;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.games.bloopyfish.HighScore;
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
   *     24 hours. Response is a JSON object structured as:
   *         ["all":[{"score":0,"date":"Date toString","player":"nickname"}],
   *          "today":[]]
   *
   * @throws IOException if the response fails to fetch its writer
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {


    List<HighScore> allTimeScores = new ArrayList<HighScore>();
    List<HighScore> past24HoursScores = new ArrayList<HighScore>();

    // Query for the list of the top 10 all-time scores
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    Query allTimeQuery =
        new Query("HighScore").addSort("score", Query.SortDirection.DESCENDING);
    List<Entity> scoreEntities = datastoreService.prepare(allTimeQuery)
        .asList(FetchOptions.Builder.withLimit(10));
    for (Entity score : scoreEntities) {
      allTimeScores.add(HighScore.fromEntity(score));
    }
    logger.log(Level.FINE, "All-time top 10 scores in datastore: " + allTimeScores);

    // Query for the list of the top 10 scores today
    // TODO(joannasmith): Sorting of this list by score might be off due to filtering by date.
    Date now = new Date();
    Date past24Hours = new Date(now.getTime() - (1000*60*60*24));
    Query.Filter filter =
        new Query.FilterPredicate("date", Query.FilterOperator.GREATER_THAN_OR_EQUAL, past24Hours);
    Query past24Query = new Query("HighScore")
        .setFilter(filter)
        .addSort("date", Query.SortDirection.DESCENDING)
        .addSort("score", Query.SortDirection.DESCENDING);
    scoreEntities = datastoreService.prepare(past24Query)
        .asList(FetchOptions.Builder.withLimit(10));
    for (Entity score : scoreEntities) {
      past24HoursScores.add(HighScore.fromEntity(score));
    }
    logger.log(Level.FINE, "Today's top 10 scores in datastore: " + past24HoursScores);

    // TODO(joannasmith): Consider extracting these into a ScoreResponse object for easier JSONing
    resp.setContentType("text/json");
    resp.getWriter().print("[");
    resp.getWriter().print("\"all\":" + gson.toJson(allTimeScores));
    resp.getWriter().println(",");
    resp.getWriter().print("\"today\":" + gson.toJson(past24HoursScores));
    resp.getWriter().print("]");
  }

  /**
   * Exposed as `POST /highscore?score=[integer]`.
   *     Saves the provided score, along with a timestamp and the name of the authenticated user
   *     in the AppEngine User Service. If there is no authenticated user, the user's name is
   *     saved as "Anonymous" instead.
   */
  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp) {
    DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    // If the user has signed in, we use their registered nickname, otherwise we default to Anon.
    String name = "Anonymous";
    if (user != null) {
      name = user.getNickname();
    }
    logger.log(Level.FINE, "User: " + name);

    // If the provided score parameter isn't an integer, we simply don't write the score.
    long gameScore = 0;
    try {
      gameScore = Long.valueOf(req.getParameter("score"));
    } catch (NumberFormatException e) {
      // The provided score isn't an integer, so this is a graceful fail
      return;
    }
    logger.log(Level.FINE, "Score: " + gameScore);

    // Create an entity to store the score data in the AppEngine Datastore.
    Entity score = new Entity("HighScore");
    score.setProperty("player", name);
    score.setProperty("score", gameScore);
    score.setProperty("date", new Date());
    logger.log(Level.FINE, "HighScore Entity: " + score);

    datastoreService.put(score);
  }
}
