##Data input formats

The underlying data format is .CSV 
A range of sample problems can be found in this folder.  

#CSV Stucture

The CSV files can be divided into two areas:

1. Keywords
2. Customer/Vist list

##Keywords
The keywords give foodel information about your problem type and explain how you would like to solve the problem.

This section is structured by row where each row comprises a keyphrase in column A and an associated value in colum B.

Valid keyphrases at present:

|        Keyphrase        |       Value      | Meaning                                                                                                      |
|:-----------------------:|:----------------:|--------------------------------------------------------------------------------------------------------------|
|           date          | dd/MM/yyyy HH:mm | The date time to be  used on the output                                                                      |
|     vehicle capacity    |         n        | The capacity of each vehicle, vehicles will not be allocated  more deliveries than they can carry            |
|     round time limit    |         n        | The maximum time in minutes between  a                                                                       |
| delivery time per house |         n        | The time (mins) to spend on each delivery                                                                    |
|          start          |      address     | An address string representing the  start point                                                              |
|           end           |      address     | An address string representing the  end point. If not present then assumes that the end point is the start.  |
|    vehicles available   |         n        | If included this will build the solution around n vehicles.  This overrides the round time limit             |

You can have values in any other cells or rows, as long as you are aware that if you have a keyphrase in col A then foodel will treat cols A and B as a keyword/value pair. 

##Customer/Visit list


After the keywords you will need to list the customers/vasits (there's no difference between a customer and a visit except the keyword). One row is used for each visit.

Initially a header row is required, the header  specifies column names so that foodel knows which columns contain which information. Valid headers and their meanings are:

| column name | format | use                                                                                                              | example                                 |
|-------------|--------|------------------------------------------------------------------------------------------------------------------|-----------------------------------------|
| name        | String | The name of the customer/visit                                                                                   | "Mr Jim Smith"                          |
| address     | String | Address                                                                                                          | "21 Accia Road Edinburgh"               |
| postcode    | String | Postal code (or ZIP in non-uk companies)                                                                         | "EH1 1AA"                               |
| bags        | int    | The number of bags to be delivered (sum of  the bags allocated to a route will not exceed the  vehicle capacity) | 3                                       |
| note        | String | A string that will be displayed on the delivery list                                                             | "Leave with neighbour in no 3, if out." |
| lat         | double | [Optional] latitude of delivery - overrides address                                                              | -3.123                                  |
| lon         | double | [Optional] longitude of delivery                                                                                 | 55.123                                  |

The columns can be presented in any order as long as they follow the order specified in the header. Additional columns will be ignored.

Volunteers:  A variant of customer/visit is a volunteer. You can specify a number of volunteers, they use the same header (but column A should contain "volunteer" . Values for bags and note will be ignored. If volunteers are specified then the solution will allocate rounds to volunteers, optimising to ensure that each volunteer has a route that ends close to their address.

##Summary 
Look at the example CSV files provided to see how the above works in practice.