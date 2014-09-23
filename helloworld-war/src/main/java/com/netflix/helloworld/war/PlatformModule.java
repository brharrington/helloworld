package com.netflix.helloworld.war;

import com.google.inject.AbstractModule;
import com.netflix.adminresources.AdminResourceExplorer;
import com.netflix.adminresources.AdminResourcesContainer;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.CloudInstanceConfig;
import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.discovery.DefaultEurekaClientConfig;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.DiscoveryManager;
import com.netflix.explorers.ExplorerManager;
import com.netflix.explorers.ExplorersManagerImpl;
import com.netflix.helloworld.servlet.AppHealthcheckHandler;

public class PlatformModule extends AbstractModule {
  @Override protected void configure() {
    try {
      final DiscoveryManager mgr = DiscoveryManager.getInstance();
      CloudInstanceConfig instanceCfg = new CloudInstanceConfig("netflix.appinfo.");
      DefaultEurekaClientConfig clientCfg = new DefaultEurekaClientConfig();
      HealthCheckHandler handler = new AppHealthcheckHandler();
      mgr.initComponent(instanceCfg, clientCfg);
      mgr.getDiscoveryClient().registerHealthCheck(handler);

      bind(DiscoveryClient.class).toInstance(mgr.getDiscoveryClient());
      bind(HealthCheckHandler.class).toInstance(handler);
      bind(ApplicationInfoManager.class).toInstance(ApplicationInfoManager.getInstance());

      bind(AdminResourcesContainer.class).asEagerSingleton();
    } catch (Exception e) {
      throw new IllegalStateException("eureka initialization failed", e);
    }
  }
}
