package de.bagra.carwebcrawler.vaadin.model


class CrawlerData(var model: String? = "", var ez: Int? = 0, var fromToPriceModel: FromToPriceModel? = null, var ps: Double? = 0.0) {
    override fun toString(): String {
        return "{$model, $ez, $fromToPriceModel, $ps}"
    }
}