package com.netflix.helloworld.servlet;

import com.google.inject.servlet.ServletModule;
import com.netflix.appinfo.HealthCheckHandler;

public class AppModule extends ServletModule {
  @Override protected void configureServlets() {
    serve("/healthcheck").with(HealthcheckServlet.class);
    serve("/edda").with(EddaServlet.class);
    serve("/test").with(TestServlet.class);
  }
}
