#Design Document
Have you ever been curious about what kind of animal that animal is? Take a photo and find out. Then see all the photos you have taken!
Our application supports: Bear, Bird, Bunny, Cat, Dog, Fish, Hamster, Person, Pig and Squirell.

Our application is designed to be an extension of the “petwars” application described throughout the term. 
To accomplish this goal, the team utilized the skills and code we created through Projects 1-4 to create the base of the application that will take a photo and save it to a database. This was also be the foundation for our deep learning. The team split into designated areas (UI, deep learning, database, camera) to equally distribute work and ensure all aspects of the application are covered and completed. The team created the custom deep learning model using Tensorflow and the stanfrod image library, either lite or mobile, as well as Imagenet, the Stanford image database, to train a new model explicitly on animals. 


##How to user our application
1. Click the button at the top and take a photo
2. If you want to do off device inference click on the switch to turn it on
3. Click on Get Type to get the type of animal in the photo
4. Click add to Database if you want to save the photo
5. User the dropdown and select the category you want to view, then click go and see all the photos you've taken in that category

##How we designed the application
There are two activties the application uses and several java classes. The first activity is where the camera, the inference is done and where the images are saved to the database. This was done on the same image as it allowed the user to choose if they wanted to keep the image. A dropdown or spinner was utilized when switching to the second activity. This changed what type of animal was shown on the second activity. The second activity retrieved all images of that type from the database and puts them all in a scroll view. 
(JOAN EXPLAIN DB HERE)
(EXPLAIN THE MODEL AS WELL)