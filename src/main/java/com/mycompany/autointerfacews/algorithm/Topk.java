/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.autointerfacews.algorithm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Topk {

//        private static KeywordAnalyzer analyzer = new KeywordAnalyzer(); 
        private static StandardAnalyzer analyzer = new StandardAnalyzer(); 
        private static IndexWriter writer;
        private static ArrayList<File> queue = new ArrayList<File>();

        public static void indexOnThisPath(String indexDir) {
                try {
                        FSDirectory dir = FSDirectory.open(new File(indexDir));
                        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
                        writer = new IndexWriter(dir, config);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void indexFileOrDirectory(String filePath) {
                try {
                        addFiles(new File(filePath));
                        int originalNumDocs = writer.numDocs();
                        for (File f : queue) {
                                FileReader fr = null;
                                try {
                                        Document doc = new Document();
                                        fr = new FileReader(f);
                                        doc.add(new TextField("contents", fr));
//                                        doc.add(new StringField("contents", MyFileReader.getXMLFileValues(f.getCanonicalPath()), Field.Store.YES));
                                        doc.add(new StringField("path", f.getPath(), Field.Store.YES));
                                        doc.add(new StringField("filename", f.getName(), Field.Store.YES));

                                        writer.addDocument(doc);
                                        System.out.println("Added: " + f);
                                } catch (Exception e) {
                                        System.out.println("Could not add: " + f);
                                } finally {
                                        fr.close();
                                }
                        }
                        int newNumDocs = writer.numDocs();
                        System.out.println("");
                        System.out.println("************************");
                        System.out.println((newNumDocs - originalNumDocs) + " documents added.");
                        System.out.println("************************");

                        queue.clear();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void closeIndex() {
                try {
                        writer.close(); // Close the Index
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        private static void addFiles(File file) {
                if (!file.exists()) {
                        System.out.println(file + " does not exist.");
                }
                if (file.isDirectory()) {
                        for (File f : file.listFiles()) {
                                addFiles(f);
                        }
                } else {
                        String filename = file.getName().toLowerCase();
                        //===================================================
                        // Only index text files
                        //===================================================
                        if (filename.endsWith(".htm") || filename.endsWith(".html")
                                || filename.endsWith(".xml") || filename.endsWith(".txt")) {
                                queue.add(file);
                        } else {
                                System.out.println("Skipped " + filename);
                        }
                }
        }
        //return list of library file url
        public static List<String> searchInIndexAndShowResult(String indexFilePath, String searchString, int maxHit) {
                List<String> res = new ArrayList<>();
                try {
                        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexFilePath))); // The api call to read the index
                        IndexSearcher searcher = new IndexSearcher(reader); // The Index Searcher Component
                        TopScoreDocCollector collector = TopScoreDocCollector.create(maxHit, true);

                        QueryParser queryParser = new QueryParser("contents", analyzer);
                        queryParser.setDefaultOperator(QueryParser.Operator.AND);
                        Query q = queryParser.parse(searchString);
                        searcher.search(q, collector);
                        ScoreDoc[] hits = collector.topDocs().scoreDocs;

                        // display results
                        System.out.println("key words: " + searchString);
                        System.out.println("Found " + hits.length + " hits.");
                        for (int i = 0; i < hits.length; ++i) {
                                int docId = hits[i].doc;
                                Document d = searcher.doc(docId);
                                System.out.println((i + 1) + ". " + d.get("path") + " score=" + hits[i].score); // Found the document
                                if (Double.compare(hits[i].score, 0.1) > 0) {
                                        res.add(d.get("path"));
                                }
                        }
                        System.out.println("Final:" + res.size());
                        res.forEach(t->System.out.println(t));
                        
                        
                        
//                        System.out.println("key words: " + searchString);
//                        String q = "static";
//                        Directory directory = FSDirectory.open(new File(indexFilePath));
//                        IndexReader indexReader = DirectoryReader.open(directory);
//                        IndexSearcher searcher = new IndexSearcher(indexReader);
//                        PhraseQuery  phraseQuery = new PhraseQuery();
//                        phraseQuery.add(new Term("contents", "Burrows-Wheeler Aligner"));
//                        TopDocs topDocs = searcher.search(phraseQuery, 10);
//                        ScoreDoc[] hits = topDocs.scoreDocs;
//                        for (ScoreDoc hit : hits) {
//                                int docId = hit.doc;
//                                Document d = searcher.doc(docId);
//                                System.out.println(d.get("fileName") + " Score :" + hit.score);
//                        }
//                        System.out.println("Found " + hits.length);


                } catch (Exception e) {
                        e.printStackTrace();
                }
                return res;
        }
        
        public static void deleteFileinDirectory(String filePath) {
                File direcotry = new File(filePath);
                if (direcotry.isDirectory()) {
                        for (File f : direcotry.listFiles()) {
                                try {
                                        Files.delete(f.toPath());
                                } catch (NoSuchFileException x) {
                                        System.err.format("%s: no such" + " file or directory%n", f.toPath());
                                } catch (DirectoryNotEmptyException x) {
                                        System.err.format("%s not empty%n", f.toPath());
                                } catch (IOException x) {
                                        System.err.println(x);
                                }
                        }
                }
        }
}
