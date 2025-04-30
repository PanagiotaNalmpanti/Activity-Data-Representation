document.addEventListener('DOMContentLoaded', () => {
    timeIntervalValues();
    afterSubmitEvents();
});

function timeIntervalValues() {
    const category = document.getElementById('category');
    const timeInterval = document.getElementById('timeInterval');

    category.addEventListener('change', () => {
        const selection = category.value;
        if (selection === 'Distance' || selection === 'HeartRate' || selection === 'Sleep' ||
            selection === 'Weight' || selection === 'VeryActiveMinutes' || selection === 'FairlyActiveMinutes' ||
            selection === 'LightlyActiveMinutes' || selection === 'SedentaryMinutes') {
            timeInterval.value = 'daily';
            timeInterval.disabled = true;
        }
        else {
            timeInterval.disabled = false;
            timeInterval.value = '';
        }
    });
}

function afterSubmitEvents() {
    const form = document.getElementById('form');
    form.addEventListener('submit', async (event) => {
        event.preventDefault(); // cancelling the default behavior of the browser (reloading the page)

        const userID = 'act:' + document.getElementById('userId').value;
        const category = document.getElementById('category').value;
        const timeInterval = document.getElementById('timeInterval').value;

        // Calories and Steps are the only observations that have hourly values
        // so in the ontology they are named differently (HCalories / HSteps, DCalories / DSteps).
        // We need to adjust that by putting the corresponding letters before the default value.
        let categValue = category;
        if (category === 'Calories' || category === 'Steps') {
            if (timeInterval === 'Hourly') {
                categValue = 'act:H' + category;
            }
            else if (timeInterval === 'Daily') {
                categValue = 'act:D' + category;
            }
        }
        else {
            categValue = 'act:' + category;
        }

        const sparqlQuery =
            'PREFIX sosa: <http://www.w3.org/ns/sosa#>\n'+
            'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n'+
            'PREFIX act: <http://www.semanticweb.org/ActivityTrackingOntology#>\n'+
            'SELECT ?result ?time\n'+
            'WHERE {\n'+
            `    ${userID} sosa:isFeatureOfInterestOf ?cat.`+'\n'+
            '    ?cat act:hasCount ?result.\n'+
            '    ?cat sosa:resultTime ?time.\n'+
            `    ?cat rdf:type ${categValue}.`+
            '}';


//        const response = await fetch("http://localhost:7200/repositories/ActivityTrackingOntology", {
//            method: 'POST',
//            headers: {
//                'Content-type': 'application/sparql-query'
//            },
//            body: sparqlQuery
//        });

//        fetch('http://localhost:3000/repositories/ActivityTrackingOntology', {
//            method: 'POST',
//            headers: {
//                'Content-type': 'application/sparql-query',
//                'Accept': 'application/json'
//            },
//            body: sparqlQuery
//        }).then(response => response.json())
//          .then(data => console.log(data))
//          .catch(error => console.log(error));

        fetch('http://localhost:3000/query', {
                    method: 'POST',
                    headers: {
                        'Content-type': 'application/json'
                    },
                    body: JSON.stringify({query: sparqlQuery})
                }).then(response => response.json())
                .then(data => console.log(data))
                .catch(error => console.log(error));
    });



}

