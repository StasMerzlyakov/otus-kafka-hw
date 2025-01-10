package ru.otus.kafka.diplom.testapp.db

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DbEventRecordRepository : CrudRepository<DbEventRecord, Long>
