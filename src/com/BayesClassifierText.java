package com;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BayesClassifierText {

	private Evidence evidence = new Evidence();
	private Model model = new Model();

	@SuppressWarnings("serial")
	private void initializeEvidence() {
		
		// Initialize the names of the classes.
		List<String> classNames = new LinkedList<String>() {{
			add("terrorism");
			add("entertainment");
		}};		
		evidence.setClasses(classNames);
		
		// Initialize attribute names.
		List<String> attributeNames = new LinkedList<String>() {{
			add("kill"); add("bomb"); add("kidnap"); add("music"); add("movie"); add("tv");
		}};		
		evidence.setAttributeNames(attributeNames);
		
		// Set evidence.
		evidence.getEvidence().add(getEvidenceLine1());
		evidence.getEvidence().add(getEvidenceLine2());
		evidence.getEvidence().add(getEvidenceLine3());
		evidence.getEvidence().add(getEvidenceLine4());
		evidence.getEvidence().add(getEvidenceLine5());
		evidence.getEvidence().add(getEvidenceLine6());
	}
	
	@SuppressWarnings("serial")
	private Document getEvidenceLine1() {
		List<Integer> line1 = new LinkedList<Integer>() {{
			add(2); add(1); add(3); add(0); add(0); add(1);
		}};		
		return new Document(line1, "terrorism");
	}
	@SuppressWarnings("serial")
	private Document getEvidenceLine2() {
		List<Integer> line1 = new LinkedList<Integer>() {{
			add(1); add(1); add(1); add(0); add(0); add(0);
		}};		
		return new Document(line1, "terrorism");
	}
	@SuppressWarnings("serial")
	private Document getEvidenceLine3() {
		List<Integer> line1 = new LinkedList<Integer>() {{
			add(1); add(1); add(2); add(0); add(1); add(0);
		}};		
		return new Document(line1, "terrorism");
	}
	@SuppressWarnings("serial")
	private Document getEvidenceLine4() {
		List<Integer> line1 = new LinkedList<Integer>() {{
			add(0); add(1); add(0); add(2); add(1); add(1);
		}};		
		return new Document(line1, "entertainment");
	}
	@SuppressWarnings("serial")
	private Document getEvidenceLine5() {
		List<Integer> line1 = new LinkedList<Integer>() {{
			add(0); add(0); add(1); add(1); add(1); add(0);
		}};		
		return new Document(line1, "entertainment");
	}
	@SuppressWarnings("serial")
	private Document getEvidenceLine6() {
		List<Integer> line1 = new LinkedList<Integer>() {{
			add(0); add(0); add(0); add(2); add(2); add(0);
		}};		
		return new Document(line1, "entertainment");
	}
	
	private void printEvidence() {
		System.out.print("Doc  ");
		for (String attributeName : evidence.getAttributeNames()) {
			System.out.printf("%4s    ", attributeName);
		}
		System.out.println("      classification");
		
		for (int i = 0; i < evidence.getEvidence().size(); i++) {
			Document e = evidence.getEvidence().get(i);
			
			System.out.printf("%2d  ", i);
			
			for (int j = 0; j < e.getWordCounts().size(); j++) {
				System.out.printf("%4d     ", e.getWordCounts().get(j));
			}
			
			System.out.print(" --- ");
			System.out.println(e.getClassification());
		}
	}	
	
	
	private void buildModel() {
		
		calculateClassificationsPriorProbabilities();		
		createCountersForEachClassification();					
		createAttributeCounts();		
	}
	
	
	private void calculateClassificationsPriorProbabilities() {
		// Calculate the probability of each class.
		// Create map to hold counts.
		Map<String, AtomicInteger> classCounts = new HashMap<>();
		for (String className : evidence.getClasses()) {
			classCounts.put(className, new AtomicInteger(0));
		}
		// Count number of events in each classification.
		for (int e = 0; e < evidence.getEvidence().size(); e++) {
			Document eLine = evidence.getEvidence().get(e);
			String className = eLine.getClassification();
			classCounts.get(className).incrementAndGet();	
		}
		
		// Put in model.
		model.setNumberOfTrainingDocuments(evidence.getEvidence().size());
		model.setClassCounts(classCounts);
	}
	
	private void printTotalNumberOfEvents() {
		System.out.println("Total number of training documents: " + model.getNumberOfTrainingDocuments());
	}
	
	private void createCountersForEachClassification() {
		// Create counter lists for each classification.
		for (String classificationName : evidence.getClasses()) {
			// Create an attribute list for this classification.
			List<AtomicInteger> attributeList = new LinkedList<AtomicInteger>();
			
			for (int attrIndex = 0; attrIndex < evidence.getAttributeNames().size(); attrIndex++) {
								
				// Put count count in list.
				attributeList.add(new AtomicInteger(0));
			}
			
			// Save list of counts in model.
			model.getWordCountsPerClass().put(classificationName,  attributeList);
		}
		
		// Create count of total words per class.
		Map<String, AtomicInteger> totalWordsPerClass = new HashMap<>();
		for (String classificationName : evidence.getClasses()) {
			totalWordsPerClass.put(classificationName, new AtomicInteger(0));
		}
		model.setTotalWordsPerClass(totalWordsPerClass);
	}
	
	public void createAttributeCounts() {
		// For each attribute.
		for (int a = 0; a < evidence.getAttributeNames().size(); a++) {
			
			// For each line of evidence.
			for (int e = 0; e < evidence.getEvidence().size(); e++) {
				// Get evidence line.
				Document eLine = evidence.getEvidence().get(e);
				
				// Get the classification for this line of evidence.
				String className = eLine.getClassification();
				
				// Get the list for this class.
				List<AtomicInteger> attributeList = model.getWordCountsPerClass().get(className);
													
				// Get the value for this attribute.
				AtomicInteger attributeValueCount = attributeList.get(a);
				
				// Get word counts for this word on this document.
				int wordCount = eLine.getWordCounts().get(a);
				
				// add count.
				attributeValueCount.addAndGet(wordCount);
				
				// Add word count to total words for this class.
				
				model.getTotalWordsPerClass().get(className).addAndGet(wordCount);
			}				
		}
	}
	
	private void printClassificationCounts() {
		for (String key : model.getClassCounts().keySet()) {
			System.out.println(key + ": " + model.getClassCounts().get(key));
		}
		System.out.println();
	}
	
	private void printAttributeCounts() {
		for (String className : model.getClassCounts().keySet()) {
			System.out.println(className + ": " + model.getClassCounts().get(className));
			System.out.println("Number of total words in class: " + model.getTotalWordsPerClass().get(className));
			
			// Get the list for this class.
			List<AtomicInteger> attributeList = model.getWordCountsPerClass().get(className);

			for (int i = 0; i < attributeList.size(); i++) {
				System.out.println(
						evidence.getAttributeNames().get(i) + 
						", count: " + attributeList.get(i).intValue());
			}
			
			System.out.println();
		}
	}
	
	private List<ClassificationResult> classify(List<Integer> values) {
		
		List<ClassificationResult> classificationList = new LinkedList<>();
		
		double totalLikelihood = 0.0;
		
		// Calculate the likelihood for each classification.
		for (String className : model.getClassCounts().keySet()) {
			
			// Initialize likelihood to prior probability of the classification.
			double likelihood = (double) model.getClassCounts().get(className).intValue() 
					/ 
					(double) model.getNumberOfTrainingDocuments();
			
			// Get the attribute list for this classification.
			List<AtomicInteger> attributeList = model.getWordCountsPerClass().get(className);
			
			// Get number of words in this class.
			int numberOfWordsInClass = model.getTotalWordsPerClass().get(className).intValue();
						
			// Get size of vocabulary.
			int vocabSize = evidence.getAttributeNames().size();

			// Multiply the probability of each attribute.
			for (int i = 0; i < attributeList.size(); i++) {
				
				// Get the word count for this attribute in this class.
				int wordCount = attributeList.get(i).intValue();
				
				// Compute probability with Laplace smoothing.
				double wordProbabilityGivenClass = (double) (wordCount + 1) 
						/ 
						(double) (numberOfWordsInClass + vocabSize);
								
				// Compute likelihood.
				for (int j = 0; j < values.get(i); j++) {
					likelihood *= wordProbabilityGivenClass;
				}
			}
			
			// Total the likelihood.
			totalLikelihood += likelihood;
			
			// Add the classification result.
			ClassificationResult cr = new ClassificationResult();
			cr.setClassificationName(className);
			cr.setLikelihood(likelihood);
			classificationList.add(cr);
		}
		
		// Scale all likelihoods to create probability.
		for (ClassificationResult cr : classificationList) {
			cr.setProbability(cr.getLikelihood() / totalLikelihood);
		}
		
		// Sort by likelihood.
		Collections.sort(classificationList, new Comparator<ClassificationResult>() {
			@Override
			public int compare(ClassificationResult o1, ClassificationResult o2) {
				if (o1.getLikelihood() > o2.getLikelihood()) return -1;
				else return 1;
			}});
		
		return classificationList;
	}
	
	
	private List<ClassificationResult> classifyUsingLogs(List<Integer> values) {
		
		List<ClassificationResult> classificationList = new LinkedList<>();
		
		
		// Calculate the likelihood for each classification.
		for (String className : model.getClassCounts().keySet()) {
			
			// Initialize likelihood to prior probability of the classification.
			double x = (double) model.getClassCounts().get(className).intValue() 
					/ 
					(double) model.getNumberOfTrainingDocuments();
			double likelihood = Math.log(x);
			
			// Get the attribute list for this classification.
			List<AtomicInteger> attributeList = model.getWordCountsPerClass().get(className);
			
			// Get number of words in this class.
			int numberOfWordsInClass = model.getTotalWordsPerClass().get(className).intValue();
						
			// Get size of vocabulary.
			int vocabSize = evidence.getAttributeNames().size();

			// Multiply the probability of each attribute.
			for (int i = 0; i < attributeList.size(); i++) {
				
				// Get the word count for this attribute in this class.
				int wordCount = attributeList.get(i).intValue();
				
				// Compute probability with Laplace smoothing.
				double wordProbabilityGivenClass = (double) (wordCount + 1) 
						/ 
						(double) (numberOfWordsInClass + vocabSize);
								
				// Add contribution of each attribute probability.
				likelihood += values.get(i) * Math.log(wordProbabilityGivenClass);
			}
			
			
			// Add the classification result.
			ClassificationResult cr = new ClassificationResult();
			cr.setClassificationName(className);
			cr.setLikelihood(likelihood);
			classificationList.add(cr);
		}
				
		// Sort by likelihood.
		Collections.sort(classificationList, new Comparator<ClassificationResult>() {
			@Override
			public int compare(ClassificationResult o1, ClassificationResult o2) {
				if (o1.getLikelihood() > o2.getLikelihood()) return -1;
				else return 1;
			}});
		
		return classificationList;
	}
	
	@SuppressWarnings("serial")
	// http://suanpalm3.kmutnb.ac.th/teacher/FileDL/choochart82255418560.pdf
	public static void main(String[] args) {
		BayesClassifierText bct = new BayesClassifierText();
		
		bct.initializeEvidence();
		bct.buildModel();
		
		bct.printEvidence();
		bct.printTotalNumberOfEvents();
		bct.printClassificationCounts();
		bct.printAttributeCounts();
		
		System.out.println("***** Classify *****");
		List<ClassificationResult> classificationResults =
				bct.classify(new LinkedList<Integer>() {{ add(2); add(1); add(2); add(0); add(0); add(1);}});
		for (ClassificationResult cr : classificationResults) {
			System.out.format("%15s Likelihood: %15f, probability: %15f\n",
					cr.getClassificationName(), cr.getLikelihood(), cr.getProbability());			
		}
		
		System.out.println();
		
		List<ClassificationResult> classificationResults1 =
				bct.classifyUsingLogs(new LinkedList<Integer>() {{ add(2); add(1); add(2); add(0); add(0); add(1);}});
		for (ClassificationResult cr : classificationResults1) {
			System.out.format("%15s Likelihood using logs: %15f\n", cr.getClassificationName(), cr.getLikelihood());
		}

	}
}
