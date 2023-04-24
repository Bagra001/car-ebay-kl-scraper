package de.bagra.carwebcrawler.vaadin.model

import java.time.LocalDate
import java.time.LocalDate.now

class CrawlerDataFilter(var model: String? = "", var ez: LocalDate? = now(), var fromToPriceModel: FromToPriceModel? = null, var ps: Int? = 0) {
    override fun toString(): String {
        return "{$model, $ez, $fromToPriceModel, $ps}"
    }
}