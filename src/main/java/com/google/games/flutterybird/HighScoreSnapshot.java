package com.google.games.flutterybird;

import java.util.Collections;
import java.util.Comparator;
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

    // Fix a bug with how we are querying the datastore. Sort the lists
    // so that score is descending.
    Comparator<HighScore> comparator = new Comparator<HighScore>() {
        @Override
        public int compare(HighScore a, HighScore b) {
          long x = a.getScore();
          long y = b.getScore();
          if (x == y) {
            return 0;
          } else if (x > y) {
            return -1;
          } else {
            return 1;
          }
        }
    };

    Collections.sort(pastHour, comparator);
    Collections.sort(past24Hours, comparator);
    Collections.sort(allTime, comparator);
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
