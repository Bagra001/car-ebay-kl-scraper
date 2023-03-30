package de.bagra.carwebcrawler.entity

import de.bagra.carwebcrawler.entity.converter.JsonToMapConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "CRAWLERDATA")
class CrawlerDataEntity(@EmbeddedId private val id: CrawlerDataId,
                        @Column(name = "url", length = 255) private val url: String,
                        @Lob @Column(name = "img", columnDefinition="BLOB") private val img: ByteArray,
                        @Column(name ="model") private val model: String,
                        @Column(name = "vintage") private val vintage: LocalDate,
                        @Column(name = "ps") private val ps: Int,
                        @Convert(attributeName = "equipment", converter = JsonToMapConverter::class)  @Column(name = "equipment", columnDefinition = "json") private val equipment: HashMap<String, String>,
                        @Column(name = "price", length = 15) private val price: String)