package parser.decoding;

import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


import parser.DependencyArcList;
import parser.DependencyInstance;
import parser.GlobalFeatureData;
import parser.LocalFeatureData;
import parser.Options;
import parser.sampling.RandomWalkSampler;
import utils.Utils;

public class HillClimbingDecoder extends DependencyDecoder {
	
	DependencyInstance pred, inst;
	LocalFeatureData lfd;
	GlobalFeatureData gfd;
	boolean addLoss;
	final int labelLossType;
	
	//int[][] staticTypes;
	
	double bestScore;	
	int unchangedRuns, totRuns;
	volatile boolean stopped;
    
    ExecutorService executorService;
	ExecutorCompletionService<Object> decodingService;
	HillClimbingTask[] tasks;
	
	public HillClimbingDecoder(Options options) {
		this.options = options;
		labelLossType = options.labelLossType;
        executorService = Executors.newFixedThreadPool(options.numHcThreads);
		decodingService = new ExecutorCompletionService<Object>(executorService);
		tasks = new HillClimbingTask[options.numHcThreads];
		for (int i = 0; i < tasks.length; ++i) {
			tasks[i] = new HillClimbingTask();
			tasks[i].id = i;
			tasks[i].sampler = new RandomWalkSampler(i, options);
		}
	}
   
   @Override
    public void shutdown()
    {
        //System.out.println("shutdown");
        executorService.shutdownNow();
    }

	@Override
	public DependencyInstance decode(DependencyInstance inst,
			LocalFeatureData lfd, GlobalFeatureData gfd, boolean addLoss) {
		this.inst = inst;
		this.lfd = lfd;
		this.gfd = gfd;
		this.addLoss = addLoss;
		bestScore = Double.NEGATIVE_INFINITY;
		pred = new DependencyInstance(inst);
		totRuns = 0;
		unchangedRuns = 0;
		stopped = false;
		//if (options.learnLabel)
		//	staticTypes = lfd.getStaticTypes();
		
		for (int i = 0; i < tasks.length; ++i) {
			decodingService.submit(tasks[i], null);			
		}
		
		for (int i = 0; i < tasks.length; ++i) {
			try {
				decodingService.take();
			} catch (InterruptedException e) {
				System.out.println("Hill climbing thread interupted!!!!");
			}
		}
		
		//if (options.learnLabel)
		//	lfd.predictLabels(pred.heads, pred.deplbids, addLoss);
		
		return pred;		
	}
	
	public class HillClimbingTask implements Runnable {
		
		public int id;
		
		RandomWalkSampler sampler;
		int converge;
	
		int n, size;
		int[] dfslis;
		DependencyArcList arcLis;
				
		@Override
		public void run() {
			
			n = inst.length;
			converge = addLoss ? options.numTrainConverge : options.numTestConverge;
			
			if (dfslis == null || dfslis.length < n) {
				dfslis = new int[n];				
			}
			if (arcLis == null)
				arcLis = new DependencyArcList(n, options.useHO);
			else
				arcLis.resize(n, options.useHO);
			
			while (!stopped) {
				
				DependencyInstance now = sampler.randomWalkSampling(
						inst, lfd, addLoss);
				
				// hill climb
				int[] heads = now.heads;
				int[] deplbids = now.deplbids;
			    
                int cnt = 0;
				boolean more;
				for (;;) {
					more = false;
					depthFirstSearch(heads);
					Utils.Assert(size == n-1);
					for (int i = 0; i < size; ++i) {
						int m = dfslis[i];
						
						int bestHead = heads[m];
						double maxScore = calcScore(heads, m, arcLis);
						//double maxScore = calcScore(now);
						
						int lastHead = heads[m];
						for (int h = 0; h < n; ++h)
							if (h != m && h != bestHead && !lfd.isPruned(h, m)
								&& !isAncestorOf(heads, m, h)) {
								heads[m] = h;
								arcLis.update(m, lastHead, h, heads);
								lastHead = h;
								double score = calcScore(heads, m, arcLis);
								//double score = calcScore(now);
								if (score > maxScore) {
									more = true;
									bestHead = h;
									maxScore = score;									
								}
							}
						heads[m] = bestHead;
						arcLis.update(m, lastHead, bestHead, heads);
					}
					if (!more) break;					

                    //DEBUG
                    //++cnt;
                    //if (cnt % 100 == 0) {
                        //System.out.println(cnt);
                    //}
				}
				
				//if (options.learnLabel) {
				//	for (int m = 1; m < n; ++m)
				//		deplbids[m] = staticTypes[heads[m]][m];
				//}
				
				double score = calcScore(now);
				synchronized (pred) {
					++totRuns;
					if (score > bestScore) {
						bestScore = score;
						unchangedRuns = 0;
						pred.heads = heads;
						pred.deplbids = deplbids;
					} else {
						++unchangedRuns;
						if (unchangedRuns >= converge)
							stopped = true;
					}
					
				}
			}
		}
		
		private boolean isAncestorOf(int[] heads, int par, int ch) 
		{
            //int cnt = 0;
			while (ch != 0) {
				if (ch == par) return true;
				ch = heads[ch];

                //DEBUG
                //++cnt;
                //if (cnt > 10000) {
                //    System.out.println("DEAD LOOP in isAncestorOf !!!!");
                //    System.exit(1);
                //}
			}
			return false;
		}
		
		private double calcScore(int[] heads, int m, DependencyArcList arcLis)
		{
			double score = lfd.getPartialScore2(heads, m, arcLis)
						 + gfd.getScore(heads);
//			if (options.learnLabel) {
//				int t = staticTypes[heads[m]][m];
//				score += lfd.getLabeledArcScore(heads[m], m, t);
//				if (addLoss) {
//					if (labelLossType == 0) {
//						if (heads[m] != inst.heads[m]) score += 0.5;
//						if (t != inst.deplbids[m]) score += 0.5;
//					} else if (heads[m] != inst.heads[m] || t != inst.deplbids[m])
//						score += 1.0;
//				}				
//			} 
//			else 
			  if (addLoss && heads[m] != inst.heads[m])
				score += 1.0;
			
			return score;
		}
		
		private double calcScore(DependencyInstance now) 
		{
			double score = 0;
			int[] heads = now.heads;
			int[] deplbids = now.deplbids;
			for (int m = 1; m < n; ++m)
//				if (options.learnLabel) {
//					int t = deplbids[m];
//					score += lfd.getLabeledArcScore(heads[m], m, t);
//					if (addLoss) {
//						if (labelLossType == 0) {
//							if (heads[m] != inst.heads[m]) score += 0.5;
//							if (t != inst.deplbids[m]) score += 0.5;
//						} else if (heads[m] != inst.heads[m] || t != inst.deplbids[m])
//							score += 1.0;
//					}
//				} else 
				  if (addLoss && heads[m] != inst.heads[m])
					score += 1.0;			 
			score += lfd.getScore(now);
			score += gfd.getScore(now);	
			return score;
		}
		
		private void depthFirstSearch(int[] heads)
		{
			arcLis.constructDepTreeArcList(heads);
			if (arcLis.left != null && arcLis.right != null)
				arcLis.constructSpan();
			if (arcLis.nonproj != null)
				arcLis.constructNonproj(heads);
			size = 0;
            //dfscnt = 0;
			dfs(0);
		}
		
		
		private void dfs(int i)
		{
            //DEBUG
            //++dfscnt;
            //if (dfscnt > 10000) {
            //    System.out.println("DEAD LOOP in dfs!!!!");
            //    System.exit(1);
            //}

			int l = arcLis.startIndex(i);
			int r = arcLis.endIndex(i);
			for (int p = l; p < r; ++p) {
				int j = arcLis.get(p);
				dfs(j);
				dfslis[size++] = j;
			}
		}
		
	}
	
}
