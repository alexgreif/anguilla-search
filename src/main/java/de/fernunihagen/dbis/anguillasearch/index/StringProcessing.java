package de.fernunihagen.dbis.anguillasearch.index;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fernunihagen.dbis.anguillasearch.CountMap;

public class StringProcessing {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringProcessing.class);

    private StringProcessing() {
        
    }

    public static CountMap<String> process(String text) {
        List<String> tkns = tokenize(text);
        try {
        tkns = removeStopwords(tkns);
        } catch(IOException e) {
            LOGGER.error("No stopwords.txt found");
        }
        CountMap<String> tknsProcessed = new CountMap<>();
        for (String tkn : tkns) {
            tknsProcessed.add(lemmatize(tkn));
        }
        return tknsProcessed;
    }

    public static List<String> tokenize(String text) {
        String[] tknsArray = text.toLowerCase().split("[\s.,!:;\\-\\)\\(_|&]+");
        return new ArrayList<>(Arrays.asList(tknsArray));
    }

    public static List<String> removeStopwords(List<String> words) throws IOException{
        List<String> wordsCopy = new ArrayList<>(words);
        Set<String> stopwords = new HashSet<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("stopwords.txt");
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            for(String line; (line = br.readLine()) != null; ) {
                if (!line.isEmpty()) {
                    stopwords.add(line);
                }
            }
        }
        wordsCopy.removeAll(stopwords);
        return wordsCopy;
    }

    public static String lemmatize(String text) {
        // I couldn't figure out how to apply only lemmatization without tokenization, 
        // so this function tokenizes before lemmatization 
        // and then concatenates lemmata to one word

        // Prepare pipeline
        Properties props = new Properties();
        props.setProperty("annotators","tokenize,ssplit,pos,lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        // Apply pipeline to input
        Annotation annot = new Annotation(text);
        annot.set(CoreAnnotations.TextAnnotation.class, text);
        pipeline.annotate(annot);

        // Get and concatenate lemmata
        StringBuilder lemma = new StringBuilder();
        for (CoreLabel token : annot.get(CoreAnnotations.TokensAnnotation.class)) {
            lemma.append(token.lemma());
        }
        return lemma.toString();
    }

}
