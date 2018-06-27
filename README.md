
### Arabic Dependency Parser

This project provides an Arabic Dependency Parser trained on data processed by [Farasa toolkit](http://farasa.qcri.org) using [MIT RBGParser v1.1](https://github.com/taolei87/RBGParser).
For more details about the RBGParser v1.1 See [Readme.md]](https://github.com/taolei87/RBGParser)

### Use Arabic Dependency Parser

To run the project from the command line, go to the dist folder and
type the following:

	java -jar "RBGParserWrapper.jar" -i <inputFile> -o <outputFile>

For more details about using the Parser:

	java -jar "RBGParserWrapper.jar" -h


### Requirements

The Dependency Parser used [Farasa Arabic Segmenter](http://farasa.qcri.org) and [Farasa Arabic POS Tagger](http://farasa.qcri.org). Download the tools and place the jars "FarasaSegmenterJar.jar" and "FarasaPOSJar.jar" in the "dict/lib" folders.
Otherwise, for building the project from source. Place the jars in "libs" folder




