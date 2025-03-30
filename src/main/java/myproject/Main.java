package myproject;

import java.io.IOException;
import java.text.ParseException;

public class Main {

    final static String filePath = "src/main/resources/";
    public static void main(String[] args) throws IOException {
        FileProcessor fileProcessor = new FileProcessor();

        //calling fileProcessor function for daily Activity
        fileProcessor.setDailyCalories(filePath+"dailyCalories_merged.csv", filePath+"health_fitness_dataset.csv");
        fileProcessor.setDailySteps(filePath+"dailySteps_merged.csv", filePath+"health_fitness_dataset.csv");
        fileProcessor.setHourlyCalories(filePath+"hourlyCalories_merged.csv");
        fileProcessor.setHourlySteps(filePath+"hourlySteps_merged.csv");
        fileProcessor.setDailyDistance((filePath+"dailyActivity_merged.csv"));
        fileProcessor.setDailyWeight(filePath+"weightLogInfo_merged.csv", filePath+"health_fitness_dataset.csv");
        fileProcessor.setDailySleepList(filePath+"sleepDay_merged.csv", filePath+"health_fitness_dataset.csv");
        fileProcessor.setDailyActiveMinutes(filePath+"dailyIntensities_merged.csv", filePath+"health_fitness_dataset.csv");
        fileProcessor.setDailyHeartRate(filePath+"heartrate_seconds_merged.csv", filePath+"health_fitness_dataset.csv");
    }
}