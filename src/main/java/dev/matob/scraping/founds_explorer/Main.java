package dev.matob.scraping.founds_explorer;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class Main {

	private static List<String> result = new LinkedList<>();

	public static void main(String[] args) {
		DefaultWebDriver.setChromeDriver("chromedriver.exe");

		try (DefaultWebDriver driver = new DefaultWebDriver()) {
			driver.setHeadless(true);
			driver.visit("https://www.fundsexplorer.com.br/funds/");
			driver.findD(By.id("fiis-list-container"));

			final List<String> urls = driver.findUrlsInElement();

			try (DefaultWebDriver _driver = new DefaultWebDriver()) {
				List<String> _result = new LinkedList<>();
				_driver.setHeadless(true);

				for (int i = 0; i < urls.size(); i++) {
					_result.add(execute(_driver, urls.get(i)));
					System.out.println(_result.get(i));
				}

				synchronized (result) {
					result.addAll(_result);
				}
			}

			for (String r : result)
				System.out.println(r);
		}

	}

	private static String execute(DefaultWebDriver driver, String url) {
		driver.visit(url);

		driver.findD(By.id("head"));
		driver.findE(By.className("section-title"));
		String codigo = driver.getText();

		driver.findD(By.id("stock-price"));
		driver.findE(By.className("price"));
		String valorCota = driver.getText().replace("R$ ", "");
		valorCota = !"N/A".equals(valorCota) ? valorCota.replace(".", "") : "0";

		driver.findD(By.id("main-indicators"));
		driver.findAllE(By.className("carousel-cell"));
		String negociacoes = "", dividendo = "", valorPatr = "", rentMensal = "";
		String value, label;
		for (WebElement e : driver.getElements()) {
			driver.findE(e, By.className("indicator-title"));
			label = driver.getText();
			driver.findE(e, By.className("indicator-value"));
			value = driver.getText();

			if ("Liquidez Diária".equals(label))
				negociacoes = !"N/A".equals(value) ? value.replace(".", "") : "0";
			else if ("Último Rendimento".equals(label))
				dividendo = !"N/A".equals(value) ? value.replace("R$ ", "") : "0";
			else if ("Patrimônio Líquido".equals(label)) {
				valorPatr = !"N/A".equals(value) ? value.replace("R$ ", "") : "0";
				if (valorPatr.endsWith("mi"))
					valorPatr = String.format("%.2f", (Double.parseDouble(valorPatr.replace(",", ".").replace(" mi", "")) * 1000000));
				if (valorPatr.endsWith("bi"))
					valorPatr = String.format("%.2f", (Double.parseDouble(valorPatr.replace(",", ".").replace(" bi", "")) * 1000000000));
			} else if ("Rentab. no mês".equals(label))
				rentMensal = !"N/A".equals(value) ? value : "0";
		}

		driver.findD(By.id("basic-infos"));
		driver.findAllE(By.className("text-wrapper"));
		String segmento = "";
		for (WebElement e : driver.getElements()) {
			driver.findE(e, By.className("title"));
			if ("SEGMENTO".equals(driver.getText())) {
				driver.findE(e, By.className("description"));
				segmento = driver.getText();
				segmento = !"N/A".equals(segmento) ? segmento : "-";
			}
		}

		double pr1 = 0, pr3 = 0, pr6 = 0, pr12 = 0;
		double dy1 = 0, dy3 = 0, dy6 = 0, dy12 = 0;
		driver.findD(By.id("dividends"));
		driver.findE(By.className("table"));
		driver.findAllE(By.tagName("tr"));
		List<WebElement> labels = driver.getElements(0).findElements(By.tagName("th"));
		List<WebElement> prValues = driver.getElements(1).findElements(By.tagName("td"));
		List<WebElement> dyValues = driver.getElements(2).findElements(By.tagName("td"));
		for (int x = 0; x < labels.size() && x < dyValues.size(); x++) {
			if ("Último".equals(labels.get(x).getText())) {
				pr1 = Double.parseDouble(prValues.get(x).getText().replace("R$ ", "").replace(".", "").replace(",", "."));
				dy1 = Double.parseDouble(dyValues.get(x).getText().replace("%", "").replace(".", "").replace(",", "."));
			} else if ("3 meses".equals(labels.get(x).getText())) {
				pr3 = Double.parseDouble(prValues.get(x).getText().replace("R$ ", "").replace(".", "").replace(",", "."));
				dy3 = Double.parseDouble(dyValues.get(x).getText().replace("%", "").replace(".", "").replace(",", ".")) / 3;
			} else if ("6 meses".equals(labels.get(x).getText())) {
				pr6 = Double.parseDouble(prValues.get(x).getText().replace("R$ ", "").replace(".", "").replace(",", "."));
				dy6 = Double.parseDouble(dyValues.get(x).getText().replace("%", "").replace(".", "").replace(",", ".")) / 6;
			} else if ("12 meses".equals(labels.get(x).getText())) {
				pr12 = Double.parseDouble(prValues.get(x).getText().replace("R$ ", "").replace(".", "").replace(",", "."));
				dy12 = Double.parseDouble(dyValues.get(x).getText().replace("%", "").replace(".", "").replace(",", ".")) / 12;
			}
		}

		return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%.4f\t%.4f\t%.4f\t%.4f", codigo, segmento, valorPatr, negociacoes, valorCota, dividendo, rentMensal, pr1, pr3, pr6, pr12);
	}
}
