package com.kotlinorm

import com.kotlinorm.Kronos.dataSource
import com.kotlinorm.common.DataSourceConfig
import com.kotlinorm.enums.KLoggerType
import com.kotlinorm.kronosSpringDemo.util.JsonResolverUtil
import com.kotlinorm.kronosSpringDemo.util.SpringDataWrapper.Companion.wrap
import com.kotlinorm.orm.database.table
import com.kotlinorm.pojo.User
import com.kotlinorm.utils.kClassCreatorCustom
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(
	scanBasePackages = [
		"com.kotlinorm.util",
		"com.kotlinorm.common",
	],
	exclude = [DataSourceAutoConfiguration::class]
)
@EnableScheduling
class KronosSpringDemoDebugApplication(
	@Autowired val dataSourceConfig: DataSourceConfig
) {

	val ds by lazy { dataSourceConfig.dataSource().wrap() }

	@PostConstruct
	fun init() {
		Kronos.apply {
			dataSource = { (ds) }
			fieldNamingStrategy = lineHumpNamingStrategy
			tableNamingStrategy = lineHumpNamingStrategy
			loggerType = KLoggerType.SLF4J_LOGGER
			serializeProcessor = JsonResolverUtil
			kClassCreatorCustom = { kClass ->
				when (kClass) {
					User::class -> User()
					else -> null
				}
			}
		}
		sync()
	}
}

fun main(args: Array<String>) {
	runApplication<KronosSpringDemoDebugApplication>(*args)
}

fun sync() {
	dataSource.table.apply {
		syncTable<User>()
	}
}