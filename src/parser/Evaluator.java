package parser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import utils.Utils;

public class Evaluator
{
	int uas, las, tot;
	int whole, nsents;
	
	boolean learnLabel;
	
	public Evaluator(Options options, DependencyPipe pipe)
	{
		uas = las = tot = 0;
		whole = nsents = 0;
		learnLabel = options.learnLabel;
	}
	
	
	public double UAS()
	{
		return uas/(tot+1e-20);
	}
	
	public double LAS()
	{
		return las/(tot+1e-20);
	}
	
	public double CAS()
	{
		return whole/(nsents+1e-20);
	}
	
	
	public void add(DependencyInstance gold, DependencyInstance predict, boolean evalWithPunc)
	{
		evaluateDependencies(gold, predict, evalWithPunc);
	}
	
    public void evaluateDependencies(DependencyInstance gold, 
    		DependencyInstance pred, boolean evalWithPunc) 
    {
    	++nsents;
    	int tt = 0, ua = 0, la = 0;
    	for (int i = 1, N = gold.length; i < N; ++i) {

            if (!evalWithPunc)
            	if (gold.forms[i].matches("[-!\"%&'()*,./:;?@\\[\\]_{}、]+")) continue;

            ++tt;
    		if (gold.heads[i] == pred.heads[i]) {
    			++ua;
    			if (learnLabel && gold.deplbids[i] == pred.deplbids[i]) ++la;
    		}
    	
    	}    		
    	
    	tot += tt;
    	uas += ua;
    	las += la;
    	whole += (tt == ua) && (tt == la || !learnLabel) ? 1 : 0;
    }
    
}
