const BaseController = require("./BaseController.js");
const path = require('path');

const { google } = require('googleapis');
const tf = require('@tensorflow/tfjs-node');
const { file } = require('googleapis/build/src/apis/file');
var fs = require("fs")

class UploadController extends BaseController {

    loadModel = async function () {
        // Get the absolute path to the model directory
        const modelDirectory = path.resolve(__dirname, '../../model');

        // Construct the absolute path to the model file
        const modelPath = path.join(modelDirectory, 'model_fashion.json');

        const model = await tf.loadLayersModel(`file://${modelPath}`);
        return model;
    }

    processFile = async function (filePath) {
        let model;
        // Load the model
        model = await this.loadModel();
        console.log('Model loaded successfully.');

        // Load the image file using TensorFlow.js
        const image = fs.readFileSync(filePath);
        const decodedImage = tf.node.decodeImage(image);

        const resizedImage = tf.image.resizeBilinear(decodedImage, [96, 96]); // Resize image to match expected input shape
        const reshapedImage = tf.expandDims(resizedImage, 0); // Add batch dimension

        // Make predictions using the model
        const predictions = model.predict(reshapedImage);

        // Cleanup the temporary file
        fs.unlinkSync(filePath);
        return predictions;
    }


    // adit code
    uploadFile = async (req, res) => {
        if (!req.file) {
            return res.status(400).json({ error: 'No file provided' });
        }
        if (req.body.gender == null) {
            return res.status(400).json({ error: 'Please choose gender' });
        }
        console.log(req.body.gender == null)
        // Setelah file berhasil di-upload, dapatkan path file
        const filePath = req.file.path;

        let result = await this.processFile(filePath);

        let { values, indices } = tf.topk(result, 2, true)

        values = values.arraySync()[0]
        indices = indices.arraySync()[0]


        const text = fs.readFileSync('public/fashion.txt', 'utf8');
        let fashion = text.split("\n")
        let predictions = []
        for (let i = 0; i < 2; i++) {
            if (values[i] > 0.01) {
                predictions.push(
                    fashion[indices[i]],
                )
            }
        }
        let gender = req.body.gender

        let query = `${predictions.join(" ")} outfit women`
        const API_KEY = 'AIzaSyAhTDe5rG09vVdYhfOxw88-_Zdkzk_UwEc';

        // Set up the API client
        var links = [];
        const customSearch = google.customsearch('v1');
        // Define search parameters
        const params = {
            q: query, // The query string
            cx: '31ec515b6a0d04171', // The search engine ID (CX ID)
            searchType: 'image', // The search type,
            num: 10,
            aspectRatio: 'tall',
        };

        const searchResult = await customSearch.cse.list({ auth: API_KEY, cx: params.cx, q: params.q, searchType: params.searchType, num: params.num, aspectRatio: params.aspectRatio });
        // Process the search results
        const items = searchResult.data.items;
        if (items.length === 0) {
            console.log('No results found.');
        } else {
            console.log('=== Search results:');

            items.forEach((item, i) => {
                links.push({ photo: item.link });
            });
        }

        res.status(200).json({
            status: 'success',
            message: 'Successfully get fashion prediction',
            predictions: query,
            data: links
        })
    }

    bestToday = async (req, res) => {
        const API_KEY = 'AIzaSyAhTDe5rG09vVdYhfOxw88-_Zdkzk_UwEc';

        // Set up the API client
        var links = [];
        const customSearch = google.customsearch('v1');
        // Define search parameters
        const params = {
            q: `korean outfit of the day`, // The query string
            cx: '31ec515b6a0d04171', // The search engine ID (CX ID)
            searchType: 'image', // The search type,
            num: 10,
            aspectRatio: 'tall',
        };

        const searchResult = await customSearch.cse.list({ auth: API_KEY, cx: params.cx, q: params.q, searchType: params.searchType, num: params.num, aspectRatio: params.aspectRatio });

        // Process the search results
        const items = searchResult.data.items;
        if (items.length === 0) {
            console.log('No results found.');
        } else {
            console.log('=== Search results:');

            items.forEach((item, i) => {
                // Create an object
                var object = {
                    nama: `Testing ${i}`,
                    photo: item.link
                };
                links.push(object);
            });
        }
        res.status(200).json({
            status: 'success',
            message: 'Successfully get best today',
            data: links
        })


    }

    uploadKonten = async (req, res) => {
        const { namaFoto, deskFoto, foto } = req.body;
        if (req.file == null) {
            var pesan = {
                messages: "Mohon upload foto produk"
            };
            return res.status(200).json(pesan)
        }
        const filePath = req.file.path;
        var isGambar = path.basename(filePath);
        let data = {
            namaFoto: namaFoto,
            deskFoto: deskFoto,
            foto: isGambar
        }
        this.foto.save(data);
        let response = {
            status: 200,
            messages: "Success Upload",
            data: data
        };
        return res.status(200).json(response);
    }

    getKonten = async (req, res) => {
        let konten = await this.foto.getResult();
        let respon = {
            "data": konten
        }
        res.status(200).json(respon);
    }

    cariKonten = async (req, res) => {
        const { name } = req.body;
        let konten = await this.foto.whereRaw(`namaFoto like "%${name}%"`).getResult();
        let respon = {
            "data": konten
        }
        res.status(200).json(respon);
    }

}


module.exports = UploadController;