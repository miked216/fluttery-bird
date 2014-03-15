package com.google.games.flappingbird;

import com.google.appengine.api.datastore.Entity;

import java.util.Date;

/**
 * Contains the data for a high score saved by a registered user.
 *
 * @author joannasmith@google.com (Joanna Smith)
 */
public class HighScore {
  private final long score;
  private final Date date;
  private final String player;

  public static HighScore fromEntity(Entity scoreEntity) {
    return new HighScore(
        (Long) scoreEntity.getProperty("score"),
        (Date) scoreEntity.getProperty("date"),
        (String) scoreEntity.getProperty("player"));
  }

  public HighScore(long score, Date date, String player) {
    this.score = score;
    this.date = date;
    this.player = player;
  }

  public long getScore() {
    return score;
  }

  public Date getDate() {
    return date;
  }

  public String getPlayer() {
    return player;
  }
}
