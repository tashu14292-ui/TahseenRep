Feature: End to End API testing for Place API 

@AddPlace
  Scenario: Add place
    Given User requests for "Add Place" using the below data:
      | name            | language  | address          | accuracy | phone_number        | website           | lat         | lng        | types           |
      | Frontline house | French-IN | 10, Tahseen test automation  | 50       | (+91) 983 893 3937  | http://google.com | -38.383494 | 33.427362 | shoe park,shop |
    When User calls "AddPlace" API using "POST" method
    Then API response status code should be 200
    And Response field "status" should be "OK"
    
@GetPlace
  Scenario: Get place
  # GET PLACE API VALIDATION
  Given User calls "GetPlace" API using "GET" method
  When API response status code should be 200
  Then Response field "name" should be "Frontline house"
  And Response field "language" should be "French-IN"
  And Response field "address" should be "10, Tahseen test automation"
 
@PutPlace
  Scenario: Put place
  # PUT PLACE API VALIDATION
  Given User requests for "Put Place" using the below data:
      | address            | place id            |
      | 15, Tahseen test automation, India | Already stored from get scenario into apiResponseData/place_id.text |
  When User calls "PutPlace" API using "PUT" method
  Then API response status code should be 200
  And Response field "msg" should be "Address successfully updated"
  When User calls "GetPlace" API using "GET" method
  And API response status code should be 200
  And Response field "address" should be "15, Tahseen test automation, India"
  
@DeletePlace
  Scenario: Delete place
  # DELETE PLACE API VALIDATION
  Given User requests for "Delete Place" using the below data:
      | place id            |
      | already stored from get scenario into apiResponseData/place_id.text |
  When User calls "DeletePlace" API using "DELETE" method
  Then API response status code should be 200
  And Response field "status" should be "OK"
  And User calls "GetPlace" API using "GET" method
  And API response status code should be 404
  And Response field "msg" should be "Get operation failed, looks like place_id  doesn't exists"
