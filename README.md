
### Arabic Dependency Parser

This project provides an Arabic Dependency Parser trained on data processed by [Farasa toolkit](http://farasa.qcri.org) using [MIT RBGParser v1.1](https://github.com/taolei87/RBGParser).
For more details about the RBGParser v1.1 See [Readme.md](https://github.com/taolei87/RBGParser)

### Use Arabic Dependency Parser

To run the project from the command line, go to the dist folder and
type the following:

	java -jar "RBGParserWrapper.jar" -i <inputFile> -o <outputFile>

For more details about using the Parser:

	java -jar "RBGParserWrapper.jar" -h


### Requirements

The Dependency Parser uses [Farasa Arabic Segmenter](http://farasa.qcri.org) and [Farasa Arabic POS Tagger](http://farasa.qcri.org). Download the tools and place the jars "FarasaSegmenterJar.jar" and "FarasaPOSJar.jar" in the "./dist/lib" folders.
Otherwise, for building the project from source. Place the jars in "./libs" folder


### Contact

If you have any inquiry, please contact <Mohamed Eldesouki>(mohamoha@hbku.edu.qa) <Kareem Darwish>(kdarwish@hbku.edu.qa) or <Ahmed Abdelali>(aabdelali@hbku.edu.qa).


### Web Site

URL for the project  and the latest news  and downloads
	http://farasa.qcri.org/


### Github

Where to download the latest version, 
	https://github.com/qcri/Farasa


### License

    QCRI FARASA package for processing Arabic text is being made 
    public for research purpose only. 
    For non-research use, please contact:
    
        Kareem M. Darwish <kdarwish@hbku.edu.qa>
        Hamdy Mubarak <hmubarak@hbku.edu.qa>
        Ahmed Abdelali <aabdelali@hbku.edu.qa>
    
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  


COPYRIGHT
----------------------------
Copyright 2015-2018 QCRI





