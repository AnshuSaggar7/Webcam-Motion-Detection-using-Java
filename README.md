# YOBBY

This sample code demonstrates how to use Java with JMF to implement a motion detection engine. It is a simple engine that displays the captured frames on the screen with overlayed text. You need to install Java 1.2 (or a higher version) and JMF (supported cameras) to use this. First compile the source code: 

javac *.java 

To run, do:

java TestMotionDetection vfw://0 

The vfw://0 is the URL for your camera. You can find out which URL you camera has by running JMFRegistry and looking for the value of JMF Registry Locator.


[![Deploy to Bluemix](https://bluemix.net/deploy/button.png)](https://bluemix.net/deploy?repository=https://github.com/qwerty123456789012/YOBBY)
