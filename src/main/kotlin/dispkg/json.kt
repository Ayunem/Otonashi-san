package dispkg

data class dataList(
    val result: Result, val input_parameter: Input_parameter,
    val accept_parameter: Accept_parameter,
    val character_list: Array<Chara>
)

data class Result(val code: Int, val msg: String)
data class Input_parameter(val name: Array<String>)
data class Accept_parameter(val name: Array<String>)
data class Chara(
    val id: Int, val name: String, val name_ruby: String,
    val family_name: String, val first_name: String,
    val family_name_ruby: String, val first_name_ruby: String,
    val is_foreigner_name: Boolean, val birth_month: Int,
    val birth_day: Int, val gender: Int, val is_idol: Boolean,
    val character_type: Int, val arrival_date: String,
    val origin_media: String, val cv: String, val class_name: String,
    val profile_list : Array<Profile?> = arrayOfNulls(0)
)

data class Profile(
    val generation : Int, val age : Int, val height : Int, val weight : Int,
    val bust : Int, val waist : Int, val hip : Int, val blood_type : String,
    val dominant_hand : String, val hometown : String = "不明または未設定", val hobby : String = "不明または未設定",
    val specialty : String = "不明または未設定", val favorite : String = "不明または未設定", val memo : String,
    val last_update : String, val bloodType : String
)
