package de.bagra.carwebcrawler.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import de.bagra.carwebcrawler.entity.CrawlerDataEntity
import de.bagra.carwebcrawler.vaadin.model.CrawledData
import java.io.ByteArrayInputStream

class MapperUtil(objectMapper: ObjectMapper) {

    private val objectMapper: ObjectMapper

    init {
        this.objectMapper = objectMapper
    }

    companion object {
        fun mapToDto(crawlerDataEntity: CrawlerDataEntity): CrawledData {
            return CrawledData(
                url = crawlerDataEntity.url, model = crawlerDataEntity.model,
                vintage = crawlerDataEntity.vintage, ps = crawlerDataEntity.ps, price = crawlerDataEntity.price,
                equipment = crawlerDataEntity.equipment, img = Image(
                    StreamResource(crawlerDataEntity.model + ".png",
                        InputStreamFactory { ByteArrayInputStream(crawlerDataEntity.img) }), crawlerDataEntity.model
                )
            )
        }
    }
}