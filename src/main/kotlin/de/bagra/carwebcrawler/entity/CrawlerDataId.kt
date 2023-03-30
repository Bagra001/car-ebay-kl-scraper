package de.bagra.carwebcrawler.entity

import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
class CrawlerDataId(private val id: Int,
                    private val ebayArticleId: String): Serializable