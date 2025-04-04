package myproject;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.TreeModel;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.repository.RepositoryConnection;
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
    RepositoryConnection connection;
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

    Set<String> declaredIDs = new HashSet<>();

    public void graphDBConnection() throws IOException {
        repository = new HTTPRepository("http://localhost:7200/repositories/" + repositoryName);
        repository.init();
        connection = repository.getConnection();
        connection.clear();
        try {
            File file = new File("src/main/resources/ActivityTrackingOntology.ttl");
            InputStream inputStream = new FileInputStream(file);
            connection.add(inputStream, "", RDFFormat.TURTLE);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadDailyCalories(List<List<String>> dailyCaloriesList) {
        Model model = new TreeModel();

        for (List<String> list: dailyCaloriesList) { //list: [userID, date, calories]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            float caloriesCount = Float.parseFloat(list.get(2));
            IRI caloriesID = Values.iri(DCalories+"ID_"+list.get(0)+"_"+list.get(1));

            if (!declaredIDs.contains(list.get(0))) { //list.get(0) returns the userID
                model.add(userID, RDF.TYPE, UserID);
                declaredIDs.add(list.get(0));
            }

            model.add(caloriesID, RDF.TYPE, DCalories);
            model.add(userID, isFeatureOfInterestOf, caloriesID);
            model.add(caloriesID, observedProperty, CaloriesProp);
            model.add(caloriesID, hasCount, Values.literal(caloriesCount));
            model.add(caloriesID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }

        connection.begin();
        connection.add(model);
        connection.commit();
        connection.close();
    }

    public void loadDailySteps(List<List<String>> dailyStepsList) {
        Model model = new TreeModel();

        for (List<String> list: dailyStepsList) { //list: [userID, date, steps]
            IRI userID = Values.iri(act+list.get(0));
            String date = list.get(1);
            int stepsCount = Integer.parseInt(list.get(2));
            IRI stepsID = Values.iri(DSteps+"ID_"+list.get(0)+"_"+list.get(1));

            if (!declaredIDs.contains(list.get(0))) { //list.get(0) returns the userID
                model.add(userID, RDF.TYPE, UserID);
                declaredIDs.add(list.get(0));
            }

            model.add(stepsID, RDF.TYPE, DSteps);
            model.add(userID, isFeatureOfInterestOf, stepsID);
            model.add(stepsID, observedProperty, StepsProp);
            model.add(stepsID, hasCount, Values.literal(stepsCount));
            model.add(stepsID, resultTime, Values.literal(date, Values.iri("http://www.w3.org/2001/XMLSchema#date")));
        }

        connection.begin();
        connection.add(model);
        connection.commit();
        connection.close();
    }

    public void loadDailyDistance(List<List<String>> dailyDistanceList) {

    }

}
