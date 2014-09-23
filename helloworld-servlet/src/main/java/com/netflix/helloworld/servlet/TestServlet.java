package com.netflix.helloworld.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Simple servlet that allows the status code and latency to be determined by query parameters
 * for testing purposes. Supported query parameters:
 *
 * <ul>
 *   <li>sleep: number of milliseconds to sleep before returning the response.</li>
 *   <li>status: status code to use for the response.</li>
 * </ul>
 */
@Singleton
public class TestServlet extends BaseServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestServlet.class);

  private int getInt(HttpServletRequest req, String param, int dflt) {
    final String v = req.getParameter(param);
    return (v == null) ? dflt : Integer.parseInt(v);
  }

  @Override public void doGet(HttpServletRequest req, HttpServletResponse res) {
    final int sleep = getInt(req, "sleep", 0);
    try {
      Thread.sleep(sleep);
    } catch (InterruptedException e) {
      LOGGER.debug("sleep interrupted", e);
    }

    final int status = getInt(req, "status", 200);
    res.setStatus(status);
  }
}
