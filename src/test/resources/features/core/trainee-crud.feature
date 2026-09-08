@component @core @trainee
Feature: Trainee CRUD management

  Scenario: Admin successfully creates a trainee
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user creates a trainee with first name "John" and last name "Smith"
    Then the response status should be 200
    And the response should contain trainee username "John.Smith"

  Scenario: Trainee username is made unique when another trainee with the same name exists
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    And a trainee with first name "John" and last name "Smith" is created
    When the authenticated user creates a trainee with first name "John" and last name "Smith"
    Then the response status should be 200
    And the response should contain trainee username "John.Smith1"

  Scenario: Creating a trainee with an empty first name fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user creates a trainee with first name "" and last name "Smith"
    Then the response status should be 400

  Scenario: Creating a trainee with a future date of birth fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user creates a trainee with future date of birth
    Then the response status should be 400

  Scenario: Authenticated user successfully retrieves an existing trainee
    Given a trainee with username "John.Smith" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated user requests trainee "John.Smith"
    Then the response status should be 200
    And the response should contain trainee username "John.Smith"

  Scenario: Requesting a nonexistent trainee fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user requests trainee "Unknown.User"
    Then the response status should be 404

  Scenario: Trainee successfully updates their own profile
    Given a trainee with username "John.Smith" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated user updates trainee "John.Smith" with first name "Johnny" and last name "Smith"
    Then the response status should be 200
    And the response should contain first name "Johnny"

  Scenario: Updating a trainee with invalid data fails
    Given a trainee with username "John.Smith" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated user updates trainee "John.Smith" with first name "" and last name "Smith"
    Then the response status should be 400

  Scenario: Updating a nonexistent trainee fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user updates trainee "Unknown.User" with first name "John" and last name "Smith"
    Then the response status should be 404

  Scenario: Trainee successfully changes their status
    Given a trainee with username "John.Smith" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated user changes trainee "John.Smith" status to inactive
    Then the response status should be 200
    And trainee "John.Smith" should be inactive

  Scenario: Changing trainee to the same status fails
    Given an active trainee with username "John.Smith" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated user changes trainee "John.Smith" status to active
    Then the response status should be 409

  Scenario: Admin successfully deletes a trainee
    Given a trainee with username "John.Smith" exists
    And an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user deletes trainee "John.Smith"
    Then the response status should be 200
    And trainee "John.Smith" should no longer exist

  Scenario: Deleting a nonexistent trainee fails
    Given an admin user with username "Admin.User" and password "Password123" exists
    And the user logs in with username "Admin.User" and password "Password123"
    When the authenticated user deletes trainee "Unknown.User"
    Then the response status should be 404

  Scenario: Trainee cannot delete their own profile
    Given a trainee with username "John.Smith" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated user deletes trainee "John.Smith"
    Then the response status should be 403
