package de.bagra.carwebcrawler.runnable

import org.jsoup.Connection.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime

class CrawlerTaskExecutor(val crawlerStatistics: CrawlerStatistics): Runnable {

    //TODO werte variable machen pries{vonPreis}::{bisPreis}, marke model ez usw in db speichern
    var urlsToCrawl: MutableMap<String, List<String>> = mutableMapOf(Pair("https://www.ebay-kleinanzeigen.de", 
        listOf("/s-autos/anzeige:angebote/preis::20000/seite:{seitenID}/ford-transit/k0c216+autos.ez_i:2018%2C+autos.power_i:101%2C", "/s-autos/anzeige:angebote/preis::20000/seite:{seitenID}/fiat-ducato/k0c216+autos.ez_i:2018%2C+autos.power_i:101%2C",
            "/s-autos/anzeige:angebote/preis::20000/seite:{seitenID}/peugeot-boxer/k0c216+autos.ez_i:2018%2C+autos.power_i:101%2C", "/s-autos/anzeige:angebote/preis::20000/seite:{seitenID}/citroen-jumper/k0c216+autos.ez_i:2018%2C+autos.power_i:101%2C")))
    
    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
    
    override fun run() {
        crawlerStatistics.startDateTime = LocalDateTime.now()
        loadLog()
        for (url: String in urlsToCrawl.keys) {
            crawlerStatistics.runs++
            while (!crawlerStatistics.done) {
                crawl(url)
            }
        }
        log.info("#################################################################################################")
        log.info("Crawl-runs {} - total articles {} - total new article {} - ", crawlerStatistics.runs, crawlerStatistics.crawledArticles, crawlerStatistics.crawledArtilceNumber)
    }

    private fun crawl(url: String) {
        for (path: String in urlsToCrawl[url]!!) {
            while (crawlerStatistics.lastResponseStatusCode != 404) {
                val doc: Document? = connectAndParseResponse(url, path)
                if (doc != null) {
                    val articles: Elements = doc.select("article")
                    for (article: Element in articles) {
                        if (!alreadyLoaded(article)) {
                            writeLog(article)
                            val doc2: Document = openArticle(url + article.attr("data-href"))
                            crawlerStatistics.crawledArtilceNumber++
                        }
                    }
                    crawlerStatistics.crawledSites++
                    log.info("site {} crawled - new article crawled {}", crawlerStatistics.crawledSites, crawlerStatistics.crawledArtilceNumber)
                }
            }
        }
    }
    
    private fun connectAndParseResponse(url: String, path: String): Document? {
        val pathWithPageNumber: String = path.replace("{seitenID}", (crawlerStatistics.crawledSites + 1).toString())
        val response: Response = Jsoup.connect(url + pathWithPageNumber).execute()
        crawlerStatistics.lastResponseStatusCode = response.statusCode()
        
        if (crawlerStatistics.lastResponseStatusCode == 404) {
            log.error("status code was 404")
            crawlerStatistics.done = true
            return null
        }
        log.info("return document for {}", url + pathWithPageNumber)
        return response.parse()
    }
    
    private fun loadLog() {
        if (crawlerStatistics.crawledArticles.isEmpty()) {
            val path: Path = Path.of("crawledArticles.txt")
            if (Files.exists(path)) {
                val allLines: List<String> = Files.readAllLines(path)
                for (line: String in allLines) {
                    val splittedLine: List<String> = line.split(",")
                    crawlerStatistics.crawledArticles[splittedLine[0]] = splittedLine[1]
                }
                log.info("log-file loaded")
            }
        }
    }
    
    private fun writeLog(article: Element) {
        val path: Path = Path.of("crawledArticles.txt")
        val stringBuilder: StringBuilder = java.lang.StringBuilder()
        stringBuilder.append(article.attr("data-adid"))
        stringBuilder.append(",")
        stringBuilder.append(article.attr("data-href"))
        stringBuilder.append("\n")
        Files.writeString(path, stringBuilder.toString(), StandardOpenOption.APPEND)
    }
    
    private fun alreadyLoaded(article: Element): Boolean {
        var alreadyLoaded = false
        val articleIid: String = article.attr("data-adid")
        val articleIUri: String = article.attr("data-href")
        if (!crawlerStatistics.crawledArticles.isEmpty() &&  crawlerStatistics.crawledArticles.containsKey(articleIid) && crawlerStatistics.crawledArticles[articleIid].equals(articleIUri)) {
            alreadyLoaded = true
            log.info("the article {} - {} was already loaded", articleIid, articleIUri)
        }
        return alreadyLoaded
    }
    
    private fun openArticle(articleUrl: String): Document {
        log.info("get article {}", articleUrl)
        return Jsoup.connect(articleUrl).get()
    }
}