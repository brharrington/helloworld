package com.netflix.helloworld.servlet;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Singleton
public class HealthcheckServlet extends BaseServlet {

  private final ApplicationInfoManager appInfoMgr;
  private final HealthCheckHandler handler;

  @Inject
  public HealthcheckServlet(ApplicationInfoManager appInfoMgr, HealthCheckHandler handler) {
    this.appInfoMgr = appInfoMgr;
    this.handler = handler;
  }

  @Override public void doGet(HttpServletRequest req, HttpServletResponse res) {
    InstanceInfo.InstanceStatus status = handler.getStatus(appInfoMgr.getInfo().getStatus());
    switch (status) {
      case UP:
        res.setStatus(200);
        break;
      case DOWN:
        res.setStatus(500);
        break;
      case OUT_OF_SERVICE:
        res.setStatus(503);
        break;
      default:
        res.setStatus(500);
        break;
    }
  }
}
