package org.cuc.utils.vsm;

public class VSM {
    public static void main(String[] args) {
            // get documents
            String[] docs = {
                             "lsq java lsq vb Auto",
                             "The search trees overcome many issues of hash dictionary Insurance",
                            // "There are many different implementations of the Java
                            // Platform running at a variety of operating systems.",
                            // "Applet is a Java class that can be Embedded within an HTML
                            // page and downloaded and executed by a Web browser",
                            // "Java programming language defines eight primitive types",
                            // "The traditional crawlers used by search engines to build
                            // their collection of Web pages frequently gather unmodified
                            // pages that already exist in their collection",
                            // "The activities of many users on an Information Retrieval
                            // System(IRS) are of tenvery similar because they have similar
                            // preferences or related interest",
                            // "Research in Information Retrieval can be categorized along
                            // multiple dimensions, focusing, for example,on the technical
                            // paradigm, the research field, the targeted document type, or
                            // the application domain."
                            "Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Auto Auto Auto best best best best best best best best best best best best best best ",
                            "Car Car Car Car Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Auto Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance ",
                            "Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Car Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance Insurance best best best best best best best best best best best best best best best best best " };
            String[] queryTerms = { "Auto Insurance" };

            Collection allDocs = new Collection(docs, queryTerms);
            allDocs.process();
            // allDocs.printDocuments();
            // allDocs.printQuerys();
            allDocs.computeIDF();
            allDocs.computeTfIdf();
            allDocs.normalizedVector();
            // allDocs.printCollectionTermList();
            // allDocs.printTermTfIdf();
            allDocs.computeSimilarity();

    }
}

class Collection {
    private Document[] documents;
    private Document[] queryTerms;

    public static String[] stopList = { "an", "and", "are", "as", "at", "be",
                    "by", "for", "from", "has", "he", "in", "is", "it", "its", "of",
                    "on", "that", "the", "to", "was", "were", "will", "with" };
    private java.util.ArrayList termList = new java.util.ArrayList();

    public Collection() {

    }

    public Collection(String[] docs, String[] queryTerms) {
            setDocuments(docs);
            setQueryTerms(queryTerms);
    }

    public void process() {
            java.util.Arrays.sort(stopList);
            for (int i = 0; i < documents.length; i++) {
                    documents[i].computeTerms();
            }
            for (int i = 0; i < queryTerms.length; i++) {
                    queryTerms[i].computeTerms();
            }
    }

    public Document[] getDocuments() {
            return documents;
    }

    public void setDocuments(Document[] docs) {
            this.documents = docs;
    }

    public void setDocuments(String[] docs) {
            documents = new Document[docs.length];
            for (int i = 0; i < documents.length; i++) {
                    documents[i] = new Document();
                    documents[i].setContent(docs[i]);
            }
    }

    public void computeIDF() {
            for (int i = 0; i < documents.length; i++) {
                    for (int j = 0; j < documents[i].getTerms().size(); j++) {
                            Term t = (Term) (documents[i].getTerms().get(j));
                            int index = termList.indexOf(t);
                            Term newT;
                            if (index < 0) {
                                    newT = new Term();
                                    newT.setTerm(t.getTerm());
                            } else {
                                    newT = (Term) (termList.get(index));
                            }
                            newT.setTf(newT.getTf() + t.getTf());
                            newT.setDf(newT.getDf() + 1);
                            termList.add(newT);
                    }
            }
            for (int i = 0; i < termList.size(); i++) {
                    Term t = (Term) termList.get(i);
                    t.setIdf(Math.log(documents.length / (double) t.getDf()));
            }
    }

    public void computeTfIdf() {
            for (int i = 0; i < documents.length; i++) {
                    for (int j = 0; j < documents[i].getTerms().size(); j++) {
                            Term t = (Term) (documents[i].getTerms().get(j));
                            int index = termList.indexOf(t);
                            Term newT = (Term) (termList.get(index));
                            t.setIdf(newT.getIdf());
                            t.setTfIdf(t.getTf() * newT.getIdf());
                            // System.out.println(t.getTerm()+"/t"+t.getIdf()+"/t"+t.getTfIdf());
                    }
            }
    }

    public void normalizedVector() {
            computeNormalizedDocumentsVector();
            computeNormalizedQueryVector();
    }

    public void computeNormalizedDocumentsVector() {
            for (int i = 0; i < documents.length; i++) {
                    double result = 0;
                    for (int j = 0; j < documents[i].getTerms().size(); j++) {
                            Term t = (Term) (documents[i].getTerms().get(j));
                            result += t.getTfIdf() * t.getTfIdf();
                    }
                    result = Math.sqrt(result);
                    for (int j = 0; j < documents[i].getTerms().size(); j++) {
                            Term t = (Term) (documents[i].getTerms().get(j));
                            t.setTfIdf(t.getTfIdf() / result);
                    }
            }
    }

    public void computeNormalizedQueryVector() {
            for (int i = 0; i < queryTerms.length; i++) {
                    double result = 0;
                    for (int j = 0; j < queryTerms[i].getTerms().size(); j++) {
                            Term t = (Term) (queryTerms[i].getTerms().get(j));
                            result += t.getTf() * t.getTf();
                    }
                    result = Math.sqrt(result);
                    // System.out.println(result);
                    for (int j = 0; j < queryTerms[i].getTerms().size(); j++) {
                            Term t = (Term) (queryTerms[i].getTerms().get(j));
                            t.setTfIdf(t.getTf() / result);
                    }
            }
    }

    public void computeSimilarity() {
            for (int i = 0; i < queryTerms.length; i++) {
                    for (int j = 0; j < documents.length; j++) {
                            double result = 0;
                            for (int k = 0; k < queryTerms[i].getTerms().size(); k++) {
                                    Term t = (Term) (queryTerms[i].getTerms().get(k));
                                    int index = documents[j].getTerms().indexOf(t);
                                    if (index >= 0) {
                                            Term newT = (Term) (documents[j].getTerms().get(index));
                                            result += t.getTfIdf() * newT.getTfIdf();
                                            // System.out.println(j+"="+t.getTerm()+":"+newT.getTerm()+t.getTfIdf()+"*"+newT.getTfIdf());
                                    }
                            }
                            System.out.println(result);
                    }
            }
    }

    // int index=termList.indexOf(t);
    // Term newT;
    // if(index<0)
    // {
    // newT=new Term();
    // newT.setTerm(t.getTerm());
    // }
    // else
    // {
    // newT=(Term)(termList.get(index));
    // }
    //     

    public void printCollectionTermList() {
            for (int i = 0; i < termList.size(); i++) {
                    Term t = (Term) termList.get(i);
                    System.out.print(t.getTerm() + "/t" + t.getTf() + "/t" + t.getDf()
                                    + "/t" + t.getIdf());
                    System.out.println();
            }

    }

    public void printTermTfIdf() {
            printTermTfIdfOfDocuments(documents);
            printTermTfIdfOfDocuments(queryTerms);
    }

    public void printTermTfIdfOfDocuments(Document[] docs) {
            for (int i = 0; i < docs.length; i++) {
                    for (int j = 0; j < docs[i].getTerms().size(); j++) {
                            Term t = (Term) (docs[i].getTerms().get(j));
                            System.out.println(t.getTerm() + "/t" + t.getTf() + "/t"
                                            + t.getIdf() + "/t" + t.getTfIdf());
                    }
            }
    }

    public void printDocuments() {
            for (int i = 0; i < documents.length; i++) {
                    documents[i].printDocument();
            }
    }

    public void printQuerys() {
            for (int i = 0; i < queryTerms.length; i++) {
                    queryTerms[i].printDocument();
            }
    }

    public Document[] getQueryTerms() {
            return queryTerms;
    }

    public void setQueryTerms(String[] querys) {
            queryTerms = new Document[querys.length];
            for (int i = 0; i < queryTerms.length; i++) {
                    queryTerms[i] = new Document();
                    queryTerms[i].setContent(querys[i]);
            }
    }
}

class Document {
    private java.util.ArrayList terms = new java.util.ArrayList();
    private String content;

    public String getContent() {
            return content;
    }

    public void setContent(String content) {
            this.content = content;
    }

    public boolean findTerm(Term t) {
            if (terms.indexOf(t) < 0)
                    return false;
            else
                    return true;
    }

    public void computeTerms() {
            String[] tokens = content.toLowerCase().split(" ");
            for (int i = 0; i < tokens.length; i++) {
                    if (!(tokens[i].equals("") || tokens[i].length() == 1))
                            if (java.util.Arrays.binarySearch(Collection.stopList,
                                            tokens[i]) < 0) {
                                    Term t = new Term();
                                    t.setTerm(tokens[i]);
                                    t.setTf(1);

                                    int index = terms.indexOf(t);
                                    if (index < 0)
                                            terms.add(t);
                                    else {
                                            Term tone = (Term) (terms.get(index));
                                            tone.setTf(tone.getTf() + 1);
                                    }
                            }
            }

    }

    public void printDocument() {
            for (int j = 0; j < terms.size(); j++) {
                    Term t = (Term) terms.get(j);
                    System.out.print(t.getTerm() + "(" + t.getTf() + ")" + "/t");
            }
            System.out.println();
    }

    public java.util.ArrayList getTerms() {
            return terms;
    }

    public void setTerms(java.util.ArrayList terms) {
            this.terms = terms;
    }
}

class Term {
    private String term;
    private int df;
    private int tf;
    private double idf;
    private double tfIdf;

    public int getDf() {
            return df;
    }

    public void setDf(int f) {
            this.df = f;
    }

    public String getTerm() {
            return term;
    }

    public void setTerm(String term) {
            this.term = term;
    }

    public double getIdf() {
            return idf;
    }

    public void setIdf(double idf) {
            this.idf = idf;
    }

    public int getTf() {
            return tf;
    }

    public void setTf(int tf) {
            this.tf = tf;
    }

    public boolean equals(Object t) {
            if (this.term.equals(((Term) t).getTerm()))
                    return true;
            else
                    return false;
    }

    public double getTfIdf() {
            return tfIdf;
    }

    public void setTfIdf(double tfIdf) {
            this.tfIdf = tfIdf;
    }

}

