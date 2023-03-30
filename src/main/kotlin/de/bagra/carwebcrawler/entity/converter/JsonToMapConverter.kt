package de.bagra.carwebcrawler.entity.converter

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.slf4j.LoggerFactory
import java.io.IOException

@Converter(autoApply = true)
class JsonToMapConverter : AttributeConverter<HashMap<String, String>, String> {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(JsonToMapConverter::class.java)
    }

    override fun convertToDatabaseColumn(attribute: HashMap<String, String>?): String? {
        return try {
            val objectMapper = ObjectMapper()
            objectMapper.writeValueAsString(attribute)
        } catch (e: JsonProcessingException) {
            LOGGER.error("Could not convert map to json string.")
            return null
        }
    }

    override fun convertToEntityAttribute(dbData: String?): HashMap<String, String>? {
        if(dbData == null) {
            return HashMap()
        }
        try {
            val objectMapper = ObjectMapper()
            @Suppress("UNCHECKED_CAST")
            return objectMapper.readValue(dbData, HashMap::class.java) as HashMap<String, String>
        } catch (e: IOException) {
            LOGGER.error("Convert error while trying to convert string(JSON) to map data structure.")
        }
        return HashMap()
    }
}