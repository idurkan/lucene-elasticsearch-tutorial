package org.idurkan

import org.apache.lucene.analysis.core.SimpleAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.ScoreDoc
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version

class SimpleSearcher {
    static void main(String[] args) {
        def indexDir = new File('index/')
        String query = 'compression'
        int maxHits = 100;

        SimpleSearcher searcher = new SimpleSearcher();
        searcher.searchIndex(indexDir, query, maxHits);
    }

    void searchIndex(File indexDir, String queryStr, int maxHits) {
        Directory directory = FSDirectory.open(indexDir)

        def searcher = new IndexSearcher(DirectoryReader.open(directory));
        def parser = new QueryParser(Version.LUCENE_44, 'contents', new SimpleAnalyzer(Version.LUCENE_44))
        Query query = parser.parse(queryStr)

        ScoreDoc[] hits = searcher.search(query, maxHits).scoreDocs;
        def hitPaths = []
        hits.each { ScoreDoc hit ->
            int docId = hit.doc
            Document doc = searcher.doc(docId)
            hitPaths << doc.get('filename');
        }

        println('The top 100 hit files were:')
        hitPaths.each {
            println(it)
        }
    }
}
