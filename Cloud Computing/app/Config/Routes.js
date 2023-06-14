const express = require("express");
const App = require("../Config/App.js");
/**
 * Express Routes class for defining API routes.
 * @class
 * @extends App
 */
class Routes extends App {
  /**
   * Constructs a new Routes object.
   * Creates a new instance of the UsersController.
   */
  constructor() {
    super();
    this.user = new this.UsersController();
    this.regist = new this.AuthRegister();
    this.login = new this.AuthLogin();
    this.upload = new this.UploadController();
  }
  /**
   * Defines the routes for the API.
   * @returns {Routes} An instance of the Express router containing the defined routes.
   */
  defineRoutes() {
    const routes = express.Router();

    routes.get("/test", this.user.testUser.bind(this.user));
    routes.post("/testPost", this.user.createUser.bind(this.user));
    routes.post("/Data", this.user.saveData.bind(this.user));
    routes.post("/Register", this.regist.saveRegist.bind(this.regist));
    routes.post("/Login", this.login.cekLogin.bind(this.login));
    routes.post("/upload-file", this.upload.uploadFile.bind(this.upload));
    routes.get("/best-today", this.upload.bestToday.bind(this.upload));
    routes.get("/getKonten", this.upload.getKonten.bind(this.upload));
    routes.post("/cariKonten", this.upload.cariKonten.bind(this.upload));
    return routes;
  }
}

module.exports = Routes;