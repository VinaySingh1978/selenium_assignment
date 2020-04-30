Selenium-Assignment - Vinay Rathor
-----------------------------------
To run the project > please run SeleniumAssignmentTest unit test

This can be invoked by maven command also

mvn clean compile test

runAssignment() code does three steps
1. init selenium chrome driver and launch https://www.chick-fil-a.com/locations url

2. Extract 50 location details from the website dynamically (location, address, site, link, state)

3. Export extracted data to CSV file using open csv writer maven library

The output.csv can be found in the root directory of project

I used chrome driver version 81.0.4044.69 which is compatible with my Windows 10 Chrome version 81