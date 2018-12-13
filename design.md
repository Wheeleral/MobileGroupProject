# Design Document
Have you ever been curious about what kind of animal that animal is? Take a photo and find out. Then see all the photos you have taken!
Our application supports: Bear, Bird, Bunny, Cat, Dog, Fish, Hamster, Person, Pig and Squirrel.

Our application is designed to be an extension of the “petwars” application described throughout the term. 
To accomplish this goal, the team utilized the skills and code we created through Projects 1-4 to create the base of the application that will take a photo and save it to a database. This was also be the foundation for our deep learning. The team split into designated areas (UI, deep learning, database, camera) to equally distribute work and ensure all aspects of the application are covered and completed. The team created the custom deep learning model using Tensorflow and the stanford image library, either lite or mobile, as well as Imagenet, the Stanford image database, to train a new model explicitly on animals. 


## How to use our application
1. Click the camera button and take a photo
2. If you want to do off device inference click on the switch to turn on cloud inference
3. Click on Get Type to get the type of animal in the photo
4. Click add to Database if you want to save the photo
5. Use the dropdown and select the category you want to view, then click go and see all the photos you've taken in that category

## How we designed the application
There are two activities the application uses and several java classes. The first activity is where the camera, the inference is done and where the images are saved to the database. This was done on the same image as it allowed the user to choose if they wanted to keep the image. A dropdown or spinner was utilized when switching to the second activity. This changed what type of animal was shown on the second activity. The second activity retrieved all images of that type from the database and puts them all in a scroll view. 


## Database
Our team used SQLite to persistently store all the model inference results and their corresponding images taken by the user. Images will be stored to external storage of the application prior to saving to the database; therefore, the database only needs to store file paths to these images on the user's device. The user can then populate
this database with the images he or she has taken since the launch of the application. Furthermore, the database is implemented such that only one instance will be used throughout the lifetime of our application.
By doing so, it avoids unnecessary, redundant initialization of database classes and objects and avoids repeatedly loading default images to the database (our team had prepared this for the user to build off of), making our code more organized for the team to carry out other tasks.

Because reading and writing to the database is costly, to optimize performance and for faster response rate to user's requests, out team created
a caching mechanism for image retrieval. In our case, however, we need to organize all bitmaps by category, and therefore, our application not only need to keep track of the individual bitmaps currently cached but also the categories in which
these bitmaps belong to. As a result, our team decided to implement two LruCaches - one to cache individual bitmaps (key, bitmap) and the other to cache the categories of those bitmaps (category, list of keys to the bitmaps stored in the other cache).

Please note: Our current implementation of the application reads in all the bitmaps that are cached, avoiding to send query requests to the database for all the images of a specific category. Therefore, the number of images displayed is 
dependent upon the number of bitmaps that are currently cached.


## Custom Tensorflow Model
The model was created using the tensorflow python libraries and ImageNet image database. 10 animal categories were chosen to be made into this model: bear, bird, bunny, cat, dog, fish, hamster, person, pig, squirrel.
The model was trained with around 7 thousand images total and trained for 5000 steps. this lead to an average accuracy of 84.7% after training. we then imported this mobilenets0.50_224 trained model into the application. 
Unfortunately the team discovered the model may have overfitted, and in turn is more likely to recognize animals as birds or people. this is an issue with how tensorflow trains and can be remeidied in the future by using better
training pictures and testing different amounts of training step. The model does work well off device, however latency is an issue. Overall this custom model was a great exercise in further unserstanding machine learning and benfits
the app's purpose well. 

The team chose to do the custom model to learn more about deep learning and to ensure the app would focus on detecting animals and not recognize household items. 

## Deploying Model to Cloud
There are two options for which the user may choose from:
1. Performing the inference operation on-device - which will be faster but slightly less accurate as shown in our analysis
2. Performing the inference operation off-device - which will be slower (for establishing connection to our server and sending requests for a prediction) but more accurate
To be able to perform the inference operations off-device, we deployed our pretrained model to a Python Flask server on Google Cloud Services, providing web API services of the model to our users.

The team chose to create a server and run the model in the cloud because there are more resources and computational power avaliable in the cloud than our personal mobile device can provide for 
better, more accurate predictions (and/or for further training of our model).