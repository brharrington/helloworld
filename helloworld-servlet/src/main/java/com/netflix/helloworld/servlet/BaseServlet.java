package com.netflix.helloworld.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides a place to put common helpers across all servlets for the application.
 */
public class BaseServlet extends HttpServlet {
  /**
   * This is here for CORS pre-flight checks. You would need a separate filter or code to
   * verify the access and set the appropriate headers.
   */
  @Override public void doOptions(HttpServletRequest req, HttpServletResponse res) {
    res.setStatus(200);
  }
}
