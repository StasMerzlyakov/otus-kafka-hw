package ru.otus.kafka.diplom.testapp

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableTransactionManagement
@EnableKafka
@EnableAutoConfiguration
class Application

fun main(args: Array<String>) = runApplication<Application>(*args).let { }