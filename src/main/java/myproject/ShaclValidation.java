package myproject;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;

import java.io.IOException;
import java.io.StringReader;

public class ShaclValidation {

    public void validation(Model model) {
        ShaclSail shaclSail = new ShaclSail(new MemoryStore());
        SailRepository repository = new SailRepository(shaclSail);
        repository.init();

        try (SailRepositoryConnection connection = repository.getConnection()) {
            StringReader shaclRules = new StringReader(
              String.join("\n", "",
                      //PREFIXES
                      "@prefix act: <http://www.semanticweb.org/ActivityTrackingOntology#> .",
                      "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .",
                      "@prefix sosa: <http://www.w3.org/ns/sosa#> .",
                      "@prefix sh: <http://www.w3.org/ns/shacl#> .",

                      //RULE 1
                      "act:ObservationUserShape",
                      "     a sh:NodeShape ;",
                      "     sh:targetClass sosa:Observation ;",
                      "     sh:property [ sh:path sosa:hasFeatureOfInterest ;",
                      "                   sh:minCount 1; sh:maxCount 1; ] .",

                      //RULE 2
                      "act:HourlyObservationShape",
                      "     a sh:NodeShape ;",
                      "     sh:targetClass sosa:Observation ;",
                      "     sh:property [ sh:path sosa:resultTime ;",
                      "                   sh:datatype xsd:dateTime ; ] .",

                      //RULE 3
                      "act:ObservationResultTimeShape",
                      "     a sh:NodeShape ;",
                      "     sh:targetClass sosa:Observation ;",
                      "     sh:property [ sh:path sosa:resultTime ;",
                      "                   sh:minCount 1; sh:maxCount 1; ] .",

                      //RULE 4
                      "act:HasCountShape",
                      "     a sh:NodeShape ;",
                      "     sh:targetClass sosa:Observation ;",
                      "     sh:property [ sh:path sosa:hasCount ;",
                      "                   sh:minInclusive 0; sh:minCount 1; sh:maxCount 1; ] .",

                      //RULE 5
                      "act:ObservedPropertiesShape",
                      "     a sh:NodeShape ;",
                      "     sh:targetClass sosa:Observation ;",
                      "     sh:property [ sh:path sosa:observedProperty ;",
                      "                   sh:or ( [ sh:class act:ActiveMinutesProp; ] ",
                      "                           [ sh:class act:CaloriesProp; ]",
                      "                           [ sh:class act:DistanceProp; ]",
                      "                           [ sh:class act:HeartRateProp; ]",
                      "                           [ sh:class act:SleepProp; ]",
                      "                           [ sh:class act:StepsProp; ]",
                      "                           [ sh:class act:WeightProp; ] ) ] ."
              )
            );

            //adding shapes
            connection.begin();
            connection.clear(RDF4J.SHACL_SHAPE_GRAPH);
            connection.add(shaclRules, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
            connection.commit();


//            try {
//                connection.commit();
//            } catch (RepositoryException exception) {
//                Throwable cause = exception.getCause();
//                if (cause instanceof ValidationException) {
//                    Model validationReportModel = ((ValidationException) cause).validationReportAsModel();
//
//                    WriterConfig writerConfig = new WriterConfig()
//                            .set(BasicWriterSettings.INLINE_BLANK_NODES, true)
//                            .set(BasicWriterSettings.XSD_STRING_TO_PLAIN_LITERAL, true)
//                            .set(BasicWriterSettings.PRETTY_PRINT, true);
//
//                    Rio.write(validationReportModel, System.out, RDFFormat.TURTLE, writerConfig);
//                }
//                throw exception;
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
