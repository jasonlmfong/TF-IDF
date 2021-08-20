import model.DocumentData;
import search.TFIDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Search{
    public static final String BOOKS_DIRECTORY = "./resources/books";
    public static final String SEARCH_QUERY_1 = "a detective who uses his deductive reasoning to solve crimes";
    public static final String SEARCH_QUERY_2 = "a girl that falls through a rabbit hole into a fantasy wonderland";
    public static final String SEARCH_QUERY_3 = "a monster abandoned by his creator";

    public static void main(String[] args){
        File documentsDirectory = new File(BOOKS_DIRECTORY);

        List<String> documents = Arrays.asList(documentsDirectory.list())
            .stream()
            .map(documentName -> BOOKS_DIRECTORY + "/" + documentName)
            .collect(Collectors.toList());

        List<String> terms = TFIDF.getWordsFromLine(SEARCH_QUERY_3);

        try{
            findMostRelevantDocuments(documents, terms);
        } catch (FileNotFoundException e){
            System.out.println(e.getMessage());
        }
    }

    private static void findMostRelevantDocuments(List<String> documents, List<String> terms) throws FileNotFoundException{
        Map<String, DocumentData> documentResults = new HashMap<>();

        for(String document : documents){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(document));
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> words = TFIDF.getWordsFromLines(lines);
            DocumentData documentData = TFIDF.createDocumentData(words, terms);
            documentResults.put(document, documentData);
            
            try {
                bufferedReader.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        Map<Double, List<String>> documentsByScore = TFIDF.getDocumentSortedByScore(terms, documentResults);
        printResults(documentsByScore);
        
    }

    private static void printResults(Map<Double, List<String>> documentsByScore){
        for(Map.Entry<Double, List<String>> docScorePair : documentsByScore.entrySet()){
            double score = docScorePair.getKey();
            for(String document : docScorePair.getValue()){
                System.out.println(String.format("Book : %s - score : %f", document.split("/")[3], score));
            }
        }
    }
}