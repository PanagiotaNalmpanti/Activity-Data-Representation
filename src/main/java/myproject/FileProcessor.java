package myproject;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

    String line;
    String[] lineInfo;
    List<List<String>> dailyCaloriesList = new ArrayList<>(); //contains records [userID, date, calories]

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
                SimpleDateFormat input = new SimpleDateFormat("dd/MM/yyyy");
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

}
