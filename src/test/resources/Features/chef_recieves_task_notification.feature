Feature: Chef receives task notifications

Scenario: Chef receives task assignment
  Given the kitchen manager has assigned a task
  When the chef logs in
  Then the system should display the task notification

Scenario: Chef views task details
  Given the chef has received a task notification
  When the chef selects the task
  Then the system should display the task details

Scenario: Chef marks task as completed
  Given the chef has completed a task
  When the chef marks the task as completed
  Then the system should update the task status