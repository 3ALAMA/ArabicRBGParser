/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rbgparserwrapper;

import java.io.IOException;
import java.io.FileNotFoundException;
import com.qcri.farasa.segmenter.Farasa;
import com.qcri.farasa.pos.Clitic;
import com.qcri.farasa.pos.FarasaPOSTagger;
import com.qcri.farasa.pos.Sentence;
//import com.qcri.farasa.diacritize.DiacritizeText;
//import com.qcri.farasa.ner.ArabicNER;
import static com.qcri.farasa.segmenter.ArabicUtils.openFileForReading;
import static com.qcri.farasa.segmenter.ArabicUtils.openFileForWriting;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;

import parser.DependencyParser;
import parser.Options;



/**
 *
 * @author aahmed
 */
public class RBGParserWrapper {

    public static String dataDirectory  = "./data/";
    public static Farasa farasa;
    public static FarasaPOSTagger farasaPOS;
    //public static ArabicNER ner;
    //public static DiacritizeText dt;
    public static DependencyParser RBGParser;
    public static String[] RBGs;
    private static ArrayList<String>[] ResData;
    
    
    private static RBGParserWrapper instance;

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, InterruptedException, FileNotFoundException, ClassNotFoundException, Exception
    {
        int i=0;
        String arg;
        String infile="";
        String outfile="";
    
    	RBGs = new String[9];       // = "       ".split(" ");
        RBGs[0] = "label:true";
        RBGs[1] = "thread:2";        
        RBGs[2] = "model:full";        
        RBGs[3] = "model-file:data/arabic.model.farasa.107";  //arabic.model.farasa.107  arabic.model.conll
        RBGs[4] = "test";
        RBGs[5] = "test-file:./data/arabic.test.conllf";
        RBGs[6] = "unimap-file:arabic.uni.map";
        RBGs[7] = "output-file:disooqi4.out";
        RBGs[8] = "graph:false";

        System.out.println("model:"+RBGs[3]);
        
        System.out.print("Initializing the system ....");

        
        while (i < args.length) {
            arg = args[i++];
            // 
            if (arg.equals("--help") || arg.equals("-h") || (args.length!=0 && args.length!=4)) {
                System.out.println("Usage: RBGParserWrapper  <--help|-h> <[-i|--input] [in-filename]> <[-o|--output] [out-filename]>");
                System.exit(-1);
                } 
            
            if (arg.equals("--input") || arg.equals("-i")) {
                                
                                infile = args[i];
                }
            if (arg.equals("--output") || arg.equals("-o")) {
                                
                                outfile = args[i];
                }       
       
        }


        farasa = new Farasa();
        farasaPOS = new FarasaPOSTagger(farasa);
        //ner = new ArabicNER(farasa, farasaPOS);
        //dt = new DiacritizeText(farasa, farasaPOS);

        Options options = new Options();
        options.processArguments(RBGs);
        DependencyParser pruner = null;
        DependencyParser parser = new DependencyParser();
        parser.options = options;	
        parser.loadModel();
        parser.options.processArguments(RBGs);

        System.out.print("\r");
        System.out.println("System ready!               ");
        String line = "";
        ArrayList<String> segmentedline;
        Sentence POSline;
        ArrayList<String> conll06txt;
        ArrayList<String> output = new ArrayList<String>();
        ArrayList<String> POSres = new ArrayList<String>();

        BufferedReader ffile = openFileForReading(infile);
        BufferedWriter bw = openFileForWriting(outfile);

        while ((line = ffile.readLine()) != null)
        //for (String line = ffile.readLine(); line != null; line = ffile.readLine()) 
        {
            //System.err.println("Processing line:"+line+"\n--------------------");
            segmentedline = callSegmenter(line, farasa);
            POSline = callPOSTagger(segmentedline,farasaPOS);
            POSres = formatPOS(POSline);
            conll06txt = conll06_format(POSline,segmentedline,farasa);
            output = parser.classifySentences(true, conll06txt);

            int idx = 0;
            for(String s : output){
                
                bw.write(s);
                if(s.length()>0){
                    bw.write("\t"+POSres.get(idx++)+"\n");
                    //System.out.println(s+" = "+POSres.get(idx-1));
                } else{
                     bw.write("\n");
                }
            }
        }

        ffile.close();
        bw.flush();
        bw.close();
        System.exit(0);
	
    }

    
    public static synchronized RBGParserWrapper getInstance() throws Exception{
        if (instance == null) {
            instance = new RBGParserWrapper();
        }
        return instance;
    }
    
    
    public static ArrayList<String> processLine(String line) throws IOException, Exception{
        ArrayList<String> results = new ArrayList<String>();
        ArrayList<String> segmentedLine = farasa.segmentLine(line);
        Sentence POSLine = farasaPOS.tagLine(segmentedLine);
        ArrayList<String> formatLine =  conll06_format(POSLine,segmentedLine,farasa);
        ArrayList<String> output = RBGParser.classifySentences(true, formatLine);
        int idx = 0;
            for(String s : output){
                if(s.length()>0){
                results.add(s+","+formatLine.get(idx++));
                }
            }
        return results;
    }
    
    private static ArrayList<String> callSegmenter(String line, Farasa tagger) throws IOException{
        return tagger.segmentLine(line);
    }
    
    private static Sentence callPOSTagger(ArrayList<String> segmentedLine, FarasaPOSTagger tagger) throws Exception{
        return tagger.tagLine(segmentedLine);
    }
    
    private static ArrayList<String> formatPOS(Sentence sentences){

        String line = "";
        String StrPOS = "";
        String StrSeg = "";
              
        ArrayList<String> results = new ArrayList<String>();
        
        for(Clitic c : sentences.clitics){
            if (c.surface.equals("S")) {
                continue;
            }else if(c.surface.equals("E") && c.position.equals("B")){
                if(StrPOS.length()!=0)
                    results.add(StrSeg+"\t"+StrPOS);
                else
                    continue;
            }else if(c.position.equals("B") && StrPOS.length()!=0){
                results.add(StrSeg+"\t"+StrPOS);
                StrSeg = c.surface;
                StrPOS = c.guessPOS;
            } else if(c.position.equals("B") && StrPOS.length()==0){
                //results.add(c.surface+"\t"+c.guessPOS);
                StrSeg = c.surface;
                StrPOS = c.guessPOS;    
            } else if(c.position.equals("I")){
                StrSeg +=" "+c.surface;
                StrPOS +="+"+c.guessPOS;    
            }
        }
        
        return results;
    }
    private static ArrayList<String> conll06_format(Sentence sentence, ArrayList<String> segmentedWords, Farasa farasa) {

        String line = "";
        int word_count = 0;
        HashMap RBGline = new HashMap();
        HashMap feats = new HashMap();
        String affixes_pos = "";
        ArrayList<String> sentences = new ArrayList<String>();
        

        for (Clitic c : sentence.clitics) {
            if (c.surface.equals("S")) {
                continue;
            } else if (c.surface.equals("E") && c.position.equals("B")) {
                String f_line = "det=" + feats.get("det") + "|" + "gen=" + feats.get("gen") + "|" + "num=" + feats.get("num");
                if (RBGline.get("LEMMA") == null) {
                    line = RBGline.get("ID") + "\t" + RBGline.get("FORM") + "\t" + farasa.lemmatizeLine(RBGline.get("FORM").toString()).get(0)  + "\t"
                            + affixes_pos + "\t" + affixes_pos + "\t" + f_line;
                } else {
                    line = RBGline.get("ID") + "\t" + RBGline.get("FORM") + "\t" + farasa.lemmatizeLine(RBGline.get("LEMMA").toString()).get(0) + "\t"
                            + RBGline.get("CPOSTAG") + "\t" + RBGline.get("POSTAG") + "\t" + f_line;
                }
                sentences.add(line.replace("DET+","").replace("+NSUFF","").replace("CONJ+","").replace("PREP+","").replace("+PRON", ""));

                sentences.add("");
                continue;
            }

            if (c.position.equals("B")) {
                if (word_count > 0) {
                    if (feats.get("det") == null) {
                        feats.put("det", "n");
                    }

                    String f_line = "det=" + feats.get("det") + "|" + "gen=" + feats.get("gen") + "|" + "num=" + feats.get("num");
                    if (RBGline.get("LEMMA") == null) {
                        line = RBGline.get("ID") + "\t" + RBGline.get("FORM") + "\t" + farasa.lemmatizeLine(RBGline.get("FORM").toString()).get(0)  + "\t"
                                + affixes_pos + "\t" + affixes_pos + "\t" + f_line;
                    } else {
                        line = RBGline.get("ID") + "\t" + RBGline.get("FORM") + "\t" + farasa.lemmatizeLine(RBGline.get("LEMMA").toString()).get(0) + "\t"
                                + RBGline.get("CPOSTAG") + "\t" + RBGline.get("POSTAG") + "\t" + f_line;
                    }

                    sentences.add(line.replace("DET+","").replace("+NSUFF","").replace("CONJ+","").replace("PREP+","").replace("+PRON", ""));
                    if (RBGline.get("FORM").equals(".") || RBGline.get("FORM").equals("!") || RBGline.get("FORM").equals("?")) {
                        sentences.add("");
                    }
                    //System.out.println(line);
                }

                RBGline = new HashMap();
                feats = new HashMap();
                feats.put("det", "n");
                feats.put("gen", "na");
                feats.put("num", "na");
                affixes_pos = "";

                RBGline.put("ID", ++word_count);
                RBGline.put("FORM", segmentedWords.get(word_count - 1).replace("+", ""));

                if (c.isStem.equals("y")) {
                    RBGline.put("LEMMA", farasa.lemmatizeLine(c.surface.replace("+", "").replace(" ","")).get(0)); //c.surface.replace("+", "") 
                    RBGline.put("CPOSTAG", c.guessPOS);
                    RBGline.put("POSTAG", c.guessPOS);
                    if (!c.genderNumber.isEmpty()) {
                        feats.put("gen", c.genderNumber.charAt(0));
                        feats.put("num", c.genderNumber.charAt(1));
                    } else {
                        feats.put("gen", "na");
                        feats.put("num", "na");
                    }
                } else {
                    affixes_pos = c.guessPOS;
                }

                if (c.det.equals("y")) {
                    feats.put("det", "y");
                }
            } else {
                if (c.isStem.equals("y")) {
                    RBGline.put("LEMMA", farasa.lemmatizeLine(c.surface.replace("+", "").replace(" ","")).get(0)); //c.surface.replace("+", "")
                    RBGline.put("CPOSTAG", c.guessPOS);
                    RBGline.put("POSTAG", c.guessPOS);
                    if (!c.genderNumber.isEmpty()) {
                        feats.put("gen", c.genderNumber.charAt(0));
                        feats.put("num", c.genderNumber.charAt(1));
                    } else {
                        feats.put("gen", "na");
                        feats.put("num", "na");
                    }
                } else {
                    affixes_pos += "+" + c.guessPOS;
                }

                if (c.det.equals("y")) {
                    feats.put("det", "y");
                }
            }
        }
        return sentences;
    }
    
}
