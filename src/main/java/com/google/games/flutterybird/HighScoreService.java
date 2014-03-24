package com.google.games.flutterybird;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
 * Implementation of the HighScore service.
 *
 * @author joannasmith@google.com (Joanna Smith)
 */
public class HighScoreService {
  private final Logger logger = Logger.getLogger(HighScoreService.class.getName());

  public HighScoreSnapshot getHighScoreSnapshot(DatastoreService datastoreService) throws IOException {
    List<HighScore> allTimeScores = new ArrayList<HighScore>();
    List<HighScore> past24HourScores = new ArrayList<HighScore>();
    List<HighScore> pastHourScores = new ArrayList<HighScore>();

    // Query for the list of the top 10 all-time scores
    Query allTimeQuery = new Query("HighScore").addSort("score", Query.SortDirection.DESCENDING);
    List<Entity> scoreEntities = datastoreService
        .prepare(allTimeQuery)
        .asList(FetchOptions.Builder.withLimit(10));
    for (Entity score : scoreEntities) {
      allTimeScores.add(HighScore.fromEntity(score));
    }

    Date now = new Date();

    // Query for the list of the top 10 scores today.
    Date pastDay = new Date(now.getTime() - (1000 * 60 * 60 * 24));
    Query.Filter dayFilter =
        new Query.FilterPredicate("date", Query.FilterOperator.GREATER_THAN_OR_EQUAL, pastDay);
    Query past24HourQuery = new Query("HighScore")
        .setFilter(dayFilter)
        .addSort("date", Query.SortDirection.DESCENDING)
        .addSort("score", Query.SortDirection.DESCENDING);
    scoreEntities = datastoreService.prepare(past24HourQuery)
        .asList(FetchOptions.Builder.withLimit(10));
    for (Entity score : scoreEntities) {
      past24HourScores.add(HighScore.fromEntity(score));
    }

    // Query for the list of the top 10 scores in the past hour.
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

    return new HighScoreSnapshot(pastHourScores, past24HourScores, allTimeScores);
  }

  public void addNewScore(DatastoreService datastoreService, HighScore newScore) {
    /*
    // BUG FIX: This app is woefully insecure, anybody can just
    // HTTP POST "/highscore?score=999999"!
    // 
    // Fix this by only allowing application administrators to post high scores.
    UserService userService = UserServiceFactory.getUserService();
    if (userService.getCurrentUser() == null || !userService.isUserAdmin()) {
      throw new RuntimeException("Non-admin user attempting to upload a high score!");
    }
    */

    // TODO(chris): User a proper escaping library.
    String name = newScore.getPlayer()
        .replace('&', ' ')
        .replace('<', ' ')
        .replace('>', ' ')
        .replace("script", "");
    // Create an entity to store the score data in the AppEngine Datastore.
    Key key = KeyFactory.createKey("HighScore", name + "-" + newScore.getScore());
    Entity score = new Entity(key);
    score.setProperty("player", name);
    score.setProperty("score", newScore.getScore());
    score.setProperty("date", newScore.getDate());

    datastoreService.put(score);
  }
}
