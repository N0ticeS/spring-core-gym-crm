@component @core @trainer
Feature: Trainer CRUD management

  Scenario: Admin successfully creates a trainer
    Given an admin user with username "Admin.User" and password "Password123" exists
    And training type "Fitness" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user creates a trainer with first name "Mike" last name "Johnson" and specialization "Fitness"
    Then the response status should be 200
    And the response should contain trainer username "Mike.Johnson"

  Scenario: Trainer username is made unique when another trainer with the same name exists
    Given an admin user with username "Admin.User" and password "Password123" exists
    And training type "Fitness" exists
    And the user logs in with username "Admin.User" and password "Password123"
    And a trainer with first name "Mike" last name "Johnson" and specialization "Fitness" is created
    When the authenticated user creates a trainer with first name "Mike" last name "Johnson" and specialization "Fitness"
    Then the response status should be 200
    And the response should contain trainer username "Mike.Johnson1"

  Scenario: Creating a trainer with empty first name fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And training type "Fitness" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user creates a trainer with first name "" last name "Johnson" and specialization "Fitness"
    Then the response status should be 400

  Scenario: Creating a trainer with nonexistent specialization fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user creates a trainer with first name "Mike" last name "Johnson" and specialization "Unknown"
    Then the response status should be 404

  Scenario: Authenticated user successfully retrieves an existing trainer
    Given a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user requests trainer "Mike.Johnson"
    Then the response status should be 200
    And the response should contain trainer username "Mike.Johnson"

  Scenario: Requesting a nonexistent trainer fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user requests trainer "Unknown.Trainer"
    Then the response status should be 404

  Scenario: Trainer successfully updates their own profile
    Given a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user updates trainer "Mike.Johnson" with first name "Michael" and last name "Johnson"
    Then the response status should be 200
    And the response should contain first name "Michael"

  Scenario: Updating trainer with invalid data fails
    Given a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user updates trainer "Mike.Johnson" with first name "" and last name "Johnson"
    Then the response status should be 400

  Scenario: Updating nonexistent trainer fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user updates trainer "Unknown.Trainer" with first name "Mike" and last name "Johnson"
    Then the response status should be 404

  Scenario: Trainer successfully changes their status
    Given a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user changes trainer "Mike.Johnson" status to inactive
    Then the response status should be 200
    And trainer "Mike.Johnson" should be inactive

  Scenario: Changing trainer to the same status fails
    Given a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user changes trainer "Mike.Johnson" status to active
    Then the response status should be 409

  Scenario: Trainer cannot update another trainer profile
    Given a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And a trainer with username "Anna.White" and specialization "Yoga" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user updates trainer "Anna.White" with first name "Anna" and last name "Black"
    Then the response status should be 403
