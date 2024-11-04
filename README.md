# Exam

## 2.3
I have unfortunately reversed the OneToMany & OnToMany relationship between Guide and Trip. I misunderstood and thought 1 trip could have multiple guides. I don't have enough time to refactor it all

## 3.3.2  Test the endpoints using a dev.http file. Document the output in your README.md file to verify the functionality.
Im encountering this error:
Error: Could not find or load main class dat.Main
Caused by: java.lang.ClassNotFoundException: dat.Main

with this error im not able to run anything on my project, i have tried creating a new project, renaming my project & much more. So now im just trying to write the code and hope its correct
I have had 0 issues with my template when running it on the other exam projects we have used for preperation.

## 3.3.3 As a minimum you should request all endpoints once to get all trips, get a trip by id, adding a trip, updating a trip, and delete a trip. Also add a guide to a trip. For each request, document the response in your README.md file by copying the response
Same issue as 3.3.2

## 3.3.5 Theoretical question: Why do we suggest a PUT method for adding a guide to a trip instead of a POST method? Write the answer in your README.md file
We are using a PUT instead of a POST, since we are not creating a new trip or guide, we are updating an existing trip with a guide.

## 8.3 Adding security roles to the endpoints will make the corresponding Rest Assured Test fail. Now the request will return a 401 Unauthorized response. Describe how you would fix the failing tests in your README.md file, or if time permits, implement the solution so your tests pass
I have fixed the issue, by adding the correct security roles to the endpoints. I have also created a new test class, where i have created a user and admin, where everytime a test is ran, either a user or admin is logged in, and the tests are ran. This way i can test if the security roles are working as intended.

## I HAVE NOT BEEN ABLE TO FIX MY EARLIER ERROR WITH THE MAIN CLASS, SO I HAVE NOT BEEN ABLE TO TEST MY ENDPOINTS, SO I HAVE NOT BEEN ABLE TO DOCUMENT THE RESPONSES. I HAVE NEVER ECOUNTERED SAID ERROR, BUT I MUST HAVE MISSED SOMETHING IN MY TEMPLATE
