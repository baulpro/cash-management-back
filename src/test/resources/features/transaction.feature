Feature: Transaction API integration tests

  Background:
    * url baseUrl

  Scenario: should return not found when account does not exist
    Given path 'transactions'
    And request
      """
      {
        "accountNumber": "9999999999",
        "transactionType": "DEBIT",
        "amount": 100
      }
      """
    When method post
    Then status 404
    And match response.status == 404
    And match response.error == 'Not Found'

  Scenario: should return bad request when amount is negative
    Given path 'transactions'
    And request
      """
      {
        "accountNumber": "1234567890",
        "transactionType": "DEBIT",
        "amount": -100
      }
      """
    When method post
    Then status 400