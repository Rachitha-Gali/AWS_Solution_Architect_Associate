package AWS.Forms;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class PDFManager {
	 private PDFParser parser;
	    private PDFTextStripper pdfStripper;
	    private PDDocument pdDoc;
	    private COSDocument cosDoc;

	    private String Text;
	    private File file;
	    
	    public PDFManager(String filePath) throws FileNotFoundException, IOException {
	    	
	    	file = new File(filePath);
	    	parser = new PDFParser(new RandomAccessFile(file, "r")); // update for PDFBox V 2.0
	    	parser.parse();
	    	cosDoc = parser.getDocument();
	    	pdDoc = new PDDocument(cosDoc);
		}

		public PDDocument getDocument() {
	        return this.pdDoc;
	    }

	    public String toText(int pageNumber) throws IOException {
	    	
	    	pdfStripper = new PDFTextStripper();
	        pdfStripper.setStartPage(pageNumber);
	        pdfStripper.setEndPage(pageNumber);
	        Text = pdfStripper.getText(pdDoc);
	        return Text;
	    }

	    public int getNumberOfPages() {
	        return pdDoc.getNumberOfPages();
	    }
	    
}
