/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser.io;

import java.io.IOException;
import parser.DependencyInstance;
import parser.DependencyPipe;
import parser.Options;
/**
 *
 * @author disooqi
 */

public abstract class DependencyGraphCreator {
	//BufferedWriter writer;
	Options options;
	String[] labels;
	boolean first, isLabeled;
        int sentence_counter;
      
	
	public static DependencyGraphCreator createDependencyGraph(Options options, DependencyPipe pipe) {
		String format = options.format;
		if (format.equalsIgnoreCase("CONLL06") || format.equalsIgnoreCase("CONLL-06")) {
			return new Conll06GraphCreator(options, pipe);
		} else if (format.equalsIgnoreCase("CONLLX") || format.equalsIgnoreCase("CONLL-X")) {
			return new Conll06GraphCreator(options, pipe);
		} /*else if (format.equalsIgnoreCase("CONLL09") || format.equalsIgnoreCase("CONLL-09")) {
			return new Conll09GraphCreator(options, pipe);
		} */else {
			System.out.printf("!!!!! Unsupported file format: %s%n", format);
			return new Conll06GraphCreator(options, pipe);
		}
	}
	
	public abstract String createGraphForInstance(DependencyInstance inst ) throws IOException;
	
	public void startCreatingGraph(String file) throws IOException {
		//writer = new BufferedWriter(new FileWriter(file));
		first = true;
		isLabeled = options.learnLabel;
                sentence_counter = 0;
	}
	
	public void close() throws IOException {
		//if (writer != null) writer.close();
	}
	
}