@component @core @training
Feature: Training management

  Scenario: Trainer successfully creates a training
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user creates training "Morning Fitness" for trainee "John.Smith" with trainer "Mike.Johnson" tomorrow for 60 minutes
    Then the response status should be 200
    And training "Morning Fitness" should exist

  Scenario: Creating training with past date fails
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user creates training "Old Training" for trainee "John.Smith" with trainer "Mike.Johnson" yesterday for 60 minutes
    Then the response status should be 400

  Scenario: Creating training with nonexistent trainee fails
    Given a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user creates training "Morning Fitness" for trainee "Unknown.User" with trainer "Mike.Johnson" tomorrow for 60 minutes
    Then the response status should be 404

  Scenario: Trainer cannot create training for another trainer
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And a trainer with username "Anna.White" and specialization "Yoga" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user creates training "Yoga Session" for trainee "John.Smith" with trainer "Anna.White" tomorrow for 60 minutes
    Then the response status should be 403

  Scenario: Admin successfully retrieves all trainings
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And training "Morning Fitness" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-01" for 60 minutes
    And training "Evening Fitness" exists for trainee "John.Smith" with trainer "Mike.Johnson" on "2026-09-05" for 90 minutes
    And an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user requests all trainings
    Then the response status should be 200
    And the response should contain 2 trainings

  Scenario: Non-admin cannot retrieve all trainings
    Given a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user requests all trainings
    Then the response status should be 403

  Scenario: Admin successfully deletes a future training
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And a future training "Morning Fitness" exists for trainee "John.Smith" with trainer "Mike.Johnson"
    And an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user deletes the saved training
    Then the response status should be 200
    And the saved training should no longer exist

  Scenario: Deleting a past training fails
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And a past training "Old Training" exists for trainee "John.Smith" with trainer "Mike.Johnson"
    And an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user deletes the saved training
    Then the response status should be 409

  Scenario: Deleting nonexistent training fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user deletes training with id 999999
    Then the response status should be 404
