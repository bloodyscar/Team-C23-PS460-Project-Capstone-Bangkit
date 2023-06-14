const Model = require("../Config/Model.js")
/**
 * Represents a model for the "users" table.
 * @extends Model
 */
class FotoModels extends Model{
    constructor() {
        super(); 
        this.setTable('kontenfoto');
        this.setPrimaryKey('id');
        this.setAllowedFields(['namaFoto', 'deskFoto', 'foto']);
        this.setReturnType('object');
        this.setTimestamps(true);
        this.setSoftDelete(false);
    }   

}

module.exports = FotoModels;