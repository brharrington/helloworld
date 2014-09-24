package com.netflix.helloworld.servlet;

import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.ribbon.transport.netty.RibbonTransport;
import com.netflix.ribbon.transport.netty.http.LoadBalancingHttpClient;
import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Return the edda content for this instance.
 */
@Singleton
public class EddaServlet extends BaseServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(EddaServlet.class);

  private static final String EDDA_CLIENT_NAME = "edda";
  private static final String EDDA_PATH = "/api/v2/view/instances/%s";

  private final LoadBalancingHttpClient<ByteBuf, ByteBuf> eddaClient = createEddaClient();

  private LoadBalancingHttpClient<ByteBuf, ByteBuf> createEddaClient() {
    final IClientConfig config =
        DefaultClientConfigImpl.getClientConfigWithDefaultValues(EDDA_CLIENT_NAME);
    return RibbonTransport.newHttpClient(config);
  }

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

  @Override public void doGet(final HttpServletRequest req, final HttpServletResponse res) {
    String id = getInstanceId();
    if (id == null) {
      res.setStatus(500);
    } else {
      try {
        HttpClientRequest<ByteBuf> eddaReq =
            HttpClientRequest.createGet(String.format(EDDA_PATH, id));
        HttpClientResponse<ByteBuf> eddaRes = eddaClient.submit(eddaReq)
            .toBlocking()
            .single();
        res.setStatus(eddaRes.getStatus().code());
        eddaRes.getContent().toBlocking().forEach((ByteBuf content) -> {
          try {
            content.readBytes(res.getOutputStream(), content.capacity());
          } catch (IOException e) {
            LOGGER.error("failed to write edda output", e);
          }
        });
      } catch (Exception e) {
        LOGGER.error("failed to get data from edda", e);
        res.setStatus(500);
      }
    }
  }
}
