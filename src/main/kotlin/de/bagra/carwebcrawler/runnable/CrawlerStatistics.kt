package de.bagra.carwebcrawler.runnable

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CrawlerStatistics {
    lateinit var startDateTime: LocalDateTime
    var crawledSites: Int = 0
    var runs: Int = 0
    var crawledArticles: MutableMap<String, String> = mutableMapOf()
    var done = false
    var interrupt = false
    var lastResponseStatusCode = 0
    var crawledArtilceNumber = 0
    var urlsToCrawl: MutableMap<String, List<String>> = mutableMapOf(Pair("https://www.ebay-kleinanzeigen.de",
        listOf("/s-autos/anzeige:angebote/preis::20000/seite:{seitenID}/ford-transit/k0c216+autos.ez_i:2018,+autos.power_i:101,",
            "/s-autos/anzeige:angebote/preis::20000/seite:{seitenID}/fiat-ducato/k0c216+autos.ez_i:2018,+autos.power_i:101,",
            "/s-autos/anzeige:angebote/preis::20000/seite:{seitenID}/peugeot-boxer/k0c216+autos.ez_i:2018,+autos.power_i:101,",
            "/s-autos/anzeige:angebote/preis::20000/seite:{seitenID}/citroen-jumper/k0c216+autos.ez_i:2018,+autos.power_i:101,")))
}