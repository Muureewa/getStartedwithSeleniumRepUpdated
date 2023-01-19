import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;  

/**
* Final Project: Create a JAVA program that analyzes a data set that interests you. 

* Selected Data Set: https://corgis-edu.github.io/corgis/csv/covid/ (from the CORGIS Dataset Project)
*/
public class covid {
    private static String[][] covidData = new String[53629][10]; // There are 53629 total rows of data
    private static String[][] newCasesPerDay = new String[350][2]; // There are 350 total days
    private static String[][] newDeathsPerDay = new String[350][2]; // There are 350 total days
    private static String[][] deathsByCountry = new String[213][2]; // There are 213 distinct countries

    private static int numDays; // 350
    private static int numCountries; // 213
    private static String dayOne; // 31/12/2019
    private static String finalDay; // 14/12/2020
    
    /**
     * ~~~ CSV File Format ~~~
     * 10 columns: Day, Month, Year, Cases, Deaths, Country, Location / Country Code, Population, Continent, Rate
     * 
     * 0 - Date.Day (Integer): The day of the month for this report
     * 1 - Date.Month (Integer): The month of the year for this report
     * 2 - Date.Year (Integer): The year of this report
     * 3 - Data.Cases (Integer): Number of new cases reported
     * 4 - Data.Deaths (Integer): Number of new deaths reported
     * 5 - Location.Country	(String): Name of country for this report (e.g. "United_States_of_America")
     * 6 - Location.Code (String): Three letter country code (e.g. "USA")
     * 7 - Data.Population (Integer): Population of the country in 2019
     * 8 - Location.Continent (String): Continent of country (e.g., "Asia", "Europe", "America")
     * 9 - Data.Rate (Float): Cumulative number of cases reported for 14 days per 100000 people 
     * 
     * 53630 rows: 53629 rows of data + 1 header row

     * This method processes the raw data that was read from covid.csv
     * @param data String array containing the raw data
     */
    public static void processData(String[] data) {
        int curIndex = 0;
        boolean firstRow = true;
        for(String row : data) {
            if(firstRow) {
                firstRow = false;
                continue;
            }
            String[] curRow = new String[10];
            if(row.contains("\"BES\"")) { // "Bonaire, Saint Eustatius and Saba" -> "Bonaire_Saint Eustatius and Saba"
                int indexOfStart = row.indexOf("Bonaire, Saint Eustatius and Saba");
                int removalIndex = indexOfStart + 7;
                row = row.substring(0, removalIndex) + "_" + row.substring(removalIndex + 2, row.length());
            }
            String[] splitRow = row.split(",");
            for(int i = 0; i < splitRow.length; i++) {
                curRow[i] = splitRow[i].replaceAll("^\"|\"$", "");
            }
            
            covidData[curIndex] = curRow;
            curIndex++;
        }
    }

    // The data covers 350 days, from December 31, 2019 - December 14, 2020
    private static void getNumDays() {
        dayOne = covidData[0][0] + "/" + covidData[0][1] + "/" + covidData[0][2];
        numDays = 1;

        finalDay = dayOne;
        for(String[] row : covidData) {
            String date = row[0] + "/" + row[1] + "/" + row[2];
            if(!date.equals(finalDay)) {
                numDays++;
            }
            finalDay = date;
            newCasesPerDay[numDays - 1][0] = date;
            newDeathsPerDay[numDays - 1][0] = date;
        }
    }

    // The data covers 213 different countries
    private static void getNumCountries() {
        Set<String> countries = new HashSet<String>(); // using a set because it ignores duplicate values
        int countryIndex = 0;
        for(String[] row : covidData) {
            String curCountry = row[5];
            if(!countries.contains(curCountry)) {
                deathsByCountry[countryIndex][0] = curCountry;
                countryIndex++;
            }
            countries.add(curCountry);
        }
        numCountries = countries.size();
    }

    // Populate the arrays newCasesPerDay[350][2] and newDeathsPerDay[350][2]
    private static void getCasesAndDeathsPerDay() {
        for(int i = 0; i < 350; i++) {
            newCasesPerDay[i][1] = "0";
            newDeathsPerDay[i][1] = "0";
        }

        int curDate = 0;
        for(String[] row : covidData) {
            String date = row[0] + "/" + row[1] + "/" + row[2];
            if(!date.equals(newCasesPerDay[curDate][0])) {
                curDate++;
            }
            newCasesPerDay[curDate][1] = String.valueOf(Integer.parseInt(newCasesPerDay[curDate][1]) + Integer.parseInt(row[3]));
            newDeathsPerDay[curDate][1] = String.valueOf(Integer.parseInt(newDeathsPerDay[curDate][1]) + Integer.parseInt(row[4]));
        }
    }

    /**
     * Populate the array deathsByCountry[213][2]
     */
    private static void getDeathsByCountry() {
        for(int i = 0; i < 213; i++) {
            deathsByCountry[i][1] = "0";
        }

        for(String[] row : covidData) {
            String curCountry = row[5];
            for(int i = 0; i < 213; i++) {
                if(deathsByCountry[i][0].equals(curCountry)) {
                    deathsByCountry[i][1] = String.valueOf(Integer.parseInt(deathsByCountry[i][1]) + Integer.parseInt(row[4]));
                    break;
                }
            }
        }
    }

    /**
     * This method prints the results of the analysis of covid.csv
     */
    private static void printResults() {
        System.out.println();

        System.out.println("The file \'covid.csv\' covers " + numDays + " days of coronavirus case data\nFrom " + dayOne + " to " + finalDay + "\nFrom " + numCountries + " different countries");
        System.out.println("\n~ New Cases & Deaths Per Day ~\n");
        System.out.println("Date\t\t\tNew Cases\tDeaths");
        for(int i = 0; i < 350; i++) {
            System.out.println(newCasesPerDay[i][0] + "\t\t" + newCasesPerDay[i][1] + "\t\t" + newDeathsPerDay[i][1]);
        }
        System.out.println("\n~ Deaths by Country ~\n");
        System.out.println("Deaths\t\tCountry");
        for(int i = 0; i < 213; i++) {
            System.out.println(deathsByCountry[i][1] + "\t\t" + deathsByCountry[i][0]);
        }

        System.out.println();
    }
    
    /**
     * This main method calls the other methods created to process and analyze the COVID-19 dataset and then prints the results
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Please enter the name of the .csv file containing COVID-19 data: ");
        String filename = in.nextLine();
        while(true) {
            try {
                Scanner sc = new Scanner(new File(filename));
                sc.useDelimiter(",");
    
                String[] rawData = new String[53630];
                int curIndex = 0;
                while(sc.hasNextLine()) {  
                    rawData[curIndex++] = sc.nextLine();
                }
                processData(rawData);
                getNumDays();
                getCasesAndDeathsPerDay();
                getNumCountries();
                getDeathsByCountry();
                printResults();
    
                sc.close(); //closes the scanner 
                break; 
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.print("Please enter the name of the .csv file containing COVID-19 data: ");
                filename = in.nextLine();
            }
        }
    }
}
