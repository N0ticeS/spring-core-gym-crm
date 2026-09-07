@component @core @trainee @trainee-trainings
Feature: Trainee trainings

  Scenario: Trainee successfully retrieves all their trainings
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And training "Morning Fitness" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-01" for 60 minutes
    And training "Evening Fitness" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-05" for 90 minutes
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated trainee "John.Smith" requests trainings
    Then the response status should be 200
    And the response should contain 2 trainings

  Scenario: Trainee trainings can be filtered
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And training "Old Training" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-08-20" for 60 minutes
    And training "New Training" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-05" for 60 minutes
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated trainee "John.Smith" requests trainings from "2026-09-01"
    Then the response status should be 200
    And the response should contain 1 training
    And the response should contain training "New Training"

  Scenario: Trainee receives empty list when no trainings match
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And training "Morning Fitness" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-01" for 60 minutes
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated trainee "John.Smith" requests trainings from "2027-01-01"
    Then the response status should be 200
    And the response should contain 0 trainings

  Scenario: Requesting trainings for nonexistent trainee fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated trainee "Unknown.User" requests trainings
    Then the response status should be 404

  Scenario: Trainee cannot retrieve another trainee trainings
    Given a trainee with username "John.Smith" exists
    And a trainee with username "Alex.Brown" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated trainee "Alex.Brown" requests trainings
    Then the response status should be 403
