Feature: LoggedIn View

  Scenario: Validate user is able to view after login
    Given User navigates to Login Page
    When User succesfully enters the log in details
    Then User should be able to view the product category page
