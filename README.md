# This is for a code challenge
I made this in an afternoon. It will generate an image with exactly 32768 distinct colours with whatever custom text you want. It should be smart enough to figure out image dimensions and other things based upon whatever custom text you supply. You can even supply custom themes to vary the colour patterns.

You'll need Apache Maven to build/run this project: https://maven.apache.org/

To build and run tests:

`mvn package`

To run:

`mvn exec:java`

To pass command line params:

~~~~
mvn exec:java "-Dexec.args=--help"
mvn exec:java "-Dexec.args=-text omgsuchtextwow -theme flat"
~~~~
