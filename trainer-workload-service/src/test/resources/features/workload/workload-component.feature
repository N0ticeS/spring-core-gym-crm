@component @workload
Feature: Trainer workload retrieval

  Scenario: Authenticated user retrieves existing trainer workload
    Given trainer workload for "Mike.Johnson" exists for year 2026 month 9 with duration 120
    And a valid token for user "Mike.Johnson" exists
    When the authenticated user requests workload for "Mike.Johnson" year 2026 month 9
    Then the response status should be 200
    And the workload response should contain duration 120

  Scenario: Requesting workload for nonexistent trainer fails
    Given a valid token for user "Mike.Johnson" exists
    When the authenticated user requests workload for "Unknown.Trainer" year 2026 month 9
    Then the response status should be 404

  Scenario: Requesting nonexistent year returns zero duration
    Given trainer workload for "Mike.Johnson" exists for year 2026 month 9 with duration 120
    And a valid token for user "Mike.Johnson" exists
    When the authenticated user requests workload for "Mike.Johnson" year 2025 month 9
    Then the response status should be 404

  Scenario: Requesting nonexistent month fails
    Given trainer workload for "Mike.Johnson" exists for year 2026 month 9 with duration 120
    And a valid token for user "Mike.Johnson" exists
    When the authenticated user requests workload for "Mike.Johnson" year 2026 month 8
    Then the response status should be 404

  Scenario: Request without authentication fails
    Given trainer workload for "Mike.Johnson" exists for year 2026 month 9 with duration 120
    When the unauthenticated user requests workload for "Mike.Johnson" year 2026 month 9
    Then the response status should be 403
