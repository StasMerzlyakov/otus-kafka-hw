package ru.otus.kafka.diplom.testapp.domain

enum class ResultCode(val code: String) {
    OK("200"),
    BAD_REQUEST("400"),
    INTERNAL_SERVER_ERROR("500"),
    ;

    override fun toString(): String {
        return "ResultCode(code='$code')"
    }
}
