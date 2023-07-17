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
data class CrawlerDataEntity(@EmbeddedId val id: CrawlerDataId,
                             @Column(name = "url", length = 255) val url: String,
                             @Lob @Column(name = "img", columnDefinition="BLOB") val img: ByteArray?,
                             @Column(name ="model") val model: String,
                             @Column(name = "vintage") val vintage: LocalDate,
                             @Column(name = "ps") val ps: Int,
                             @Convert(attributeName = "equipment", converter = JsonToMapConverter::class)  @Column(name = "equipment", columnDefinition = "json") val equipment: HashMap<String, String>,
                             @Column(name = "price", length = 15) val price: String) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CrawlerDataEntity) return false

        if (id != other.id) return false
        if (url != other.url) return false
        if (!img.contentEquals(other.img)) return false
        if (model != other.model) return false
        if (vintage != other.vintage) return false
        if (ps != other.ps) return false
        if (equipment != other.equipment) return false
        if (price != other.price) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + img.contentHashCode()
        result = 31 * result + model.hashCode()
        result = 31 * result + vintage.hashCode()
        result = 31 * result + ps
        result = 31 * result + equipment.hashCode()
        result = 31 * result + price.hashCode()
        return result
    }
}