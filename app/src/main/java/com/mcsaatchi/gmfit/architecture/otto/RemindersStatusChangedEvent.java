package com.mcsaatchi.gmfit.architecture.otto;

public class RemindersStatusChangedEvent {
  private boolean isReminderOn;

  public RemindersStatusChangedEvent(boolean isReminderOn) {
    this.isReminderOn = isReminderOn;
  }

  public boolean isReminderOn() {
    return isReminderOn;
  }
}
