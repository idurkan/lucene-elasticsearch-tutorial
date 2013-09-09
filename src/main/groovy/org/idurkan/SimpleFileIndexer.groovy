package org.idurkan

import org.apache.lucene.analysis.core.SimpleAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.FSDirectory
import org.apache.lucene.util.Version
import groovy.io.FileType

class SimpleFileIndexer {
    public static void main(String[] args) {
        def dataDir = new File('data/')
        def indexDir = new File('index/')

        def indexer = new SimpleFileIndexer()
        int filesIndexed = indexer.buildIndex(indexDir, dataDir)

        println("Indexed ${filesIndexed} files.")
    }

    private int buildIndex(File indexDir, File dataDir) {
        def indexWriter = new IndexWriter(
                FSDirectory.open(indexDir),
                new IndexWriterConfig(Version.LUCENE_44, new SimpleAnalyzer(Version.LUCENE_44))
        )

        indexDirectory(indexWriter, dataDir)

        int numIndexed = indexWriter.maxDoc()
        indexWriter.close()

        return numIndexed
    }

    private void indexDirectory(IndexWriter indexWriter, File dataDir) {
        def fileList = []
        dataDir.eachFileRecurse(FileType.FILES) { File file ->
            fileList << file
        }

        fileList.each { File file ->
            indexFileWithWriter(indexWriter, file)
        }
    }

    private void indexFileWithWriter(IndexWriter indexWriter, File file) {
        if (file.isHidden() || file.isDirectory() || !file.canRead() || !file.exists()) {
            return
        }
        def path = file.getCanonicalPath()
        println('Indexing: ' + path)

        Document doc = new Document()
        doc.add(new Field('contents', new FileReader(file)))
        doc.add(new Field('filename', path, Field.Store.YES, Field.Index.ANALYZED))

        indexWriter.addDocument(doc)
    }
}
