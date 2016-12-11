package com.mcsaatchi.gmfit.architecture.otto;

import com.squareup.otto.Bus;

public class EventBusSingleton {
  private static final Bus BUS = new Bus();

  private EventBusSingleton() {
    //no instances
  }

  public static Bus getInstance() {
    return BUS;
  }
}