author: Alex Hard, Erika Snow, Alexandra Wheeler, Joan Wong  
summary: CS4518 Tutorial: Optimizing Tensorflow Model  
id: arcoreTutorial  
categories: common  
environment: markdown  
status: draft  


# CS4518 Tutorial: Optimizing Tensorflow Model  

## Initial Training
In order to complete this tutorial, you should complete the steps in this codelab for training a custom tensorflow model: https://codelabs.developers.google.com/codelabs/tensorflow-for-poets/#0

## Changing the training base model
When traning a new model with tensorflow, you can train it from scratch, which would take days of heavy computing, or you can train it off an existing model, which takes about an hour. 

In the original codelab (step 4) you set the model before training the script. to train on a lower accuracy lower latency model, set the following vaules before running the script:

'IMAGE_SIZE=128'  
'ARCHITECTURE="mobilenet_0.25_${IMAGE_SIZE}"'

for a high accuracy model run these values: 
 
'IMAGE_SIZE=224'  
'ARCHITECTURE="mobilenet_1.0_${IMAGE_SIZE}"'

then you may run the script to train the models. Tensorflow will download the new model and train the images.

then you must convert the model to a tflite model, which can be down by following this codelab: https://codelabs.developers.google.com/codelabs/tensorflow-for-poets-2-tflite/#0

## Integrating the model

After you have trained the new models, you must integrate them into the application. To do so is easy. 

In MainActivty update

'int SIZE_X'  
'int SIZE_Y'  
to match the values of 'IMAGE_SIZE' you set when traning the model. Similarly in 'runinference()' you must change the parameters inside 'imageTaken.createScaledBitmap()' to match the new resolution.

then you must update the model used in the code. to do so in Mian Activity go to ' getModelPath()' and changed the returned filepath to the new location of your optimized model.

and with that you're all set to use the new model!