package com.netflix.helloworld.servlet;

import com.netflix.appinfo.HealthCheckHandler;
import com.netflix.appinfo.InstanceInfo;

import javax.inject.Singleton;

@Singleton
public class AppHealthcheckHandler implements HealthCheckHandler {

  private volatile InstanceInfo.InstanceStatus status;

  public AppHealthcheckHandler() {
    this.status = InstanceInfo.InstanceStatus.STARTING;
  }

  public void setStatus(InstanceInfo.InstanceStatus status) {
    this.status = status;
  }

  @Override
  public InstanceInfo.InstanceStatus getStatus(InstanceInfo.InstanceStatus currentStatus) {
    return status;
  }
}
