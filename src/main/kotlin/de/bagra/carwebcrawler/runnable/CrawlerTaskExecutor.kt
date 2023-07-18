package de.bagra.carwebcrawler.runnable

import de.bagra.carwebcrawler.entity.CrawlerDataEntity
import de.bagra.carwebcrawler.entity.CrawlerDataId
import de.bagra.carwebcrawler.repository.CrawlerDataRepository
import de.bagra.carwebcrawler.util.HtmlUtil
import de.bagra.carwebcrawler.util.LogUtil
import org.jsoup.Connection.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Integer.parseInt
import java.time.LocalDate
import java.time.LocalDateTime


class CrawlerTaskExecutor(
    private val crawlerStatistics: CrawlerStatistics,
    private val crawlerDataRepository: CrawlerDataRepository
) : Runnable {

    private lateinit var articleDetails: List<Element>

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun run() {
        crawlerStatistics.done = false
        crawlerStatistics.interrupt = false
        crawlerStatistics.startDateTime = LocalDateTime.now()
        LogUtil.loadLog(crawlerStatistics)
        for (url: String in crawlerStatistics.urlsToCrawl.keys) {
            crawlerStatistics.runs++
            while (!crawlerStatistics.done && !crawlerStatistics.interrupt) {
                crawl(url)
            }
        }
        log.info("#################################################################################################")
        log.info(
            "Crawl-runs {} - total articles {} - total new article {} - ",
            crawlerStatistics.runs,
            crawlerStatistics.crawledArticles.size,
            crawlerStatistics.crawledArtilceNumber
        )
    }

    private fun crawl(url: String) {
        for (path: String in crawlerStatistics.urlsToCrawl[url]!!) {
            while (crawlerStatistics.lastResponseStatusCode != 404 && !crawlerStatistics.interrupt) {
                val doc: Document? = connectAndParseResponse(url, path)
                if (doc != null) {
                    val articles: Elements = doc.select("article")
                    for (article: Element in articles) {
                        if (crawlerStatistics.interrupt) {
                            return
                        }
                        if (!alreadyLoaded(article)) {
                            LogUtil.writeLog(article)
                            val articleDoc: Document = openArticle(url + article.attr("data-href"))
                            saveArticle(articleDoc, article.attr("data-adid"))
                            crawlerStatistics.crawledArtilceNumber++
                        }
                    }
                    crawlerStatistics.crawledSites++
                    log.info(
                        "site {} crawled - new article crawled {}",
                        crawlerStatistics.crawledSites,
                        crawlerStatistics.crawledArtilceNumber
                    )
                }
            }
            if (crawlerStatistics.interrupt) {
                return
            }
        }
    }

    private fun saveArticle(articleDoc: Document, articleId: String) {
        val articleElement: Element = articleDoc.body()
        articleDetails = articleElement.select("li.addetailslist--detail")
        val defect = articleDetails.find { element ->  element.text() == "Fahrzeugzustand"}
        if (defect != null && HtmlUtil.getHtmlElementFromElement(defect, "span.addetailslist--detail--value")
            ?.text() == "Unbeschädigtes Fahrzeug") {
            val crawlerDataEntity = CrawlerDataEntity(
                CrawlerDataId(null, articleId),
                articleDoc.location(),
                HtmlUtil.loadImgFromImgSrc(articleElement),
                HtmlUtil.getValueFromArticleDetails(articleDetails, "Modell"),
                HtmlUtil.getValueFromArticleDetails(articleDetails, "Kraftstoffart"),
                LocalDate.now(),
                LocalDate.now(),
                HtmlUtil.getValueFromArticleDetails(articleDetails, "Umweltplakette"),
                parseInt(HtmlUtil.getValueFromArticleDetails(articleDetails, "Leistung")),
                parseInt(HtmlUtil.getValueFromArticleDetails(articleDetails, "Kilometerstand").split("\\w")[0]),
                hashMapOf(),
                articleElement.getElementsByClass("boxedarticle--price").text()
            )
            crawlerDataRepository.save(crawlerDataEntity)
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

    private fun alreadyLoaded(article: Element): Boolean {
        var alreadyLoaded = false
        val articleIid: String = article.attr("data-adid")
        val articleIUri: String = article.attr("data-href")
        if (crawlerStatistics.crawledArticles.isNotEmpty() && crawlerStatistics.crawledArticles.containsKey(articleIid) && crawlerStatistics.crawledArticles[articleIid].equals(
                articleIUri
            )
        ) {
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