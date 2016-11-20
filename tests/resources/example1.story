Scenario: My first scenario
Meta:
@HPQC_ID some_id
@Requirement A-1, B-2
@DomainObject Car

Given working application
And 3 instances of Car object with following values
| Engine            | Color           |
| big               | red             |
| small             | blue            |
| broken            | green           |
And one configuration for all examples

When open edit view for Car object with parameter color set to <color>
And setting value for Is GPS Present to: Yes
And setting value for <field> to: <value>
Then form is submitted with status <status>
And error message for field <field> is shown <message>
Examples:
| network | field                            | value                             | status  | message                                                                          |
| Long    | Car Name                         |                                   | Failure | The 'Device Name' field is required                                              |
| Long    | Car Name                         | [space]                           | Success |                                                                                  |
| Long    | Car Name                         | [latino]punctuation               | Success |                                                                                  |
| Long    | Car Name                         | [latino]upper                     | Success |                                                                                  |
|-- Long  | Service ID                       | 1234567890123456                  | Failure | db validation failed                                                             |
| Long    | Service ID                       | aAóÓ:"\                           | Success |
