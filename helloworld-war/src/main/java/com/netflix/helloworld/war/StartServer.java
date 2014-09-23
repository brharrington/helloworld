package com.netflix.helloworld.war;

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.config.ConfigurationManager;
import com.netflix.governator.guice.LifecycleInjector;
import com.netflix.governator.lifecycle.LifecycleManager;
import com.netflix.helloworld.servlet.AppHealthcheckHandler;
import com.netflix.helloworld.servlet.AppModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import java.util.ArrayList;
import java.util.List;


public class StartServer extends GuiceServletContextListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartServer.class);

  private Injector injector;

  public StartServer() {
    List<Module> modules = new ArrayList<>();
    modules.add(new PlatformModule());
    modules.add(new AppModule());

    injector = LifecycleInjector.builder()
        .withModules(modules)
        .build()
        .createInjector();
  }

  @Override protected Injector getInjector() {
    return injector;
  }

  @Override public void contextInitialized(ServletContextEvent event) {
    super.contextInitialized(event);
    try {
      LOGGER.info("loading application.properties");
      ConfigurationManager.loadPropertiesFromResources("application.properties");
      LifecycleManager lcMgr = injector.getInstance(LifecycleManager.class);
      lcMgr.start();
    } catch (Exception e) {
      LOGGER.error("initialization failed", e);
      throw new IllegalStateException(e);
    }
    injector.getInstance(AppHealthcheckHandler.class).setStatus(InstanceInfo.InstanceStatus.UP);
    LOGGER.info("initialization complete");
  }

  @Override public void contextDestroyed(ServletContextEvent event) {
    super.contextDestroyed(event);
    LifecycleManager lcMgr = injector.getInstance(LifecycleManager.class);
    lcMgr.close();
  }
}
