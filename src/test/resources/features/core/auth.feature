@component @core @auth
Feature: User authentication

  Scenario: Successful login with valid credentials
    Given a user with username "John.Smith" and password "Password123" exists
    When the user logs in with username "John.Smith" and password "Password123"
    Then the response status should be 200
    And the response should contain a JWT token

  Scenario: Login fails with incorrect password
    Given a user with username "John.Smith" and password "Password123" exists
    When the user logs in with username "John.Smith" and password "WrongPassword123"
    Then the response status should be 401

  Scenario: Login fails when username is empty
    When the user logs in with username "" and password "Password123"
    Then the response status should be 400

  Scenario: Login fails when password is empty
    When the user logs in with username "John.Smith" and password ""
    Then the response status should be 400
