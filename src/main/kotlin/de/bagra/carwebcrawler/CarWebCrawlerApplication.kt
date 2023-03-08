package de.bagra.carwebcrawler

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.theme.Theme
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@Theme(value = "main-theme", variant = "dark")
@SpringBootApplication
private class CarWebCrawlerApplication: SpringBootServletInitializer(), AppShellConfigurator

fun main(args: Array<String>) {
    runApplication<CarWebCrawlerApplication>(*args)
}
