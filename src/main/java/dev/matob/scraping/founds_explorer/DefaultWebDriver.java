package dev.matob.scraping.founds_explorer;

import java.io.Closeable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DefaultWebDriver implements WebDriver, Closeable {

	private static final String DEFAULT_CACHE = "default";

	private static Class<? extends RemoteWebDriver> DRIVER_CLASS;

	public static void setChromeDriver(String driverPath) {
		System.setProperty("webdriver.chrome.driver", driverPath);
		DRIVER_CLASS = ChromeDriver.class;
	}

	protected WebDriver driver;

	protected Map<String, WebElement> elementMap;
	protected Map<String, List<WebElement>> elementsMap;

	protected boolean headless;

	public DefaultWebDriver() {
		elementMap = new HashMap<>();
		elementsMap = new HashMap<>();
	}

	public void setHeadless(boolean headless) {
		this.headless = headless;
	}

	private void createDriver() {
		if (DRIVER_CLASS.equals(ChromeDriver.class)) {
			ChromeOptions options = new ChromeOptions();
			if (headless)
				options.addArguments("--headless");
			driver = new ChromeDriver(options);
		}
	}

	public void clear() {
		elementMap.clear();
		elementsMap.clear();
	}

	/**
	 *
	 * @param url
	 * @param delay
	 *            Time to wait to visit the url (<b>in seconds</b>)
	 */
	public void visit(String url, long delay) {
		try {
			Thread.sleep(delay * 1000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		visit(url);
	}

	public void visit(String url) {
		if (driver == null)
			createDriver();

		Logger.getGlobal().log(Level.INFO, "Thread " + Thread.currentThread().getId() + " - Visit: " + url);
		driver.get(url);
	}

	protected WebElement getElement() {
		return getElement(DEFAULT_CACHE);
	}

	protected WebElement getElement(String cache) {
		return existsElement(cache) ? elementMap.get(cache) : null;
	}

	protected void setElement(WebElement element) {
		elementMap.put(DEFAULT_CACHE, element);
	}

	protected void setElement(WebElement element, String cache) {
		elementMap.put(cache, element);
	}

	// --------------------------------------------------------------

	public WebElement findD(By by) {
		return find(false, null, by, DEFAULT_CACHE);
	}

	public WebElement findD(By by, boolean cache) {
		return find(false, null, by, ((cache) ? DEFAULT_CACHE : null));
	}

	public WebElement findD(By by, String cache) {
		return find(false, null, by, cache);
	}

	// --------------------------------------------------------------

	public WebElement findE(By by) {
		return find(true, getElement(), by, DEFAULT_CACHE);
	}

	public WebElement findE(By by, boolean cache) {
		return find(true, getElement(), by, ((cache) ? DEFAULT_CACHE : null));
	}

	public WebElement findE(By by, String cache) {
		return find(true, getElement(cache), by, cache);
	}

	public WebElement findE(By by, String inCache, String cache) {
		return find(true, getElement(inCache), by, cache);
	}

	public WebElement findE(WebElement element, By by) {
		return find(true, element, by, DEFAULT_CACHE);
	}

	public WebElement findE(WebElement element, By by, boolean cache) {
		return find(true, element, by, ((cache) ? DEFAULT_CACHE : null));
	}

	public WebElement findE(WebElement element, By by, String cache) {
		return find(true, element, by, cache);
	}

	// --------------------------------------------------------------

	private WebElement find(boolean useElement, WebElement element, By by, String cache) {
		if (useElement && element == null)
			return null;

		try {
			element = ((useElement) ? element : driver).findElement(by);
		} catch (Exception ex) {
			element = null;
		}

		if (cache != null)
			elementMap.put(cache, element);

		return element;
	}

	public String getText() {
		return getText(DEFAULT_CACHE);
	}

	public String getText(String cache) {
		return (existsElement(cache)) ? elementMap.get(cache).getText() : null;
	}

	public String getHTML() {
		return getHTML(DEFAULT_CACHE);
	}

	public String getHTML(String cache) {
		return (existsElement(cache)) ? elementMap.get(cache).getAttribute("innerHTML") : null;
	}

	public void setText(String text) {
		this.setText(DEFAULT_CACHE, text);
	}

	public void setText(String cache, CharSequence... text) {
		if (!existsElement(cache))
			return;

		elementMap.get(cache).sendKeys(text);
	}

	public boolean existsElement() {
		return existsElement(DEFAULT_CACHE);
	}

	public boolean existsElement(String cache) {
		return elementMap.containsKey(cache) && elementMap.get(cache) != null;
	}

	protected List<WebElement> getElements() {
		return getElements(DEFAULT_CACHE);
	}

	protected WebElement getElements(int index) {
		return getElements(DEFAULT_CACHE, index);
	}

	protected List<WebElement> getElements(String cache) {
		return existsElements(cache) ? elementsMap.get(cache) : new LinkedList<>();
	}

	protected WebElement getElements(String cache, int index) {
		return existsElements(cache) ? elementsMap.get(cache).get(index) : null;
	}

	protected void setElements(List<WebElement> elements, String cache) {
		elementsMap.put(cache, elements);
	}

	protected void setElements(List<WebElement> elements) {
		elementsMap.put(DEFAULT_CACHE, elements);
	}

	// --------------------------------------------------------------

	public List<WebElement> findAllD(By by) {
		return findAll(false, null, by, DEFAULT_CACHE);
	}

	public List<WebElement> findAllD(By by, boolean cache) {
		return findAll(false, null, by, ((cache) ? DEFAULT_CACHE : null));
	}

	public List<WebElement> findAllD(By by, String cache) {
		return findAll(false, null, by, cache);
	}

	// --------------------------------------------------------------

	public List<WebElement> findAllE(By by) {
		return findAll(true, getElement(), by, DEFAULT_CACHE);
	}

	public List<WebElement> findAllE(By by, boolean cache) {
		return findAll(true, getElement(), by, ((cache) ? DEFAULT_CACHE : null));
	}

	public List<WebElement> findAllE(By by, String cache) {
		return findAll(true, getElement(cache), by, cache);
	}

	public List<WebElement> findAllE(By by, String inCache, String cache) {
		return findAll(true, getElement(inCache), by, cache);
	}

	public List<WebElement> findAllE(WebElement element, By by) {
		return findAll(true, element, by, DEFAULT_CACHE);
	}

	public List<WebElement> findAllE(WebElement element, By by, boolean cache) {
		return findAll(true, element, by, ((cache) ? DEFAULT_CACHE : null));
	}

	public List<WebElement> findAllE(WebElement element, By by, String cache) {
		return findAll(true, element, by, cache);
	}

	// --------------------------------------------------------------

	private List<WebElement> findAll(boolean useElement, WebElement element, By by, String cache) {
		if (useElement && element == null)
			return new LinkedList<>();

		List<WebElement> elements;
		try {
			elements = ((useElement) ? element : driver).findElements(by);
		} catch (Exception ex) {
			elements = new LinkedList<>();
		}

		if (cache != null)
			elementsMap.put(cache, elements);

		return elements;
	}

	public boolean existsElements() {
		return existsElements(DEFAULT_CACHE);
	}

	public boolean existsElements(String cache) {
		return elementsMap.containsKey(cache) && !elementsMap.get(cache).isEmpty();
	}

	public void forEach(Consumer<? super WebElement> action) {
		forEach(action, DEFAULT_CACHE);
	}

	public void forEach(Consumer<? super WebElement> action, String cache) {
		if (existsElements(cache))
			elementsMap.get(cache).forEach(action);
	}

	public List<String> findUrls() {
		return findUrls(null, false, DEFAULT_CACHE);
	}

	public List<String> findUrls(boolean useElement) {
		return findUrls(null, useElement, DEFAULT_CACHE);
	}

	public List<String> findUrls(String regex) {
		return findUrls(regex, false, DEFAULT_CACHE);
	}

	public List<String> findUrls(String regex, boolean useElement) {
		return findUrls(regex, useElement, DEFAULT_CACHE);
	}

	public List<String> findUrls(boolean useElement, String cache) {
		return findUrls(null, useElement, cache);
	}

	public List<String> findUrlsInElement() {
		return findUrls(null, true, DEFAULT_CACHE);
	}

	public List<String> findUrlsInElement(String regex) {
		return findUrls(regex, true, DEFAULT_CACHE);
	}

	public List<String> findUrls(String regex, boolean useElement, String cache) {
		List<String> urls = new LinkedList<>();

		if (useElement && (!elementMap.containsKey(cache) || elementMap.get(cache) == null))
			return urls;

		List<WebElement> elements = ((useElement) ? elementMap.get(cache) : driver).findElements(By.xpath(".//*"));

		elements.forEach((e) -> {
			String href = e.getAttribute("href");
			if (href != null && !href.trim().isEmpty()) {
				href = href.trim();
				if (regex == null || href.matches(regex))
					urls.add(href);
			}
		});

		return urls;
	}

	public String getAttribute(String name) {
		return getAttribute(name, DEFAULT_CACHE);
	}

	public String getAttribute(String name, String cache) {
		return getElement(cache).getAttribute(name);
	}

	// ###################################################################################
	// ##################################### WRAPING #####################################
	// ###################################################################################
	@Override
	public void get(String url) {
		driver.get(url);
	}

	@Override
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	@Override
	public String getTitle() {
		return driver.getTitle();
	}

	@Override
	public List<WebElement> findElements(By by) {
		return driver.findElements(by);
	}

	@Override
	public WebElement findElement(By by) {
		return driver.findElement(by);
	}

	@Override
	public String getPageSource() {
		return driver.getPageSource();
	}

	@Override
	public void close() {
		elementMap = new HashMap<>();
		elementsMap = new HashMap<>();

		if (driver != null)
			driver.quit();
	}

	@Override
	public void quit() {
		driver.quit();
	}

	@Override
	public Set<String> getWindowHandles() {
		return driver.getWindowHandles();
	}

	@Override
	public String getWindowHandle() {
		return driver.getWindowHandle();
	}

	@Override
	public TargetLocator switchTo() {
		return driver.switchTo();
	}

	@Override
	public Navigation navigate() {
		return driver.navigate();
	}

	@Override
	public Options manage() {
		return driver.manage();
	}
}