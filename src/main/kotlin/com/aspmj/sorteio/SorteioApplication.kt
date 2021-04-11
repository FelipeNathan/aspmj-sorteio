package com.aspmj.sorteio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@EnableTransactionManagement
@SpringBootApplication
class SorteioApplication

fun main(args: Array<String>) {
    runApplication<SorteioApplication>(*args)
}
