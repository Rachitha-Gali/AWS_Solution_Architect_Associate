package AWS.Forms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Keys;


public class App 
{
	private static String INPUTFILE = "pdf" ;
	static WebDriver driver;
	static String username = "**************";
	static String password = "****************";
	
	//question number
	static int number = 1;
	
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException{
		
		PDFManager pdfManager = new PDFManager(INPUTFILE);

		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Rachitha\\Downloads\\chromedriver_win32 (1)\\chromedriver.exe");
		
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);

		loginToGoogleForms();
		
		//click on create new form
		driver.findElement(By.xpath("(//*[@class=\"docs-homescreen-grid-item\"])[1]")).click();
		
		//itterate over pages
//		for(int i=15; i<pdfManager.getNumberOfPages()+1 ; i++) {
		//i= page numbers
		//adding questions and options to the form
		for(int i=184; i< 192 ; i++) {
			String text = pdfManager.toText(i);
			extractQuestionsFromThePage(text);
		}
		
		
		//click on existing form to which right answers have to be marked 
		driver.findElement(By.xpath("//*[@class='docs-homescreen-grid-item'][3]")).click();
		
		for(int i=258; i< 264 ; i++) {
			String text = pdfManager.toText(i);
			extractAnswersFromThePage(text);
		}
		
		driver.close();
	}
	
	private static void extractAnswersFromThePage(String text) throws InterruptedException {
		String[] lines = text.split("\n");
		
        for (int i=0; i < lines.length;i++ ){
        	Pattern qp = Pattern.compile("^"+number+". ");
        	String answer = "";
        	List<Character> options = new ArrayList<Character>();
        	
        	String line = lines[i];
        	line = line.trim();
        	
        	Matcher m = qp.matcher(line);
        	if(m.find()) {
        		String temp="";
        		number++;
        		qp = Pattern.compile("^"+number+". ");
        		do {
        			answer = answer + " " + line;
        			i++;
        			if(i > lines.length-1) {
        				break;
        			}
        			line = lines[i];
                	line = line.trim();
        			
        			m = qp.matcher(line);
        		}while(! m.find());
        		
        		i--;
        		temp = answer;
        		temp = temp.trim();
        		
        		while(Character.isDigit(temp.charAt(0)) || temp.charAt(0)=='.'){
        			temp = temp.substring(1);
        		}
        		temp = temp.trim();
        		options.add(temp.charAt(0));
        		
        		temp = temp.substring(1);
        		temp = temp.trim();
        		if(temp.charAt(0)==',') {
        			temp = temp.substring(1);
            		temp = temp.trim();
            		options.add(temp.charAt(0));
        		}
        		
        		System.out.println(answer);
        		
        		
        		addAnswersToForm(answer,number,options);
     
        	}	
        }
	}
	
	
	private static void addAnswersToForm(String answer,int number, List<Character> options) throws InterruptedException {
		
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
		WebElement webElement = driver.findElement(By.xpath("(//div[@class='freebirdFormeditorViewItemcardRoot item-dlg-affectsIndex item-dlg-dragTarget']/div/div/div[1])["+(number-1)+"]"));
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].scrollIntoView();", webElement );
			
		webElement.click();
		WebElement element;
		driver.findElement(By.xpath("(//*[text()='Answer key'])["+(number-1)+"]")).click();
		
		//setting question points to 1
		try {
			element = driver.findElement(By.xpath("//input[@type='number' and @value=0]"));
			element.click();
			element.clear();
			element.sendKeys("1");
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		//selecting the options
		try {
			
			for (Character character : options) {
				WebElement box = null;
				if(character.equals('A')) {
					box = driver.findElement(By.xpath("(//*[@class='freebirdFormeditorViewAssessmentAnswersListItemContent'])[1]"));
				}else if (character.equals('B')) {
					box = driver.findElement(By.xpath("(//*[@class='freebirdFormeditorViewAssessmentAnswersListItemContent'])[2]"));
				}else if (character.equals('C')) {
					box = driver.findElement(By.xpath("(//*[@class='freebirdFormeditorViewAssessmentAnswersListItemContent'])[3]"));
				}else if (character.equals('D')) {
					box = driver.findElement(By.xpath("(//*[@class='freebirdFormeditorViewAssessmentAnswersListItemContent'])[4]"));
				}else if (character.equals('E')) {
					box = driver.findElement(By.xpath("(//*[@class='freebirdFormeditorViewAssessmentAnswersListItemContent'])[5]"));
				}
				box.click();
			}
			
		}catch (Exception e) {
			
			System.out.println(e.getMessage());
		}
		
		//writing the feedback to wrong answers
		try {
			driver.findElement(By.xpath("//*[text()='Add answer feedback']")).click();
			WebElement modalContainer = driver.findElement(By.xpath("//*[@role='dialog']"));
			modalContainer.click();
			modalContainer.findElement(By.xpath("//*[@role='dialog']//*[text()='Enter feedback']//following-sibling::div/textarea")).sendKeys(answer);
			modalContainer.findElement(By.xpath("//*[text()='Save']")).click();
		}catch (Exception e) {
			System.out.print(e.getMessage());
		}	
			
		driver.findElement(By.xpath("//span[text()='Done']")).click();
			
	}
	
	
	

	private static void loginToGoogleForms() {
		driver.get("https://stackoverflow.com/users/login");
		driver.findElement(By.xpath("//*[@id=\"openid-buttons\"]/button[1]")).click();
		
		driver.findElement(By.id("identifierId")).sendKeys(username);
		driver.findElement(By.xpath("//*[@id='identifierNext']")).click();
		driver.findElement(By.name("password")).sendKeys(password);
		driver.findElement(By.xpath("//*[@id='passwordNext']")).click();
		
		driver.get("https://www.google.com/forms/about/");
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		driver.findElement(By.xpath("//*[@id=\"section-intro\"]/div[2]/div/div/div[1]/a[1]")).click();
	}


	private static void extractQuestionsFromThePage(String text) throws InterruptedException {
			
			String[] lines = text.split("\n");
	        Pattern qp = Pattern.compile("^\\d+. ");
	
	        for (int i=0; i < lines.length;i++ ){
	        	String line = lines[i];
	        	line = line.trim();
	        	Matcher m = qp.matcher(line);
	        	if(m.find()) {
	        		String question = line;
	        		System.out.println(question);
	        		ArrayList<String> answers = new ArrayList<String>();
	        		i++;
	        		line = lines[i];
	        		line = line.trim();
	        		Pattern ap = Pattern.compile("^[A-Za-z]. ");
	        		Matcher n = ap.matcher(line);
	        		while(! n.find()) {
	        			question = question + " " + line;
	        			i++;
	        			line = lines[i];
	        			line = line.trim();
	        			n = ap.matcher(line);
	        		}
	        		n = ap.matcher(line);
	        		while(n.find()){
	        			
	        			String answer = "";
	        			do{
	        				answer = answer + " " + line;
	        				i++;
	        				if(i>=lines.length) {
	        					break;
	        				}
	        				line = lines[i];
	        				line = line.trim();
	        				n = ap.matcher(line);
	        				m = qp.matcher(line);
	        			}while(! (m.find() || n.find()));
	        			i--;
	        			
	        			answers.add(answer);
	        			i++;
	        			if(i>=lines.length) {
	    					break;
	    				}
	    				line = lines[i];
	    				line = line.trim();
	    				n = ap.matcher(line);
	            		
	            	}
	            	
	        		i--;
	        		
	        		addQuestionsAndAnswersToForm(question,answers);
	        	}
	        }
	}


	private static void addQuestionsAndAnswersToForm(String question, ArrayList<String> answers) throws InterruptedException {
		
		JavascriptExecutor js = (JavascriptExecutor) driver;
		//js.executeScript("arguments[0].scrollIntoView();", driver.findElement(By.xpath("(//*[@aria-label='Question title'])[last()]")));
		js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		
		driver.findElement(By.xpath("(//div[@class='freebirdFormeditorViewItemContent'])[last()-1]")).click();
		
		driver.findElement(By.xpath("(//div[@aria-label=\"Add question\"])[1]")).click();
		
		
		driver.findElement(By.xpath("(//*[@aria-label='Question title'])[last()]")).clear();
		
		driver.findElement(By.xpath("(//*[@aria-label='Question title'])[last()]")).sendKeys(question);
		
		driver.findElement(By.xpath("(//*[@class='freebirdFormeditorViewItemTitleInput'])[last()]/following-sibling::div//div[@role='listbox']")).click();
		if(question.toLowerCase().contains("choose two")) {
			
			driver.findElement(By.xpath("((//*[@class='freebirdFormeditorViewItemTitleInput'])[last()]/following-sibling::div//div[@role='listbox']//span[text()='Checkboxes'])[last()]")).click();
		}else {
			
			driver.findElement(By.xpath("((//*[@class='freebirdFormeditorViewItemTitleInput'])[last()]/following-sibling::div//div[@role='listbox']//span[text()='Multiple choice'])[last()]")).click();
		}
		
		for(int a=0;a<answers.size();a++) {
			Thread.sleep(3*1000);
			driver.findElement(By.xpath("(//input[@data-initial-value='Option "+(a+1)+"'])[last()]")).sendKeys(Keys.chord(Keys.CONTROL, "a"));
			driver.findElement(By.xpath("(//input[@data-initial-value='Option "+(a+1)+"'])[last()]")).sendKeys(answers.get(a));
			
			js.executeScript("arguments[0].scrollIntoView();", driver.findElement(By.xpath("(//input[@aria-label='Add option'])[last()]")));
			driver.findElement(By.xpath("(//input[@aria-label='Add option'])[last()]")).click();
			
		}
		driver.findElement(By.xpath("(//div[@class='freebirdFormeditorViewOmnilistItemEditRegion'])[last()]//div[@aria-label='Remove option']")).click();
		
	}
}
