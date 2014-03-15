package com.google.games.flappingbird;

import java.util.Date;
import java.util.List;

/**
 * Current High Score snapshot.
 *
 * @author chrsmith@google.com (Chris Smith)
 */
public class HighScoreSnapshot {
  private List<HighScore> pastHour = null;
  private List<HighScore> allTime = null;

  public HighScoreSnapshot(List<HighScore> pastHour, List<HighScore> allTime) {
    this.pastHour = pastHour;
    this.allTime = allTime;
  }

  public List<HighScore> getAllTime() {
      return allTime;
  }

  public List<HighScore> getPastHour() {
    return pastHour;
  }
}
