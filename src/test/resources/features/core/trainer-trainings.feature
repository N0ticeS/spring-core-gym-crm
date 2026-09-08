@component @core @trainer @trainer-trainings
Feature: Trainer trainings

  Scenario: Trainer successfully retrieves all their trainings
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And training "Morning Fitness" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-01" for 60 minutes
    And training "Evening Fitness" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-05" for 90 minutes
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated trainer "Mike.Johnson" requests trainings
    Then the response status should be 200
    And the response should contain 2 trainings

  Scenario: Trainer trainings can be filtered by date
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And training "Old Training" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-08-20" for 60 minutes
    And training "New Training" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-05" for 60 minutes
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated trainer "Mike.Johnson" requests trainings from "2026-09-01"
    Then the response status should be 200
    And the response should contain 1 training
    And the response should contain training "New Training"

  Scenario: Trainer receives empty list when no trainings match
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And training "Morning Fitness" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-01" for 60 minutes
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated trainer "Mike.Johnson" requests trainings from "2027-01-01"
    Then the response status should be 200
    And the response should contain 0 trainings

  Scenario: Requesting trainings for nonexistent trainer fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated trainer "Unknown.Trainer" requests trainings
    Then the response status should be 404

  Scenario: Trainer cannot retrieve another trainer trainings
    Given a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And a trainer with username "Anna.White" and specialization "Yoga" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated trainer "Anna.White" requests trainings
    Then the response status should be 403
