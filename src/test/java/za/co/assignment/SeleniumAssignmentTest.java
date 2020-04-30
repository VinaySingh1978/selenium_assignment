package za.co.assignment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import com.opencsv.CSVWriter;

import io.github.bonigarcia.wdm.WebDriverManager;

public class SeleniumAssignmentTest {

	WebDriver driver = null;
	private static final int MAX_ELEMENTS_REQUIRED = 50;
	private static final String LOCATION = "Location";
	private static final String ADDRESS = "Address";
	private static final String CITY = "City";
	private static final String STATE = "State";
	private static final String LINK = "Link";

	// init code
	@Before
	public void initCode() {
		WebDriverManager.chromedriver().version("81.0.4044.69").setup();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized");
		options.addArguments("enable-automation");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-browser-side-navigation");
		options.addArguments("--disable-gpu");
		driver = new ChromeDriver(options);
		driver.get("https://www.chick-fil-a.com/locations");
		driver.switchTo().frame(0);
	}

	// cleanup code
	@After
	public void destroyCode() {
		driver.quit();
	}

	@Test
	public void runAssignment() throws InterruptedException, IOException, URISyntaxException {
		writeToCsv(fetchDataAfterExtraction());
	}

	// fetchDataAfterScraping - scrape all data from website
	private Map<String, List<String>> fetchDataAfterExtraction() throws InterruptedException {
		Map<String, List<String>> extractedData = new HashMap<>();
		List<WebElement> locationNameElements = new ArrayList<>();
		List<WebElement> addressElements = new ArrayList<>();
		List<WebElement> cityElements = new ArrayList<>();
		List<WebElement> stateElements = new ArrayList<>();
		List<WebElement> locationLinkElements = new ArrayList<>();
		int maxNumberOfClicksRequired = 5;
		int i = 0;
		while (i < maxNumberOfClicksRequired) {
			driver.findElement(By.xpath("//*[text()='Show more results']")).click();
			// explicit wait
			Thread.sleep(3000);
			locationNameElements = driver.findElements(By.xpath("//span[contains(@class, 'LocationName-geo')]"));
			addressElements = driver.findElements(By.xpath("//span[contains(@class, 'c-address-street-1')]"));
			cityElements = driver.findElements(By.xpath("//span[contains(@class, 'c-address-city')]"));
			stateElements = driver.findElements(By.xpath("//abbr[contains(@class, 'c-address-state')]"));
			locationLinkElements = driver.findElements(By.xpath("//a[contains(@class, 'Teaser-titleLink')]"));
			i++;
		}
		extractedData.put(LOCATION,
				locationNameElements.stream().map(WebElement::getText).collect(Collectors.toList()));
		extractedData.put(ADDRESS, addressElements.stream().map(WebElement::getText).collect(Collectors.toList()));
		extractedData.put(CITY, cityElements.stream().map(WebElement::getText).collect(Collectors.toList()));
		extractedData.put(STATE, stateElements.stream().map(WebElement::getText).collect(Collectors.toList()));

		final List<String> locationLinks = new ArrayList<>();
		locationLinkElements.forEach(linkElement -> {
			String unformattedLink = linkElement.getAttribute("onclick");
			String tmp = unformattedLink.substring("window.open(".length());
			locationLinks.add(tmp.substring(0, tmp.length() - ", '_parent')".length()));
		});
		extractedData.put(LINK, locationLinks);
		return extractedData;
	}

	// write to CSV
	private void writeToCsv(Map<String, List<String>> inputData) throws IOException, URISyntaxException {
		String fileName = "output.csv";
		String[] headerEntries = { LOCATION, ADDRESS, CITY, STATE, LINK }; 
        try (FileOutputStream fos = new FileOutputStream(fileName);
                OutputStreamWriter osw = new OutputStreamWriter(fos, 
                        StandardCharsets.UTF_8);
                CSVWriter writer = new CSVWriter(osw)) {
            writer.writeNext(headerEntries);
            if (inputData != null && !inputData.isEmpty()) {
            	List<String> locationNames = inputData.get(LOCATION);
            	List<String> addresses = inputData.get(ADDRESS);
            	List<String> cities = inputData.get(CITY);
            	List<String> states = inputData.get(STATE);
            	List<String> links = inputData.get(LINK);
            	for (int i = 0 ; i < MAX_ELEMENTS_REQUIRED; i++) {
            		String[] entry = new String[5];
            		entry[0] = locationNames.get(i);
            		entry[1] = addresses.get(i);
            		entry[2] = cities.get(i);
            		entry[3] = states.get(i);
            		entry[4] = links.get(i);
            		writer.writeNext(entry);
            	}
            }
        }        
	}
}