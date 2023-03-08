package de.bagra.carwebcrawler.vaadin.model

class FromToPriceModel(var fromValue: Double?, var toValue: Double?) {
    override fun toString(): String {
        return "{$fromValue, $toValue}"
    }
}