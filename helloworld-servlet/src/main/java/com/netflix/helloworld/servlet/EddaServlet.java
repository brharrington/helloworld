package com.netflix.helloworld.servlet;

import com.netflix.client.ClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpResponse;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.niws.client.http.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

/**
 * Return the edda content for this instance.
 */
@Singleton
public class EddaServlet extends BaseServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(EddaServlet.class);

  private static final String EDDA_CLIENT_NAME = "edda";
  private static final String EDDA_PATH = "/api/v2/view/instances/%s";

  private String getInstanceId() {
    return System.getenv("EC2_INSTANCE_ID");
  }

  private void copy(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int length;
    while ((length = in.read(buffer)) != -1) {
      out.write(buffer, 0, length);
    }
  }

  @SuppressWarnings("deprecation")
  @Override public void doGet(HttpServletRequest req, HttpServletResponse res) {
    String id = getInstanceId();
    if (id == null) {
      res.setStatus(500);
    } else {
      try {
        RestClient rc = (RestClient) ClientFactory.getNamedClient(EDDA_CLIENT_NAME);
        HttpRequest eddaReq = new HttpRequest.Builder()
            .verb(HttpRequest.Verb.GET)
            .uri(String.format(EDDA_PATH, id))
            .build();
        try (HttpResponse eddaRes = rc.executeWithLoadBalancer(eddaReq)) {
          if (eddaRes.getStatus() == 200) {
            res.setStatus(200);
            res.setContentType("application/json");
            try (
                InputStream in = eddaRes.getInputStream();
                OutputStream out = res.getOutputStream()) {
              copy(in, out);
            }
          }
        }
      } catch (Exception e) {
        LOGGER.error("failed to get data from edda", e);
        res.setStatus(500);
      }
    }
  }
}
