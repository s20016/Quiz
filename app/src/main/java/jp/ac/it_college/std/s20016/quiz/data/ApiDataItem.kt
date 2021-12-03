package jp.ac.it_college.std.s20016.quiz.data

data class ApiDataItem(
    val answers: Int,
    val choices: List<String>,
    val id: Int,
    val question: String
)