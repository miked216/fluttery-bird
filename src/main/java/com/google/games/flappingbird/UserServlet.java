package com.google.games.flappingbird;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Get information about the current user.
*
 * @author chrsmith@google.com (Chris Smith)
*/
public class UserServlet extends HttpServlet {
  private final Logger logger = Logger.getLogger(UserServlet.class.getName());

  /**
   * Exposed as `GET /user`.
   *     Returns the user's name and an optional URL to log in at.
   */
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      UserService userService = UserServiceFactory.getUserService();
      User currentUser = userService.getCurrentUser();


      String returnUrl = req.getParameter("returnURL");
      if (returnUrl == null) {
        returnUrl = "http://flapping-bird.appspot.com";
      }

      boolean isLoggedIn = (currentUser != null);
      String userName = (isLoggedIn) ? currentUser.getNickname() : "Anonymous";
      String logInUrl = userService.createLoginURL(returnUrl);
      String logOutUrl = userService.createLogoutURL(returnUrl);

      resp.setContentType("text/json");
      resp.getWriter().print("({");
      resp.getWriter().print("  userName: \"" + userName + "\",");
      resp.getWriter().print("  isLoggedIn: " + isLoggedIn + ",");  // bool
      resp.getWriter().print("  logInUrl: \"" + logInUrl + "\",");
      resp.getWriter().print("  logOutUrl: \"" + logInUrl + "\"");
      resp.getWriter().print("})");
  }
}
