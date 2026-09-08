@component @workload @consumer
Feature: Trainer workload message processing

  Scenario: ADD message creates trainer workload
    When the workload consumer receives ADD message for trainer "Mike.Johnson" on "2026-09-10" for 60 minutes
    Then workload for "Mike.Johnson" year 2026 month 9 should have duration 60

  Scenario: Multiple ADD messages accumulate duration
    When the workload consumer receives ADD message for trainer "Mike.Johnson" on "2026-09-10" for 60 minutes
    And the workload consumer receives ADD message for trainer "Mike.Johnson" on "2026-09-15" for 90 minutes
    Then workload for "Mike.Johnson" year 2026 month 9 should have duration 150

  Scenario: DELETE message decreases duration
    When the workload consumer receives ADD message for trainer "Mike.Johnson" on "2026-09-10" for 120 minutes
    And the workload consumer receives DELETE message for trainer "Mike.Johnson" on "2026-09-10" for 60 minutes
    Then workload for "Mike.Johnson" year 2026 month 9 should have duration 60

  Scenario: ADD message for another month creates separate monthly summary
    When the workload consumer receives ADD message for trainer "Mike.Johnson" on "2026-09-10" for 60 minutes
    And the workload consumer receives ADD message for trainer "Mike.Johnson" on "2026-10-10" for 90 minutes
    Then workload for "Mike.Johnson" year 2026 month 9 should have duration 60
    And workload for "Mike.Johnson" year 2026 month 10 should have duration 90

  Scenario: ADD message for another year creates separate yearly summary
    When the workload consumer receives ADD message for trainer "Mike.Johnson" on "2026-09-10" for 60 minutes
    And the workload consumer receives ADD message for trainer "Mike.Johnson" on "2027-01-10" for 120 minutes
    Then workload for "Mike.Johnson" year 2026 month 9 should have duration 60
    And workload for "Mike.Johnson" year 2027 month 1 should have duration 120

  Scenario: Invalid message with zero duration is rejected
    When the workload consumer receives invalid ADD message for trainer "Mike.Johnson" with zero duration
    Then a constraint violation should be thrown
    And workload for trainer "Mike.Johnson" should not exist
