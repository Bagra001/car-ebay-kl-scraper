package de.bagra.carwebcrawler.util

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

class HtmlUtil {
    companion object {
        fun loadImgFromImgSrc(articleElement: Element): ByteArray? {
            var imgByteArray: ByteArray? = null
            val img: Element? = articleElement.select("img#viewad-image").first()
            if (img != null) {
                val imgUrl : String = img.absUrl("src");
                imgByteArray = Jsoup.connect(imgUrl).ignoreContentType(true).execute().bodyAsBytes()
            }
            return imgByteArray
        }

        fun getHtmlElementFromElement(element: Element, selector: String): Element? {
            return element.select(selector).first()
        }

        fun getValueFromArticleDetails(articleDetails: List<Element>, detailText: String): String {
            val detailElement: Element? = articleDetails.find { element ->  element.text() == detailText}
            return getHtmlElementFromElement(detailElement!!, "span.addetailslist--detail--value")?.text() ?: ""
        }
    }
}