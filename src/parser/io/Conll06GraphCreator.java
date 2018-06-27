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

import dependensee.Graph;
import dependensee.DependencyTree;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author disooqi
 */
public class Conll06GraphCreator extends DependencyGraphCreator {

    public Conll06GraphCreator(Options options, DependencyPipe pipe) {

        this.options = options;
        this.labels = pipe.types;
    }

    @Override
    public String createGraphForInstance(DependencyInstance inst) throws IOException {
        String[] forms = inst.forms;
        String[] lemmas = inst.lemmas;
        String[] cpos = inst.cpostags;
        String[] pos = inst.postags;
        int[] heads = inst.heads;
        int[] labelids = inst.deplbids;

        String ar = "\u0627\u0625\u0622\u0623\u0621\u0628\u062a\u062b\u062c\u062d\u062e\u062f\u0630\u0631\u0632\u0633\u0634\u0635\u0636\u0637\u0638\u0639\u063a\u0641\u0642\u0643\u0644\u0645\u0646\u0647\u0648\u064a\u0649\u0629\u0624\u0626\u064e\u064b\u064f\u064c\u0650\u064d\u0652\u0651";
        String buck = "A<|>'btvjHxd*rzs$SDTZEgfqklmnhwyYp&}aFuNiKo~";
        //input = 
        String s = "";
        int i = 0;
        for (String word : inst.forms) {
            //s = StringUtils.replaceChars(word, buck, ar);
            String input = word.replace("A", "\u0627").replace("<", "\u0625").replace("|", "\u0622").replace(">", "\u0623").replace("'", "\u0621");
            input = input.replace("b", "\u0628").replace("t", "\u062a").replace("v", "\u062b").replace("j", "\u062c").replace("H", "\u062d");
            input = input.replace("x", "\u062e").replace("d", "\u062f").replace("*", "\u0630").replace("r", "\u0631").replace("z", "\u0632");
            input = input.replace("s", "\u0633").replace("$", "\u0634").replace("S", "\u0635").replace("D", "\u0636").replace("T", "\u0637");
            input = input.replace("Z", "\u0638").replace("E", "\u0639").replace("g", "\u063a").replace("f", "\u0641").replace("q", "\u0642");
            input = input.replace("k", "\u0643").replace("l", "\u0644").replace("m", "\u0645").replace("n", "\u0646").replace("h", "\u0647");
            input = input.replace("w", "\u0648").replace("y", "\u064a").replace("Y", "\u0649").replace("p", "\u0629").replace("&", "\u0624");
            input = input.replace("}", "\u0626").replace("{", "إ");
            input = input.replace("a", "\u064e").replace("F", "\u064b").replace("u", "\u064f").replace("N", "\u064c");
            input = input.replace("i", "\u0650").replace("K", "\u064d").replace("o", "\u0652").replace("~", "\u0651");
            forms[i++] = input;
        }

        try {
            String image_name = System.currentTimeMillis()+ ".png";
            DependencyTree.writeImage(forms, pos, heads, labelids, labels, "deptrees/"+image_name, 3);
            assert (new File("deptrees/"+image_name).exists());
            return image_name;
        } catch (Exception ex) {
            Logger.getLogger(Conll06GraphCreator.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }


    }

}
