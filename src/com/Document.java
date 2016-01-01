package com;

import java.util.List;

public class Document {
	
	private String classification;
	private List<Integer> wordCounts;
	
	public Document(List<Integer> wordCounts, String classification) {
		this.wordCounts = wordCounts;
		this.classification = classification;
	}
	
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	public List<Integer> getWordCounts() {
		return wordCounts;
	}
	public void setWordCounts(List<Integer> wordCounts) {
		this.wordCounts = wordCounts;
	}
}
