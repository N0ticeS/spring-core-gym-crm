@component @core @trainee @trainee-trainers
Feature: Trainee trainers management

  Scenario: Trainee successfully retrieves unassigned trainers
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And a trainer with username "Anna.White" and specialization "Yoga" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated trainee "John.Smith" requests unassigned trainers
    Then the response status should be 200
    And the response should contain 2 trainers
    And the response should contain trainer "Mike.Johnson"
    And the response should contain trainer "Anna.White"

  Scenario: Assigned trainer is not returned as unassigned
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And a trainer with username "Anna.White" and specialization "Yoga" exists
    And trainer "Mike.Johnson" is assigned to trainee "John.Smith"
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated trainee "John.Smith" requests unassigned trainers
    Then the response status should be 200
    And the response should contain 1 trainer
    And the response should contain trainer "Anna.White"
    And the response should not contain trainer "Mike.Johnson"

  Scenario: Trainee successfully updates assigned trainers
    Given a trainee with username "John.Smith" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And a trainer with username "Anna.White" and specialization "Yoga" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated trainee "John.Smith" updates trainers to "Mike.Johnson,Anna.White"
    Then the response status should be 200
    And trainee "John.Smith" should have 2 assigned trainers
    And trainee "John.Smith" should have trainer "Mike.Johnson"
    And trainee "John.Smith" should have trainer "Anna.White"

  Scenario: Updating trainers with nonexistent trainer fails
    Given a trainee with username "John.Smith" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated trainee "John.Smith" updates trainers to "Unknown.Trainer"
    Then the response status should be 404

  Scenario: Trainee cannot manage trainers of another trainee
    Given a trainee with username "John.Smith" exists
    And a trainee with username "Alex.Brown" exists
    And a trainer with username "Mike.Johnson" and specialization "Fitness" exists
    And the user logs in with username "John.Smith" and password "Password123"
    When the authenticated trainee "Alex.Brown" updates trainers to "Mike.Johnson"
    Then the response status should be 403
