@component @core @security
Feature: Core service permissions

  Scenario: Protected endpoint cannot be accessed without authentication
    When the user requests all trainers without authentication
    Then the response status should be 401

  Scenario: Admin can create a trainee
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user creates a trainee
    Then the response status should be 200

  Scenario: Trainer cannot create a trainee
    Given a trainer user with username "Mike.Johnson" and password "Password123" exists
    And the user logs in with username "Mike.Johnson" and password "Password123"
    When the authenticated user creates a trainee
    Then the response status should be 403
