package com.storminho.uffs;

public class GlobalVariables {
    //Where the tuple is gonna be split
    public static final String splitChars = ":+\\s*";

    //file to input path
    //Don't forget to define an environment variable called "STORMINHO" or something that you want (if you choose another name, you have to change in here and in "arffPath"
    public static final String filePath = System.getenv("STORMINHO") + "cd-100.csv";

    //Which tuple's column holds the id field
    public static final int fieldId = 1;

    //Where Id field will be split
    public static final String indexSplitToken = "-";

    //Dup validator
    public static final String dupToken = "dup";

    //Weka
    public final static int attributesNumber = 12;

    /*Select the methods that gonna be used. Use this as a sum with the following numbers:
    * 1 - Cosine Similarity
    * 2 - Jaccard  Similarity
    * 4 - Jaro Winkler Similarity
    * 8 - Levenshtein Similarity
    * Ex: public final static int rankingMethods = 2 + 8; means that Jaccard and Levenshtein gonna be used */
    public final static int rankingMethods = 1 + 8;
    public final static String arffPath = System.getenv("STORMINHO");
}
