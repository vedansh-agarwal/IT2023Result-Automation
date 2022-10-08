package org.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Scanner;

import static java.nio.file.Paths.get;

public class getTopCGPA {
    static WebDriver driver;
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter absolute path to CSV: ");
        String filePath = sc.nextLine();
        driver = WebDriverManager.chromedriver().create();
        driver.get("http://gbuexam.in/result/login.php");
        List<String[]> studentDetails = getDataFromFile(filePath);
        CSVWriter newFile = getCSVWriterFromPath("/Users/vedansh/Desktop/Book1.csv");
        for(String[] details: studentDetails) {
            String cgpa = getCGPA(details);
//            System.out.println(details[0]+" "+ details[1] +" "+ details[2]+" "+ cgpa);
            newFile.writeNext(new String[]{details[0], details[1], details[2], cgpa});
        }
        closeCSVWriter(newFile);
        driver.close();
    }

    public static String getCGPA(String[] details) throws InterruptedException {
        String output = "";
        Thread.sleep(500);
        driver.findElement(By.id("username")).sendKeys(details[1]);
        Thread.sleep(200);
        driver.findElement(By.id("password")).sendKeys(details[2]);
        Thread.sleep(200);
        driver.findElement(By.xpath("/html/body/section/section/div/form/button")).click();
        Thread.sleep(500);
        try {
            driver.findElement(By.linkText("Try Again")).click();
            output = "Incorrect details";
        } catch (NoSuchElementException e) {
            WebElement btn = driver.findElement(By.xpath("/html/body/div[1]/ul/li/ul/li/ul/li/ul/li/ul/form/button"));
            String text = btn.getText();
            if(text.equals("Declared")) {
                btn.click();
                Thread.sleep(1000);
                output = driver.findElement(By.xpath("/html/body/section/div/div[3]/div[4]")).getText().substring(7);
                Thread.sleep(500);
                driver.navigate().back();
                Thread.sleep(500);
                driver.findElement(By.linkText("Logout")).click();
            } else {
                driver.findElement(By.linkText("Logout")).click();
                output = text;
            }
        }
        return output;
    }

    private static void closeCSVWriter(CSVWriter csvWriter) {
        try {
            csvWriter.close();
        } catch (Exception e) {
            System.out.println("E");
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

    private static CSVWriter getCSVWriterFromPath(String filePath) {
        FileWriter outputFile = null;
        try {
            outputFile = new FileWriter(filePath);
        } catch (Exception err) {
            System.out.println("Error creating writer object for "+filePath);
        }
        return new CSVWriter(outputFile, ',', '\0', '\0', "\n");
    }
}
