const BaseController = require("./BaseController.js");
class UserController extends BaseController{

    testUser = async (req, res) => {
        let data = await this.user.getResult();
        let respon = {
          "data":data
        }
        res.status(201).json(respon);
    }

    createUser = async (req, res) => {
        const {username, fullname, email} = req.body;
        const isValid = {
            username: { required: true, alpha_numeric: true },
            fullname: { required: true, alpha_numeric_space: true },
            email: {required: true, is_email: true}
          };
          const errors = await this.valid(isValid).check()(req, res);
          if (errors && errors.length > 0) {
            res.status(400).json({ error: errors });
          } else {
            res.status(201).json({ message: "User created successfully" });
          }
    }

    saveData=async(req, res) =>{
      const{
        npm,
        nama,
        gender,
        email
      } =req.body;
      var data = {
        nama :nama,
        npm :npm,
        gender :gender,
        email :email
      }
      const cekdata = await this.user.save(data);
      console.log(cekdata)
      var rest = {
        "data":data
      }
      res.status(200).json(rest)
    }
}


module.exports = UserController;