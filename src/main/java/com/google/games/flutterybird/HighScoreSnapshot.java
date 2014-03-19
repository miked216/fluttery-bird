package com.google.games.flutterybird;

import java.util.Date;
import java.util.List;

/**
 * Current High Score snapshot.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class HighScoreSnapshot {
  private List<HighScore> pastHour = null;
  private List<HighScore> past24Hours = null;
  private List<HighScore> allTime = null;

  public HighScoreSnapshot(List<HighScore> pastHour, List<HighScore> past24Hours, List<HighScore> allTime) {
    this.pastHour = pastHour;
    this.past24Hours = past24Hours;
    this.allTime = allTime;
  }

  public List<HighScore> getAllTime() {
      return allTime;
  }

  public List<HighScore> getPastHour() {
    return pastHour;
  }

  public List<HighScore> getPast24Hours() {
    return past24Hours;
  }
}
