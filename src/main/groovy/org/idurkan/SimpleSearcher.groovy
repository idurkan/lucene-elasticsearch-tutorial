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
        String query = 'stem AND compression'
        int maxHits = 100;

        SimpleSearcher searcher = new SimpleSearcher();
        searcher.searchIndex(indexDir, query, maxHits);
    }

    void searchIndex(File indexDir, String queryStr, int maxHits) {
        Directory directory = FSDirectory.open(indexDir)

        def searcher = new IndexSearcher(DirectoryReader.open(directory))
        def parser = new QueryParser(Version.LUCENE_44, 'contents', new SimpleAnalyzer(Version.LUCENE_44))
        Query query = parser.parse(queryStr)

        ScoreDoc[] hits = searcher.search(query, maxHits).scoreDocs;
        def hitPaths = []

        println('We found ' + hits.length + ' hits!')

        hits.each { ScoreDoc hit ->
            hit.score
            int docId = hit.doc
            Document doc = searcher.doc(docId)

            println(hit.score + ' points: ' + doc.get('filename'))
        }
    }
}
