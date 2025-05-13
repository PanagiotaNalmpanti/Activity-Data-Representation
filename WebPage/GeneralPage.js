document.addEventListener('DOMContentLoaded', () => {
    chartCreation();
});

async function chartCreation() {
    // first chart preparation
    let sparqlQuery1 = queryCreation123("act:Weight", 40, 60, "2024-01-01");
    let sparqlQuery2 = queryCreation123("act:Weight", 60, 80, "2024-01-01");
    let sparqlQuery3 = queryCreation123("act:Weight", 80, 100, "2024-01-01");
    let sparqlQuery4 = queryCreation123("act:Weight", 100, 120, "2024-01-01");

    let value1 = await fetching123(sparqlQuery1);
    let value2 = await fetching123(sparqlQuery2);
    let value3 = await fetching123(sparqlQuery3);
    let value4 = await fetching123(sparqlQuery4);

    let data = [value1, value2, value3, value4];
    firstChart(data);

    // second chart preparation
    sparqlQuery1 = queryCreation123("act:Weight", 60, 80, "2024-12-25");
    sparqlQuery2 = queryCreation123("act:Weight", 80, 100, "2024-12-25");
    sparqlQuery3 = queryCreation123("act:Weight", 100, 120, "2024-12-25");
    sparqlQuery4 = queryCreation123("act:Weight", 120, 200, "2024-12-25");

    value1 = await fetching123(sparqlQuery1);
    value2 = await fetching123(sparqlQuery2);
    value3 = await fetching123(sparqlQuery3);
    value4 = await fetching123(sparqlQuery4);

    data = [value1, value2, value3, value4];
    secondChart(data);

    // third chart preparation
    sparqlQuery1 = queryCreation123("act:HeartRate", 50, 60, "2024-01-01");
    sparqlQuery2 = queryCreation123("act:HeartRate", 60, 70, "2024-01-01");
    sparqlQuery3 = queryCreation123("act:HeartRate", 70, 80, "2024-01-01");
    sparqlQuery4 = queryCreation123("act:HeartRate", 80, 90, "2024-01-01");

    value1 = await fetching123(sparqlQuery1);
    value2 = await fetching123(sparqlQuery2);
    value3 = await fetching123(sparqlQuery3);
    value4 = await fetching123(sparqlQuery4);

    data = [value1, value2, value3, value4];
    thirdChart(data);

    //fourth chart preparation
    sparqlQuery = queryCreation4();
    data = await fetching4(sparqlQuery);
    fourthChart(data);

    // fifth chart preparation
    sparqlQuery = queryCreation5();
    data = await fetching5(sparqlQuery);
    fifthChart(data);
}

function queryCreation123(obs, amount1, amount2, date) {
    const q =
        'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n'+
        'PREFIX sosa: <http://www.w3.org/ns/sosa#>\n'+
        'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n'+
        'PREFIX act: <http://www.semanticweb.org/ActivityTrackingOntology#>\n'+
        'SELECT (ROUND(?users * 100.0 / ?totalUsers) AS ?percentage)\n'+
        'WHERE {\n'+
        '  {\n'+
        '    SELECT (COUNT (?u) AS ?totalUsers)\n'+
        '    WHERE {\n'+
        '        ?u rdf:type act:UserID.\n'+
        `        ?x rdf:type ${obs}.\n`+
        '        ?u sosa:isFeatureOfInterestOf ?x.\n'+
        `        ?x sosa:resultTime "${date}"^^xsd:date.\n`+
        '    }\n'+
        '  }\n'+
        '  {\n'+
        '    SELECT (COUNT (?u) AS ?users)\n'+
        '    WHERE {\n'+
        '        ?u rdf:type act:UserID.\n'+
        `        ?x rdf:type ${obs}.\n`+
        '        ?u sosa:isFeatureOfInterestOf ?x.\n'+
        '        ?x act:hasCount ?count.\n'+
        `        ?x sosa:resultTime "${date}"^^xsd:date.\n`+
        `        FILTER (?count >= ${amount1} && ?count < ${amount2})\n`+
        '    }\n'+
        '  }\n'+
        '}';
    return q;
}

function queryCreation4() {
    const q =
        'PREFIX sosa: <http://www.w3.org/ns/sosa#>\n'+
        'PREFIX act: <http://www.semanticweb.org/ActivityTrackingOntology#>\n'+
        'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n'+
        'SELECT ?date (ROUND(AVG(?calories)) AS ?avgCalories)\n'+
        'WHERE {\n'+
        '    ?u rdf:type act:UserID.\n'+
        '    ?cal rdf:type act:DCalories.\n'+
        '    ?u sosa:isFeatureOfInterestOf ?cal.\n'+
        '    ?cal act:hasCount ?calories.\n'+
        '    ?cal sosa:resultTime ?date.\n'+
        '    FILTER(?date >= "2016-04-12"^^xsd:date && ?date <= "2016-05-13"^^xsd:date)\n'+
        '}\n'+
        'GROUP BY ?date';
    console.log(q);
    return q;
}

function queryCreation5() {
    const q =
        'PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n'+
        'PREFIX sosa: <http://www.w3.org/ns/sosa#>\n'+
        'PREFIX act: <http://www.semanticweb.org/ActivityTrackingOntology#>\n'+
        'PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n'+
        'SELECT ?userId (SUM(?active) AS ?activeMins) (SUM(?sedentary) AS ?sedentaryMins)\n'+
        'WHERE {\n'+
        '    ?user rdf:type act:UserID;\n'+
        '    	  sosa:isFeatureOfInterestOf ?act, ?sed.\n'+
        '    ?act act:hasCount ?active;\n'+
        '    	 sosa:resultTime ?date;\n'+
        '         rdf:type act:LightlyActiveMinutes.\n'+
        '    ?sed act:hasCount ?sedentary;\n'+
        '         sosa:resultTime ?date;\n'+
        '    	 rdf:type act:SedentaryMinutes.\n'+
        '    FILTER(?date >= "2016-04-12"^^xsd:date && ?date <= "2016-05-13"^^xsd:date)\n'+
        '    BIND(STRAFTER(STR(?user), "#") AS ?userId)\n'+
        '}\n'+
        'GROUP BY ?userId';
    return q;
}

function fetching123(query) {
    return fetch('http://localhost:7200/repositories/ActivityTrackingOntology', {
            method: 'POST',
            headers: {
                'Content-type': 'application/sparql-query',
                'Accept': 'application/sparql-results+json'
            },
            body: query
        }).then(response => response.text())
          .then(text => {
            console.log("Data:", text);
            const data = JSON.parse(text);
            const bindings = data.results.bindings;
            return bindings.length > 0 ? parseFloat(bindings[0].percentage.value) : 0;
          })
          .catch(error => {
            console.log(error);
            document.getElementById('error-message').classList.remove('hidden');
            return 0;
          });
}

function fetching4(query) {
    return fetch('http://localhost:7200/repositories/ActivityTrackingOntology', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/sparql-query',
            'Accept': 'application/sparql-results+json'
        },
        body: query
    }).then(response => response.text())
      .then(text => {
        console.log("Data:", text);
        const data = JSON.parse(text);
        const bindings = data.results.bindings;
        const values = bindings.map(row => row.avgCalories.value);
        const dates = bindings.map(row => row.date.value);
        return {values: values, dates: dates}
      })
      .catch(error => {
        console.log(error);
        document.getElementById('error-message').classList.remove('hidden');
        return {values: [], dates: []};
      })
}

function fetching5(query) {
    return fetch('http://localhost:7200/repositories/ActivityTrackingOntology', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/sparql-query',
            'Accept': 'application/sparql-results+json'
        },
        body: query
    }).then(response => response.text())
      .then(text => {
        console.log("Data:", text);
        const data = JSON.parse(text);
        const bindings = data.results.bindings;
        const users = bindings.map(row => row.userId.value);
        const activeMins = bindings.map(row => row.activeMins.value);
        const sedentaryMins = bindings.map(row => row.sedentaryMins.value);
        return {users: users, activeMins: activeMins, sedentaryMins: sedentaryMins};
      })
      .catch(error => {
        console.log(error);
        document.getElementById('error-message').classList.remove('hidden');
        return {users: [], activeMins: [], sedentaryMins: []};
      })
}

function firstChart(data) {
    const ctx = document.getElementById('chart1');

    const chart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['<60kl', '60-80kl', '80-100kl', '100-120kl'],
            datasets: [{
                label: '%',
                data: data,
                borderWidth: 1,
                backgroundColor: ['rgba(229, 80, 80, 0.5)', 'rgba(63, 125, 88, 0.5)', 'rgba(236, 82, 40, 0.5)', 'rgba(239, 150, 81, 0.5)'],
                borderColor: 'rgba(84, 71, 63, 1)'
            }]
        },
        options: {
            plugins: {
                title: {
                    display: true,
                    text: "User Percentage by Weight range - Start of 2024"
                }
            }
        }
    });
}

function secondChart(data) {
    const ctx = document.getElementById('chart2');

    const chart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['60-80kl', '80-100kl', '100-120kl', '>120kl'],
            datasets: [{
                label: '%',
                data: data,
                borderWidth: 1,
                backgroundColor: ['rgba(63, 125, 88, 0.5)', 'rgba(236, 82, 40, 0.5)', 'rgba(239, 150, 81, 0.5)', 'rgba(133, 72, 54, 0.5)'],
                borderColor: 'rgba(84, 71, 63, 1)'
            }]
        },
        options: {
            plugins: {
                title: {
                    display: true,
                    text: "User Percentage by Weight range - End of 2024"
                }
            }
        }
    });
}

function thirdChart(data) {
    const ctx = document.getElementById('chart3');

    const chart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['50-60bpm', '60-70bpm', '70-80bpm', '80-90bpm'],
            datasets: [{
                label: '%',
                data: data,
                borderWidth: 1,
                backgroundColor: ['rgba(63, 125, 88, 0.5)', 'rgba(236, 82, 40, 0.5)', 'rgba(229, 80, 80, 0.5)', 'rgba(239, 150, 81, 0.5)'],
                borderColor: 'rgba(84, 71, 63, 1)'
            }]
        },
        options: {
            plugins: {
                title: {
                    display: true,
                    text: "User Percentage by HeartRate range - 2024"
                }
            }
        }
    });
}

function fourthChart(data) {
    const ctx = document.getElementById('chart4');

    const chart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.dates,
            datasets: [{
                label: 'Calories',
                data: data.values,
                backgroundColor: 'rgba(75, 89, 69, 0.8)',
                borderColor: 'rgba(145, 172, 143, 0.5)'
            }]
        },
        options: {
            scales: {
              y: {
                min: 2000
              }
            },
            plugins: {
                title: {
                    display: true,
                    text: "Average Calories Burned - April-May 2016"
                }
            }
        }
    });
}

function fifthChart(data) {
    const ctx = document.getElementById('chart5');

    const chart = new Chart(ctx, {
        type: 'bar',
        data: {
          labels: data.users,
          datasets: [{
            label: 'Active Minutes',
            data: data.activeMins,
            borderWidth: 1,
            backgroundColor: 'rgba(102, 120, 95, 0.5)',
            borderColor: 'rgba(84, 71, 63, 1)'
          },{
            label: 'Sedentary Minutes',
            data: data.sedentaryMins,
            borderWidth: 1,
            backgroundColor: 'rgba(229, 80, 80, 0.5)',
            borderColor: 'rgba(84, 71, 63, 1)'
          }]
        },
        options: {
          scales: {
            y: {
              min: 1000
            }
          },
            plugins: {
            title: {
              display: true,
              text: "Active vs Sedentary Minutes per User - April-May 2016"
            }
          }
        }
    });
}