Feature: Kitchen manager assigns tasks

  Scenario: Kitchen manager assigns task to chef
    Given the kitchen manager is logged into the system
    When the kitchen manager selects a chef
    And assigns a cooking task
    Then the system should save the task assignment

  Scenario: Kitchen manager assigns task based on workload
    Given the kitchen manager is assigning tasks
    When the system suggests a chef with a lighter workload
    Then the kitchen manager should assign the task to that chef based on workload

  Scenario: Kitchen manager assigns task based on expertise
    Given the kitchen manager is assigning tasks
    When the system suggests a chef with relevant expertise
    Then the kitchen manager should assign the task to that chef based on expertise