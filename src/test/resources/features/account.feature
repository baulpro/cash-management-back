Feature: Account API integration tests

  Background:
    * url baseUrl

  Scenario: should return not found when client does not exist
    Given path 'accounts'
    And request
      """
      {
        "accountNumber": "1234567890",
        "typeAccount": "SAVING",
        "originalBalance": 500,
        "status": true,
        "clientId": "11111111-1111-1111-1111-111111111111"
      }
      """
    When method post
    Then status 404
    And match response.status == 404
    And match response.error == 'Not Found'

  Scenario: should return not found when account number is blank and client does not exist
    Given path 'accounts'
    And request
      """
      {
        "accountNumber": "",
        "typeAccount": "SAVING",
        "originalBalance": 500,
        "status": true,
        "clientId": "11111111-1111-1111-1111-111111111111"
      }
      """
    When method post
    Then status 404