import fetch from 'node-fetch';
import express from 'express';
import cors from 'cors';
const app = express();

app.use(cors());
app.use(express.json());

app.post('/query', async (req, res) => {
    const sparqlQuery = req.body.query;

    try {
        const response = await fetch('http://localhost:7200/repositories/ActivityTrackingOntology', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/sparql-query',
                'Accept': 'application/sparql-results+json'
            },
            body: sparqlQuery
        });

        if (!response.ok) {
            throw new Error(`GraphDB error: ${response.status} ${response.statusText}`);
        }

        const data = await response.json();
        res.json(data);
    } catch (error) {
        console.error('Error forwarding query:', error);
        res.status(500).json({ error: error.toString() });
    }
});

app.listen(3000, () => {
    console.log('Proxy server running...');
});