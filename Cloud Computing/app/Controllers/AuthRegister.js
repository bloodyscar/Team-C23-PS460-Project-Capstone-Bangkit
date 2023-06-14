const bcrypt = require('bcrypt');
const BaseController = require("./BaseController.js");
class AuthRegister extends BaseController{

    saveRegist=async (req, res) => {
        const {username, password, email} = req.body;
        const salt = await bcrypt.genSalt(10);
        const hashPass = await bcrypt.hash(password, salt);
        var regist = {
            username: username,
            password: hashPass,
            email: email
        }

        var cekregist = this.user.save(regist);
        if (cekregist  == false) {
            var response = {
                status:400,
                messages: "Check again your data"
            }
            res.status(400).json(response);
        }
        else {
            var response = {
                status:200,
                messages: "Success",
                data:regist
            }
            res.status(200).json(response);
        }



    }

    
}


module.exports = AuthRegister;