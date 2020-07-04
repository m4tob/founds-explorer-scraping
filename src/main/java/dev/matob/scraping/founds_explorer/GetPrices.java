package dev.matob.scraping.founds_explorer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class GetPrices {

	private static final List<String> fiis = new LinkedList<String>(Arrays.asList(
			"HCTR11", "MXRF11", "HFOF11", "XPSF11", "HGRU11",
			"TRXF11", "RECT11", "XPPR11", "HGBS11", "MALL11",
			"XPML11", "BBPO11", "RBVA11", "OULG11", "MFII11",
			"CTXT11", "NSLU11", "RBED11", "BCFF11", "KNRI11",
			"HGLG11", "XPLG11"));

	public static void main(String[] args) {
		DefaultWebDriver.setChromeDriver("chromedriver.exe");

		try (DefaultWebDriver driver = new DefaultWebDriver()) {
			driver.setHeadless(true);

			try (DefaultWebDriver _driver = new DefaultWebDriver()) {
				_driver.setHeadless(true);

				List<String> result = new LinkedList<String>();
				for (String fii : fiis) {
					result.add(execute(_driver, fii));
				}
				for (String r : result)
					System.out.println(r);
			}
		}
	}

	private static String execute(DefaultWebDriver driver, String fii) {
		driver.visit("https://www.fundsexplorer.com.br/funds/" + fii);

		driver.findD(By.id("stock-price"));
		driver.findE(By.className("price"));
		String valorCota = driver.getText().replace("R$ ", "");
		valorCota = !"N/A".equals(valorCota) ? valorCota.replace(".", "") : "0";

		driver.findD(By.id("main-indicators"));
		driver.findAllE(By.className("carousel-cell"));
		String dividendo = "";
		String value, label;
		for (WebElement e : driver.getElements()) {
			driver.findE(e, By.className("indicator-title"));
			label = driver.getText();
			driver.findE(e, By.className("indicator-value"));
			value = driver.getText();

			if ("Último Rendimento".equals(label))
				dividendo = !"N/A".equals(value) ? value.replace("R$ ", "") : "0";
		}

		return String.format("%s\t%s", valorCota, dividendo);
	}
}
