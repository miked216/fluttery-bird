package com.google.games.flappingbird;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.games.flappingbird.HighScore;
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
 * Implementation of the HighScore service.
 *
 * @author joannasmith@google.com (Joanna Smith)
 */
public class HighScoreService {
  private final Logger logger = Logger.getLogger(HighScoreService.class.getName());

  public HighScoreSnapshot getHighScoreSnapshot(DatastoreService datastoreService) throws IOException {
    List<HighScore> allTimeScores = new ArrayList<HighScore>();
    List<HighScore> pastHourScores = new ArrayList<HighScore>();

    // Query for the list of the top 10 all-time scores
    Query allTimeQuery = new Query("HighScore").addSort("score", Query.SortDirection.DESCENDING);
    List<Entity> scoreEntities = datastoreService
        .prepare(allTimeQuery)
        .asList(FetchOptions.Builder.withLimit(10));
    for (Entity score : scoreEntities) {
      allTimeScores.add(HighScore.fromEntity(score));
    }
    logger.log(Level.FINE, "All-time top 10 scores in datastore: " + allTimeScores);

    // Query for the list of the top 10 scores today
    // TODO(joannasmith): Sorting of this list by score might be off due to filtering by date.
    Date now = new Date();
    Date pastHour = new Date(now.getTime() - (1000 * 60 * 60));
    Query.Filter filter =
        new Query.FilterPredicate("date", Query.FilterOperator.GREATER_THAN_OR_EQUAL, pastHour);
    Query pastHourQuery = new Query("HighScore")
        .setFilter(filter)
        .addSort("date", Query.SortDirection.DESCENDING)
        .addSort("score", Query.SortDirection.DESCENDING);
    scoreEntities = datastoreService.prepare(pastHourQuery)
        .asList(FetchOptions.Builder.withLimit(10));
    for (Entity score : scoreEntities) {
      pastHourScores.add(HighScore.fromEntity(score));
    }
    logger.log(Level.FINE, "Last hour's top scores in datastore: " + pastHourScores);

    return new HighScoreSnapshot(pastHourScores, allTimeScores);
  }

  public void addNewScore(DatastoreService datastoreService, UserService userService, int gameScore) {
    User user = userService.getCurrentUser();
    // If the user has signed in, we use their registered nickname, otherwise we default to Anon.
    String name = (user != null) ? user.getNickname() : "Anonymous";
    logger.log(Level.FINE, "User: " + name);

    // Create an entity to store the score data in the AppEngine Datastore.
    Entity score = new Entity("HighScore");
    score.setProperty("player", name);
    score.setProperty("score", gameScore);
    score.setProperty("date", new Date());
    logger.log(Level.FINE, "HighScore Entity: " + score);

    datastoreService.put(score);
  }
}
