package myproject;

import java.io.*;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileProcessor {

    String line;
    String[] lineInfo;
    List<List<String>> dailyCaloriesList = new ArrayList<>(); //contains records [userID, date, calories]
    List<List<String>> dailyStepsList = new ArrayList<>(); //contains records [userID, date, steps]
    List<List<String>> hourlyCaloriesList = new ArrayList<>(); //contains records [userID, date-time, calories]
    List<List<String>> hourlyStepsList = new ArrayList<>(); //contains records [userID, date-time, steps]
    List<List<String>> dailyDistanceList = new ArrayList<>(); //contains records [userID, date, distance]
    List<List<String>> dailyWeightList = new ArrayList<>(); //contains records [userID, date, weight in kg]
    List<List<String>> dailySleepList = new ArrayList<>(); //contains records [userID, date, sleepHours]
    List<List<String>> dailyVeryActiveMinutesList = new ArrayList<>(); //contains records [userID, date, very active minutes]
    List<List<String>> dailyFairlyActiveMinutesList = new ArrayList<>(); //contains records [userID, date, fairly active minutes]
    List<List<String>> dailyLightlyActiveMinutesList = new ArrayList<>(); //contains records [userID, date, lightly active minutes]
    List<List<String>> dailySedentaryMinutesList = new ArrayList<>(); //contains records [userID, date, sedentary minutes]
    List<List<String>> dailyHeartRateList = new ArrayList<>(); //contains records [userID, date, heart rate]

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

                dailyCaloriesList.add(Arrays.asList(userID, date, calories));
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

                dailyCaloriesList.add(Arrays.asList(userID, date, calories));
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

                dailyStepsList.add(Arrays.asList(userID, date, steps));
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

                dailyStepsList.add(Arrays.asList(userID, date, steps));
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

                hourlyCaloriesList.add(Arrays.asList(userID, dateTime, calories));
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

                hourlyStepsList.add(Arrays.asList(userID, dateTime, steps));
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getHourlyStepsList() { return hourlyStepsList; }

    public void setDailyDistance(String filePath) throws IOException {
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(file);

        //reading the file (dailyActivity_merged.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String initialDate = lineInfo[1];
                String distance = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(lineInfo[3]));

                //date formatting to "yyyy-mm-dd"
                SimpleDateFormat input = new SimpleDateFormat("M/d/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                String date = output.format(input.parse(initialDate));

                dailyDistanceList.add(Arrays.asList(userID, date, distance));
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getDailyDistanceList() { return dailyDistanceList; }

    public void setDailyWeight(String filePath1, String filePath2) throws IOException {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        InputStream inputStream1 = new FileInputStream(file1);
        InputStream inputStream2 = new FileInputStream(file2);

        //reading the first file (weightLogInfo_merged.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream1))) {
            reader.readLine();
            while((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                //the date column is of type "M/d/yyyy hh:mm:ss a", we want only the date (there are no double dates)
                String[] initialDateList = lineInfo[1].split(",");
                String initialDate = initialDateList[0];
                String weight = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(lineInfo[2]));

                //date formatting to "yyyy-mm-dd"
                SimpleDateFormat input = new SimpleDateFormat("M/d/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                String date = output.format(input.parse(initialDate));

                dailyWeightList.add(Arrays.asList(userID, date, weight));
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        //reading the second file (health_fitness_dataset.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream2))) {
            reader.readLine();
            while((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String date = lineInfo[1];
                String weight = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(lineInfo[5]));

                dailyWeightList.add(Arrays.asList(userID, date, weight));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getDailyWeightList() { return dailyWeightList; }

    public void setDailySleep(String filePath1, String filePath2) throws IOException {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        InputStream inputStream1 = new FileInputStream(file1);
        InputStream inputStream2 = new FileInputStream(file2);

        //reading the first file (sleepDay_merged.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream1))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String[] initialDateList = lineInfo[1].split(",");
                String initialDate = initialDateList[0];
                //in this file the sleep is counted in minutes, so they are converted to hours
                String sleepHours = String.format(Locale.ENGLISH, "%.1f", Double.parseDouble(lineInfo[3])/60);

                //date formatting to "yyyy-mm-dd"
                SimpleDateFormat input = new SimpleDateFormat("M/d/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                String date = output.format(input.parse(initialDate));

                dailySleepList.add(Arrays.asList(userID, date, sleepHours));
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        //reading the second file (health_fitness_dateset.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream2))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String date = lineInfo[1];
                String sleepHours = lineInfo[11];

                dailySleepList.add(Arrays.asList(userID, date, sleepHours));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getDailySleepList() { return dailySleepList; }

    public void setDailyActiveMinutes(String filePath1, String filePath2) throws IOException {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        InputStream inputStream1 = new FileInputStream(file1);
        InputStream inputStream2 = new FileInputStream(file2);

        //reading the first file (dailyIntensities_merged.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream1))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String initialDate = lineInfo[1];
                String sedentaryMinutes = lineInfo[2];
                String lightlyActiveMinutes = lineInfo[3];
                String fairlyActiveMinutes = lineInfo[4];
                String veryActiveMinutes = lineInfo[5];

                //date formatting to "yyyy-mm-dd"
                SimpleDateFormat input = new SimpleDateFormat("M/d/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                String date = output.format(input.parse(initialDate));

                dailyVeryActiveMinutesList.add(Arrays.asList(userID, date, veryActiveMinutes));
                dailyFairlyActiveMinutesList.add(Arrays.asList(userID, date, fairlyActiveMinutes));
                dailyLightlyActiveMinutesList.add(Arrays.asList(userID, date, lightlyActiveMinutes));
                dailySedentaryMinutesList.add(Arrays.asList(userID, date, sedentaryMinutes));
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        //reading the second file (health_fitness_dateset.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream2))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String date = lineInfo[1];
                String intensity = lineInfo[8];
                String activeMinutes = lineInfo[7];

                if (intensity.equals("Low")) {
                    dailyLightlyActiveMinutesList.add(Arrays.asList(userID, date, activeMinutes));
                }
                else if (intensity.equals("Medium")) {
                    dailyFairlyActiveMinutesList.add(Arrays.asList(userID, date, activeMinutes));
                }
                else if (intensity.equals("High")) {
                    dailyVeryActiveMinutesList.add(Arrays.asList(userID, date, activeMinutes));
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<List<String>> getDailyVeryActiveMinutes() { return dailyVeryActiveMinutesList; }
    public List<List<String>> getDailyFairlyActiveMinutes() { return dailyFairlyActiveMinutesList; }
    public List<List<String>> getDailyLightlyActiveMinutes() { return dailyLightlyActiveMinutesList; }
    public List<List<String>> getDailySedentaryMinutes() { return dailySedentaryMinutesList; }

    public void setDailyHeartRate(String filePath1, String filePath2) throws IOException {
        File file1 = new File(filePath1);
        File file2 = new File(filePath2);
        InputStream inputStream1 = new FileInputStream(file1);
        InputStream inputStream2 = new FileInputStream(file2);

        //reading the first file (heartrate_seconds_merged.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream1))) {
            reader.readLine();
            Map<String, List<Integer>> groupingList = new HashMap<>();
            while((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String[] initialDateTime = lineInfo[1].split(",");
                String initialDate = initialDateTime[0];
                Integer heartRateBySecond = Integer.parseInt(lineInfo[2]);

                //date formatting to "yyyy-mm-dd"
                SimpleDateFormat input = new SimpleDateFormat("M/d/yyyy");
                SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
                String date = output.format(input.parse(initialDate));

                //Mapping the heartRates to group them by userID and date
                List<Integer> heartRateList = new ArrayList<>();
                String key = userID + "_" + date;
                if (groupingList.containsKey(key)) {
                    heartRateList = groupingList.get(key);
                    heartRateList.add(heartRateBySecond);
                }
                else {
                    heartRateList = new ArrayList<>();
                    heartRateList.add(heartRateBySecond);
                    groupingList.put(key, heartRateList);
                }
            }

            for (Map.Entry<String, List<Integer>> entry: groupingList.entrySet()) {
                List<Integer> heartRateList = entry.getValue();

                //avg computation
                Iterator<Integer> iterator = heartRateList.iterator();
                double sum = 0;
                int num = 0;
                while (iterator.hasNext()) {
                    sum += iterator.next();
                    num++;
                }
                double avgHR = (num>0) ? (sum/num) : 0.0;
                String heartRate = String.format(Locale.ENGLISH, "%.1f", avgHR);
                String[] userDate = entry.getKey().split("_");
                String userID = userDate[0];
                String date = userDate[1];

                dailyHeartRateList.add(Arrays.asList(userID, date, heartRate));
            }
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        //reading the second file (health_fitness_dataset.csv)
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream2))) {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                lineInfo = line.split(",");
                String userID = lineInfo[0];
                String date = lineInfo[1];
                String heartRate = lineInfo[16];

                dailyHeartRateList.add(Arrays.asList(userID, date, heartRate));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
