@integration
Feature: Training workload integration

  Scenario: Creating training updates trainer workload
    Given the Core and Workload services are running
    And trainer "Mike.Johnson" and trainee "John.Smith" exist
    And the user is authenticated as admin
    When a 60 minute training is created through Core
    Then Core should return 200
    And eventually workload for "Mike.Johnson" should contain 60 minutes

  Scenario: Multiple trainings accumulate trainer workload
    Given the Core and Workload services are running
    And trainer "Mike.Johnson" and trainee "John.Smith" exist
    And the user is authenticated as admin
    When a 60 minute training is created through Core
    Then Core should return 200
    And eventually workload for "Mike.Johnson" should contain 60 minutes
    When a 90 minute training is created through Core
    Then Core should return 200
    And eventually workload for "Mike.Johnson" should contain 150 minutes

  Scenario: Deleting training decreases trainer workload
    Given the Core and Workload services are running
    And trainer "Mike.Johnson" and trainee "John.Smith" exist
    And the user is authenticated as admin
    When a 120 minute training is created through Core
    Then Core should return 200
    And eventually workload for "Mike.Johnson" should contain 120 minutes
    When the created training is deleted through Core
    Then Core should return 200
    And eventually workload for "Mike.Johnson" should contain 0 minutes

  Scenario: Invalid training does not update trainer workload
    Given the Core and Workload services are running
    And trainer "Mike.Johnson" and trainee "John.Smith" exist
    And the user is authenticated as admin
    When a 0 minute training is created through Core
    Then Core should return 400
    And workload for "Mike.Johnson" should not exist
