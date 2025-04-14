package myproject;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class DataBaseCreator {
    String repositoryName = "ActivityTrackingOntology";
    HTTPRepository repository;
    ShaclValidation shaclValidation;
    String act = "http://www.semanticweb.org/ActivityTrackingOntology#";
    String sosa = "http://www.w3.org/ns/sosa#";

    //IRI definition
    IRI UserID = Values.iri(act+"UserID");
    IRI ActiveMinutesProp = Values.iri(act+"ActiveMinutesProp");
    IRI CaloriesProp = Values.iri(act+"CaloriesProp");
    IRI DistanceProp = Values.iri(act+"DistanceProp");
    IRI HeartRateProp = Values.iri(act+"HeartRateProp");
    IRI SleepProp = Values.iri(act+"SleepProp");
    IRI StepsProp = Values.iri(act+"StepsProp");
    IRI WeightProp = Values.iri(act+"WeightProp");
    IRI FairlyActiveMinutes = Values.iri(act+"FairlyActiveMinutes");
    IRI LightlyActiveMinutes = Values.iri(act+"LightlyActiveMinutes");
    IRI SedentaryMinutes = Values.iri(act+"SedentaryMinutes");
    IRI VeryActiveMinutes = Values.iri(act+"VeryActiveMinutes");
    IRI DCalories = Values.iri(act+"DCalories");
    IRI Distance = Values.iri(act+"Distance");
    IRI DSteps = Values.iri(act+"DSteps");
    IRI HeartRate = Values.iri(act+"HeartRate");
    IRI SleepHours = Values.iri(act+"SleepHours");
    IRI Weight = Values.iri(act+"Weight");
    IRI HCalories = Values.iri(act+"HCalories");
    IRI HSteps = Values.iri(act+"HSteps");
    IRI Sensor = Values.iri(act+"Sensor");
    IRI isFeatureOfInterestOf = Values.iri(sosa+"isFeatureOfInterestOf");
    IRI madeBySensor = Values.iri(sosa+"madeBySensor");
    IRI observedProperty = Values.iri(sosa+"observedProperty");
    IRI hasCount = Values.iri(act+"hasCount");
    IRI resultTime = Values.iri(sosa+"resultTime");
    IRI ActivityTracker = Values.iri(act+"ActivityTracker");



    public void graphDBConnection(FileProcessor fileProcessor) throws IOException {
        repository = new HTTPRepository("http://localhost:7200/repositories/" + repositoryName);
        repository.init();

        try (RepositoryConnection connection = repository.getConnection()){
            connection.clear();
            connection.begin();
            File file = new File("src/main/resources/ActivityTrackingOntology.ttl");
            try (InputStream inputStream = new FileInputStream(file)) {
                connection.add(inputStream, "", RDFFormat.TURTLE);
            }
            connection.commit();
            loadData(fileProcessor, connection);
        }
        catch (IOException | RepositoryException e) {
            e.printStackTrace();
        }
    }

    public void loadData(FileProcessor fileProcessor, RepositoryConnection connection) {
        List<List<String>> dailyCaloriesList = fileProcessor.getDailyCaloriesList();
        List<List<String>> dailyStepsList = fileProcessor.getDailyStepsList();
        List<List<String>> dailyDistanceList = fileProcessor.getDailyDistanceList();
        List<List<String>> dailyWeightList = fileProcessor.getDailyWeightList();
        List<List<String>> dailySleepList = fileProcessor.getDailySleepList();
        List<List<String>> dailyHeartRateList = fileProcessor.getDailyHeartRate();
        List<List<String>> dailyVeryActiveMinutesList = fileProcessor.getDailyVeryActiveMinutes();
        List<List<String>> dailyFairlyActiveMinutesList = fileProcessor.getDailyFairlyActiveMinutes();
        List<List<String>> dailyLightlyActiveMinutesList = fileProcessor.getDailyLightlyActiveMinutes();
        List<List<String>> dailySedentaryMinutesList = fileProcessor.getDailySedentaryMinutes();
        List<List<String>> hourlyCaloriesList = fileProcessor.getHourlyCaloriesList();
        List<List<String>> hourlyStepsList = fileProcessor.getHourlyStepsList();
        Set<String> declaredIDs = fileProcessor.getDeclaredIDs();

        shaclValidation = new ShaclValidation();

        //loading general data
        Model model = new TreeModel();
        model.add(ActivityTracker, RDF.TYPE, Sensor);
        connection.begin();
        connection.add(model);
        connection.commit();

        //loading specific data
        loadUserIDs(declaredIDs, connection, shaclValidation);
        loadDailyCalories(dailyCaloriesList, connection, shaclValidation);
        loadDailySteps(dailyStepsList, connection, shaclValidation);
        loadDailyDistance(dailyDistanceList, connection, shaclValidation);
        loadDailyWeight(dailyWeightList, connection, shaclValidation);
        loadDailySleep(dailySleepList, connection, shaclValidation);
        loadDailyHeartRate(dailyHeartRateList, connection, shaclValidation);
        loadDailyVeryActiveMinutes(dailyVeryActiveMinutesList, connection, shaclValidation);
        loadDailyFairlyActiveMinutes(dailyFairlyActiveMinutesList, connection, shaclValidation);
        loadDailyLightlyActiveMinutes(dailyLightlyActiveMinutesList, connection, shaclValidation);
        loadDailySedentaryMinutes(dailySedentaryMinutesList, connection, shaclValidation);
        loadHourlyCalories(hourlyCaloriesList, connection, shaclValidation);
        loadHourlySteps(hourlyStepsList, connection, shaclValidation);
    }

    public void loadUserIDs (Set<String> declaredIDs, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();
        for (String u: declaredIDs) {
            IRI userID = Values.iri(act+u);
            model.add(userID, RDF.TYPE, UserID);
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("UserID committed");
    }

    public void loadDailyCalories(List<List<String>> dailyCaloriesList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailyCaloriesList) { //list: [userID, date, calories]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            float caloriesCount = Float.parseFloat(list.get(2));
            IRI caloriesID = Values.iri(DCalories+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(caloriesID, RDF.TYPE, DCalories);
            model.add(userID, isFeatureOfInterestOf, caloriesID);
            model.add(caloriesID, observedProperty, CaloriesProp);
            model.add(caloriesID, hasCount, Values.literal(caloriesCount));
            model.add(caloriesID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailyCalories committed");
    }

    public void loadDailySteps(List<List<String>> dailyStepsList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailyStepsList) { //list: [userID, date, steps]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            int stepsCount = Integer.parseInt(list.get(2));
            IRI stepsID = Values.iri(DSteps+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(stepsID, RDF.TYPE, DSteps);
            model.add(userID, isFeatureOfInterestOf, stepsID);
            model.add(stepsID, observedProperty, StepsProp);
            model.add(stepsID, hasCount, Values.literal(stepsCount));
            model.add(stepsID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailySteps committed");
    }

    public void loadDailyDistance(List<List<String>> dailyDistanceList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailyDistanceList) { //list: [userID, date, distance]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            float distanceCount = Float.parseFloat(list.get(2));
            IRI distanceID = Values.iri(Distance+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(distanceID, RDF.TYPE, Distance);
            model.add(userID, isFeatureOfInterestOf, distanceID);
            model.add(distanceID, observedProperty, DistanceProp);
            model.add(distanceID, hasCount, Values.literal(distanceCount));
            model.add(distanceID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailyDistance committed");
    }

    public void loadDailyWeight(List<List<String>> dailyWeightList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailyWeightList) { //list: [userID, date, weight]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            float weightCount = Float.parseFloat(list.get(2));
            IRI weightID = Values.iri(Weight+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(weightID, RDF.TYPE, Weight);
            model.add(userID, isFeatureOfInterestOf, weightID);
            model.add(weightID, observedProperty, WeightProp);
            model.add(weightID, hasCount, Values.literal(weightCount));
            model.add(weightID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailyWeight committed");
    }

    public void loadDailySleep(List<List<String>> dailySleepList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailySleepList) { //list: [userID, date, sleepHours]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            float sleepCount = Float.parseFloat(list.get(2));
            IRI sleepID = Values.iri(SleepHours+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(sleepID, RDF.TYPE, SleepHours);
            model.add(userID, isFeatureOfInterestOf, sleepID);
            model.add(sleepID, observedProperty, SleepProp);
            model.add(sleepID, hasCount, Values.literal(sleepCount));
            model.add(sleepID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailySleep committed");
    }

    public void loadDailyHeartRate(List<List<String>> dailyHeartRateList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailyHeartRateList) { //list: [userID, date, HeartRate]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            float heartRateCount = Float.parseFloat(list.get(2));
            IRI heartRateID = Values.iri(HeartRate+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(heartRateID, RDF.TYPE, HeartRate);
            model.add(userID, isFeatureOfInterestOf, heartRateID);
            model.add(heartRateID, observedProperty, HeartRateProp);
            model.add(heartRateID, hasCount, Values.literal(heartRateCount));
            model.add(heartRateID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailyHeartRate committed");
    }

    public void loadDailyVeryActiveMinutes(List<List<String>> dailyVeryActiveMinutesList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailyVeryActiveMinutesList) { //list: [userID, date, VeryActiveMinutes]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            int VAMinutesCount = Integer.parseInt(list.get(2));
            IRI VAMinutesID = Values.iri(VeryActiveMinutes+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(VAMinutesID, RDF.TYPE, VeryActiveMinutes);
            model.add(userID, isFeatureOfInterestOf, VAMinutesID);
            model.add(VAMinutesID, observedProperty, ActiveMinutesProp);
            model.add(VAMinutesID, hasCount, Values.literal(VAMinutesCount));
            model.add(VAMinutesID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailyVeryActiveMinutes committed");
    }

    public void loadDailyFairlyActiveMinutes(List<List<String>> dailyFairlyActiveMinutesList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailyFairlyActiveMinutesList) { //list: [userID, date, FairlyActiveMinutes]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            int FAMinutesCount = Integer.parseInt(list.get(2));
            IRI FAMinutesID = Values.iri(FairlyActiveMinutes+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(FAMinutesID, RDF.TYPE, FairlyActiveMinutes);
            model.add(userID, isFeatureOfInterestOf, FAMinutesID);
            model.add(FAMinutesID, observedProperty, ActiveMinutesProp);
            model.add(FAMinutesID, hasCount, Values.literal(FAMinutesCount));
            model.add(FAMinutesID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailyFairlyActiveMinutes committed");
    }

    public void loadDailyLightlyActiveMinutes(List<List<String>> dailyLightlyActiveMinutesList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailyLightlyActiveMinutesList) { //list: [userID, date, LightlyActiveMinutes]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            int LAMinutesCount = Integer.parseInt(list.get(2));
            IRI LAMinutesID = Values.iri(LightlyActiveMinutes+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(LAMinutesID, RDF.TYPE, LightlyActiveMinutes);
            model.add(userID, isFeatureOfInterestOf, LAMinutesID);
            model.add(LAMinutesID, observedProperty, ActiveMinutesProp);
            model.add(LAMinutesID, hasCount, Values.literal(LAMinutesCount));
            model.add(LAMinutesID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailyLightlyActiveMinutes committed");
    }

    public void loadDailySedentaryMinutes(List<List<String>> dailySedentaryMinutesList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: dailySedentaryMinutesList) { //list: [userID, date, SedentaryMinutes]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            int SedentaryMinutesCount = Integer.parseInt(list.get(2));
            IRI SedentaryMinutesID = Values.iri(SedentaryMinutes+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(SedentaryMinutesID, RDF.TYPE, SedentaryMinutes);
            model.add(userID, isFeatureOfInterestOf, SedentaryMinutesID);
            model.add(SedentaryMinutesID, observedProperty, ActiveMinutesProp);
            model.add(SedentaryMinutesID, hasCount, Values.literal(SedentaryMinutesCount));
            model.add(SedentaryMinutesID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("DailySedentaryMinutes committed");
    }

    public void loadHourlyCalories(List<List<String>> hourlyCaloriesList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: hourlyCaloriesList) { //list: [userID, dateTime, calories]
            IRI userID = Values.iri(act+list.get(0));
            String dateTime = list.get(1);
            int caloriesCount = Integer.parseInt(list.get(2));
            IRI caloriesID = Values.iri(HCalories+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(caloriesID, RDF.TYPE, HCalories);
            model.add(userID, isFeatureOfInterestOf, caloriesID);
            model.add(caloriesID, observedProperty, CaloriesProp);
            model.add(caloriesID, hasCount, Values.literal(caloriesCount));
            model.add(caloriesID, resultTime, Values.literal(dateTime, Values.iri("http://www.w3.org/2001/XMLSchema#dateTime")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("HourlyCalories committed");
    }

    public void loadHourlySteps(List<List<String>> hourlyStepsList, RepositoryConnection connection, ShaclValidation shaclValidation) {
        Model model = new TreeModel();

        for (List<String> list: hourlyStepsList) { //list: [userID, dateTime, steps]
            IRI userID = Values.iri(act+list.get(0));
            String dateTime = list.get(1);
            int stepsCount = Integer.parseInt(list.get(2));
            IRI stepsID = Values.iri(HSteps+"ID_"+list.get(0)+"_"+list.get(1));

            model.add(stepsID, RDF.TYPE, HSteps);
            model.add(userID, isFeatureOfInterestOf, stepsID);
            model.add(stepsID, observedProperty, CaloriesProp);
            model.add(stepsID, hasCount, Values.literal(stepsCount));
            model.add(stepsID, resultTime, Values.literal(dateTime, Values.iri("http://www.w3.org/2001/XMLSchema#dateTime")));
        }
        shaclValidation.validation(model);

        connection.begin();
        connection.add(model);
        connection.commit();
        System.out.println("HourlySteps committed");
    }

}
