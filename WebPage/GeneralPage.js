document.addEventListener('DOMContentLoaded', () => {
    chartCreation();
});

async function chartCreation() {
    // first chart preparation
    let sparqlQuery1 = queryCreation(60, 80, "2024-06-01");
    let sparqlQuery2 = queryCreation(80, 100, "2024-06-01");
    let sparqlQuery3 = queryCreation(100, 120, "2024-06-01");

    let value1 = await fetching(sparqlQuery1);
    let value2 = await fetching(sparqlQuery2);
    let value3 = await fetching(sparqlQuery3);

    let data = [value1, value2, value3];
    firstChart(data);

    // second chart preparation
//    secondChart();

//    // third sparql query
//    thirdChart(); // need to pass the data
//
//    //fourth sparql query
//    fourthChart(); // need to pass the data
//
//    // fifth sparql query
//    fifthChart(); // need to pass the data
//
//    // sixth sparql query
//    sixthChart(); // need to pass the data
}

function queryCreation(weight1, weight2, date) {
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
        '        ?w rdf:type act:Weight.\n'+
        '        ?u sosa:isFeatureOfInterestOf ?w.\n'+
        `        ?w sosa:resultTime "${date}"^^xsd:date.\n`+
        '    }\n'+
        '  }\n'+
        '  {\n'+
        '    SELECT (COUNT (?u) AS ?users)\n'+
        '    WHERE {\n'+
        '        ?u rdf:type act:UserID.\n'+
        '        ?w rdf:type act:Weight.\n'+
        '        ?u sosa:isFeatureOfInterestOf ?w.\n'+
        '        ?w act:hasCount ?weight.\n'+
        `        ?w sosa:resultTime "${date}"^^xsd:date.\n`+
        `        FILTER (?weight >= ${weight1} && ?weight < ${weight2})\n`+
        '    }\n'+
        '  }\n'+
        '}';
    return q;
}

function fetching(query) {
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
            return 0;
          });
}

function firstChart(data) {
    const ctx = document.getElementById('chart1');

    const chart = new Chart(ctx, {
        type: 'pie',
        data: {
            labels: ['60-80kl', '80-100kl', '100-120kl'],
            datasets: [{
                label: '%',
                data: data,
                borderWidth: 1,
                backgroundColor: ['rgba(63, 125, 88, 0.5)', 'rgba(236, 82, 40, 0.5)', 'rgba(239, 150, 81, 0.5)'],
                borderColor: 'rgba(84, 71, 63, 1)'
            }]
        },
        options: {
            plugins: {
                title: {
                    display: true,
                    text: "User percentage by Weight range - 2024"
                }
            }
        }
    });
}