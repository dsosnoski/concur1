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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Run timed test of finding best matches for misspelled words.
 */
public class ForkJoinDistance extends TimingTestBase
{
    private ForkJoinPool threadPool = new ForkJoinPool();
    
    private final String[] knownWords;
    
    private final int blockSize;
    
    public ForkJoinDistance(String[] words, int block) {
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
        return threadPool.invoke(new DistanceTask(target, 0, knownWords.length, knownWords));
    }
    
    /**
     * Shortest distance task implementation using RecursiveTask.
     */
    public class DistanceTask extends RecursiveTask<DistancePair>
    {
        private final String compareText;
        private final int startOffset;
        private final int compareCount;
        private final String[] matchWords;
        
        /**
         * Constructor.
         * 
         * @param from
         * @param offset
         * @param count
         * @param words
         */
        public DistanceTask(String from, int offset, int count, String[] words) {
            compareText = from;
            startOffset = offset;
            compareCount = count;
            matchWords = words;
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
        private int editDistance(int index, int[] v0, int[] v1) {
            
            // initialize v0 as previous row of distances (starts as edit distance for empty 'word')
            String word = matchWords[index];
            for (int i = 0; i < v0.length; i++) {
                v0[i] = i;
            }
            
            // calculate updated v0 (current row distances) from the previous row v0
            for (int i = 0; i < word.length(); i++) {
                
                // first element of v1 = delete (i+1) chars from target to match empty 'word'
                v1[0] = i + 1;
                
                // use formula to fill in the rest of the row
                for (int j = 0; j < compareText.length(); j++) {
                    int cost = (word.charAt(i) == compareText.charAt(j)) ? 0 : 1;
                    v1[j + 1] = minimum(v1[j] + 1, v0[j + 1] + 1, v0[j] + cost);
                }
                
                // swap v1 (current row) and v0 (previous row) for next iteration
                int[] hold = v0;
                v0 = v1;
                v1 = hold;
            }
            
            // return final value representing best edit distance
            return v0[compareText.length()];
        }
        
        /* (non-Javadoc)
         * @see java.util.concurrent.RecursiveTask#compute()
         */
        @Override
        protected DistancePair compute() {
            if (compareCount > blockSize) {
                
                // split range in half and find best result from bests in each half of range
                int half = compareCount / 2;
                DistanceTask t1 = new DistanceTask(compareText, startOffset, half, matchWords);
                t1.fork();
                DistanceTask t2 = new DistanceTask(compareText, startOffset + half, compareCount - half, matchWords);
                DistancePair p2 = t2.compute();
                return DistancePair.best(p2, t1.join());
            }
            
            // directly compare distances for comparison words in range
            int[] v0 = new int[compareText.length() + 1];
            int[] v1 = new int[compareText.length() + 1];
            int bestIndex = -1;
            int bestDistance = Integer.MAX_VALUE;
            boolean single = false;
            for (int i = 0; i < compareCount; i++) {
                int distance = editDistance(i + startOffset, v0, v1);
                if (bestDistance > distance) {
                    bestDistance = distance;
                    bestIndex = i + startOffset;
                    single = true;
                } else if (bestDistance == distance) {
                    single = false;
                }
            }
            return new DistancePair(bestDistance, single ? matchWords[bestIndex] : null);
        }
        
    }
}