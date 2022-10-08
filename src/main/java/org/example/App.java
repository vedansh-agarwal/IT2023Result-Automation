package org.example;

import com.opencsv.CSVReader;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import io.github.bonigarcia.wdm.WebDriverManager;

import static java.nio.file.Paths.*;

public class App {
    static WebDriver driver;
    static String resultFolder;
    public static void main(String[] args) throws InterruptedException, IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter absolute path to CSV: ");
        String filePath = sc.nextLine();
        System.out.print("Enter absolute path of folder to save result images: ");
        resultFolder = sc.nextLine();
        driver = WebDriverManager.chromedriver().create();
        driver.get("http://gbuexam.in/result/login.php");
        List<String[]> studentDetails = getDataFromFile(filePath);
        for(String[] details: studentDetails)   getResult(details);
    }

    public static void getResult(String[] details) throws InterruptedException, IOException {
        Thread.sleep(500);
        driver.findElement(By.id("username")).sendKeys(details[1]);
        Thread.sleep(200);
        driver.findElement(By.id("password")).sendKeys(details[2]);
        Thread.sleep(200);
        driver.findElement(By.xpath("/html/body/section/section/div/form/button")).click();
        Thread.sleep(500);
        try {
            driver.findElement(By.linkText("Try Again")).click();
        } catch (NoSuchElementException e) {
            WebElement btn = driver.findElement(By.xpath("/html/body/div[1]/ul/li/ul/li/ul/li/ul/li/ul/form/button"));
            if(btn.getText().equals("Declared")) {
                btn.click();
                Thread.sleep(1000);
                File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                FileUtils.copyFile(screenshotFile, new File(get(resultFolder, details[0]+".png").toString()));
                Thread.sleep(500);
                driver.navigate().back();
                Thread.sleep(500);
                driver.findElement(By.linkText("Logout")).click();
            } else {
                driver.findElement(By.linkText("Logout")).click();
            }
        }
    }

    private static List<String[]> getDataFromFile(String filePath) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(filePath);
        } catch (FileNotFoundException err) {
            System.out.println(filePath+" not found");
            return null;
        }
        CSVReader csvReader = new CSVReader(fileReader);
        List<String[]> output;
        try {
            output = csvReader.readAll();
            csvReader.close();
        } catch (Exception err) {
            System.out.println("CSV format invalid for "+filePath);
            return null;
        }
        try {
            fileReader.close();
            csvReader.close();
        } catch (Exception err) {
            return output;
        }
        return output;
    }
}
