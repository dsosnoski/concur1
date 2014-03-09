/*
 * Copyright (c) 2014, Dennis M. Sosnoski.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.sosnoski.concur.article1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Run timed test of finding best matches for misspelled words.
 */
public class ThreadPoolDistance extends TimingTestBase
{
    private final ExecutorService threadPool;
    
    private final String[] knownWords;
    
    private final int blockSize;
    
    public ThreadPoolDistance(String[] words, int block) {
        threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        knownWords = words;
        blockSize = block;
    }

	@Override
	public void shutdown() {
		threadPool.shutdown();
	}
	
	@Override
	public int blockSize() {
		return blockSize;
	}

    /* (non-Javadoc)
     * @see com.sosnoski.concur1.TimedTest#bestMatch(java.lang.String)
     */
    @Override
    public DistancePair bestMatch(String target) {
        
        // build a list of tasks for matching to ranges of known words
        List<DistanceTask> tasks = new ArrayList<DistanceTask>();
        int size = 0;
        for (int base = 0; base < knownWords.length; base += size) {
            size = Math.min(blockSize, knownWords.length - base);
            tasks.add(new DistanceTask(target, base, size));
        }
        DistancePair best;
        try {
            
            // pass the list of tasks to the executor, getting back list of futures
            List<Future<DistancePair>> results = threadPool.invokeAll(tasks);
            
            // find the best result, waiting for each future to complete
            best = DistancePair.worstMatch();
            for (Future<DistancePair> future: results) {
                DistancePair result = future.get();
                best = DistancePair.best(best, result);
            }
            
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return best;
    }
    
    /**
     * Shortest distance task implementation using Callable.
     */
    public class DistanceTask implements Callable<DistancePair>
    {
        private final String targetText;
        private final int startOffset;
        private final int compareCount;
        
        /**
         * Constructor.
         * 
         * @param target
         * @param offset
         * @param count
         */
        public DistanceTask(String target, int offset, int count) {
            targetText = target;
            startOffset = offset;
            compareCount = count;
        }
        
        protected int minimum(int a, int b, int c) {
            return Math.min(Math.min(a, b), c);
        }
        
        /**
         * Calculate edit distance from targetText to known word.
         *
         * @param word known word
         * @param v0 int array of length targetText.length() + 1
         * @param v1 int array of length targetText.length() + 1
         */
        private int editDistance(String word, int[] v0, int[] v1) {
            
            // initialize v0 (prior row of distances) as edit distance for empty 'word'
            for (int i = 0; i < v0.length; i++) {
                v0[i] = i;
            }
            
            // calculate updated v0 (current row distances) from the previous row v0
            for (int i = 0; i < word.length(); i++) {
                
                // first element of v1 = delete (i+1) chars from target to match empty 'word'
                v1[0] = i + 1;
                
                // use formula to fill in the rest of the row
                for (int j = 0; j < targetText.length(); j++) {
                    int cost = (word.charAt(i) == targetText.charAt(j)) ? 0 : 1;
                    v1[j + 1] = minimum(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost);
                }
                
                // swap v1 (current row) and v0 (previous row) for next iteration
                int[] hold = v0;
                v0 = v1;
                v1 = hold;
            }
            
            // return final value representing best edit distance
            return v0[targetText.length()];
        }

        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public DistancePair call() throws Exception {
            
            // directly compare distances for comparison words in range
            int[] v0 = new int[targetText.length() + 1];
            int[] v1 = new int[targetText.length() + 1];
            int bestIndex = -1;
            int bestDistance = Integer.MAX_VALUE;
            boolean single = false;
            for (int i = 0; i < compareCount; i++) {
                int distance = editDistance(knownWords[i + startOffset], v0, v1);
                if (bestDistance > distance) {
                    bestDistance = distance;
                    bestIndex = i + startOffset;
                    single = true;
                } else if (bestDistance == distance) {
                    single = false;
                }
            }
            return single ? new DistancePair(bestDistance, knownWords[bestIndex]) :
            	new DistancePair(bestDistance);
        }
    }
}