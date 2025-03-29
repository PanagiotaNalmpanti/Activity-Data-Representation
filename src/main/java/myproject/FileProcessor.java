package myproject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileProcessor {

    String line;
    String[] lineInfo;
    List<List<String>> dailyCaloriesList = new ArrayList<>(); //contains records [userID, date, calories]
    List<List<String>> dailyStepsList = new ArrayList<>(); //contains records [userID, date, steps]
    List<List<String>> hourlyCaloriesList = new ArrayList<>(); //contains records [userID, date-time, calories]
    List<List<String>> hourlyStepsList = new ArrayList<>(); //contains records [userID, date-time, steps]

    public void setDailyCalories(String filePath1, String filePath2) throws IOException {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        InputStream inputStream1 = new FileInputStream(file1);
        InputStream inputStream2 = new FileInputStream(file2);

        //reading the first file (dailyCalories_merged.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream1))) {
            reader.readLine(); //skipping the first line, which contains the headers
            while((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String initialDate = lineInfo[1];
                String calories = lineInfo[2];

                //date formatting to yyyy-mm-dd
                SimpleDateFormat input = new SimpleDateFormat("M/dd/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                String date = output.format(input.parse(initialDate));

                List<String> list = new ArrayList<>();
                list.add(userID);
                list.add(date);
                list.add(calories);
                dailyCaloriesList.add(list);
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        //reading the second file (health_fitness_dataset.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream2))) {
            reader.readLine(); //skipping the first line, which contains the headers
            while((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String date = lineInfo[1]; //in this file the date is already in the right format (yyyy-mm-dd)
                String calories = lineInfo[9];

                List<String> list = new ArrayList<>();
                list.add(userID);
                list.add(date);
                list.add(calories);
                dailyCaloriesList.add(list);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getDailyCaloriesList(){
        return dailyCaloriesList;
    }

    public void setDailySteps(String filePath1, String filePath2) throws IOException {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        InputStream inputStream1 = new FileInputStream(file1);
        InputStream inputStream2 = new FileInputStream(file2);

        //reading the first file (dailySteps_merged.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream1))) {
            reader.readLine(); //skipping the first line, which contains the headers
            while((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String initialDate = lineInfo[1];
                String steps = lineInfo[2];

                //date formatting to yyyy-mm-dd
                SimpleDateFormat input = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                String date = output.format(input.parse(initialDate));

                List<String> list = new ArrayList<>();
                list.add(userID);
                list.add(date);
                list.add(steps);
                dailyStepsList.add(list);
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        //reading the second file (health_fitness_dataset.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream2))) {
            reader.readLine(); //skipping the first line, which contains the headers
            while((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String date = lineInfo[1]; //in this file the date is already in the right format (yyyy-mm-dd)
                String steps = lineInfo[13];

                List<String> list = new ArrayList<>();
                list.add(userID);
                list.add(date);
                list.add(steps);
                dailyStepsList.add(list);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getDailyStepsList(){
        return dailyStepsList;
    }

    public void setHourlyCalories(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        //reading the file (hourlyCalories_merged.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine(); //skipping the first line, which contains the headers
            while ((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String initialDateTime = lineInfo[1];
                String calories = lineInfo[2];

                //date-time formatting to "yyyy-mm-dd hh:mm:ss"
                SimpleDateFormat input = new SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.ENGLISH);
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String dateTime = output.format(input.parse(initialDateTime));

                List<String> list = new ArrayList<>();
                list.add(userID);
                list.add(dateTime);
                list.add(calories);
                hourlyCaloriesList.add(list);
            }
        }
        catch (IOException | ParseException e){
            e.printStackTrace();
        }
    }

    public List<List<String>> getHourlyCaloriesList() { return hourlyCaloriesList; }

    public void setHourlySteps(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        //reading the file (hourlySteps_merged.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String initialDateTime = lineInfo[1];
                String steps = lineInfo[2];

                //date-time formatting to "yyyy-mm-dd hh:mm:ss"
                SimpleDateFormat input = new SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.ENGLISH);
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                String dateTime = output.format(input.parse(initialDateTime));

                List<String> list = new ArrayList<>();
                list.add(userID);
                list.add(dateTime);
                list.add(steps);
                hourlyStepsList.add(list);
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getHourlyStepsList() { return hourlyStepsList; }

}
