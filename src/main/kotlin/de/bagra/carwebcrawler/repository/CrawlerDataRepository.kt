package de.bagra.carwebcrawler.repository

import de.bagra.carwebcrawler.entity.CrawlerDataEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CrawlerDataRepository: JpaRepository<CrawlerDataEntity, Int>