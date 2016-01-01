package com;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Model {
	
	private int numberOfTrainingDocuments;
	private Map<String, List<AtomicInteger>> wordCountsPerClass = new HashMap<>();	
	private Map<String, AtomicInteger> classCounts;
	private Map<String, AtomicInteger> totalWordsPerClass;
	
	public int getNumberOfTrainingDocuments() {
		return numberOfTrainingDocuments;
	}
	public void setNumberOfTrainingDocuments(int numberOfTrainingDocuments) {
		this.numberOfTrainingDocuments = numberOfTrainingDocuments;
	}
	public Map<String, List<AtomicInteger>> getWordCountsPerClass() {
		return wordCountsPerClass;
	}
	public void setWordCountsPerClass(Map<String, List<AtomicInteger>> wordCountsPerClass) {
		this.wordCountsPerClass = wordCountsPerClass;
	}
	public Map<String, AtomicInteger> getClassCounts() {
		return classCounts;
	}
	public void setClassCounts(Map<String, AtomicInteger> classCounts) {
		this.classCounts = classCounts;
	}
	public Map<String, AtomicInteger> getTotalWordsPerClass() {
		return totalWordsPerClass;
	}
	public void setTotalWordsPerClass(Map<String, AtomicInteger> totalWordsPerClass) {
		this.totalWordsPerClass = totalWordsPerClass;
	}
	

}
