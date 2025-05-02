document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('form').reset();
    timeIntervalValues();
    afterSubmitEvents();
});

function timeIntervalValues() {
    const category = document.getElementById('category');
    const timeInterval = document.getElementById('timeInterval');

    category.addEventListener('change', () => {
        const selection = category.value;
        if (selection === 'Distance' || selection === 'HeartRate' || selection === 'SleepHours' ||
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
        let t = "daily";
        if (category === 'Calories' || category === 'Steps') {
            if (timeInterval === 'hourly') {
                categValue = 'act:H' + category;
                t = "hourly";
            }
            else if (timeInterval === 'daily') {
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

        fetch('http://localhost:7200/repositories/ActivityTrackingOntology', {
            method: 'POST',
            headers: {
                'Content-type': 'application/sparql-query',
                'Accept': 'application/sparql-results+json'
            },
            body: sparqlQuery
        }).then(response => response.text())
          .then(text => {
            console.log("Raw Response:", text); // Δες τι γυρνάει η βάση
            const data = JSON.parse(text);
            chartCreation(data, category);
          })
          .catch(error => {
            console.log(error);
            document.getElementById('error-message').classList.remove('hidden');
          });

    });
}

let chart = null;

function chartCreation(data, category) {
    const bindings = data.results.bindings;
    const values = bindings.map(row => row.result.value);
    const dates = bindings.map(row => row.time.value);

    document.getElementById('chart-container').classList.remove('hidden');
    const ctx = document.getElementById('myChart');

    if (chart !== null) { chart.destroy(); }

    if (bindings.length !== 0) {
        chart = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: dates,
            datasets: [{
              label: category,
              data: values,
              borderWidth: 1,
              backgroundColor: 'rgba(84, 71, 63, 0.5)',
              borderColor: 'rgba(84, 71, 63, 1)'
            }]
          },
          options: {
            scales: {
              y: {
                beginAtZero: true
              }
            }
          }
        });
    }
    else {
        document.getElementById('no-data-message').classList.remove('hidden');
        chart = new Chart(ctx, {
          type: 'bar',
          data: {
            labels: [],
            datasets: [{
              label: 'No Data Available',
              data: [],
              borderWidth: 1,
              backgroundColor: '#54473F'
            }]
          },
          options: {
            scales: {
              y: {
                beginAtZero: true
              }
            }
          }
        });
    }
}

