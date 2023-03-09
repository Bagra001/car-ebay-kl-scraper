package de.bagra.carwebcrawler.boundry.v1

import de.bagra.carwebcrawler.service.CrawlerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CrawlerController {
    
    @Autowired
    private lateinit var crawlerService: CrawlerService
    
    @PostMapping("/crawler/start")
    fun startCrawler(): ResponseEntity<Void> {
        crawlerService.startCrawling()
        return ResponseEntity.ok().build()
    }

    @PostMapping("/crawler/stop")
    fun stopForceCrawler(): ResponseEntity<Void> {
        crawlerService.stopForceCrawling()
        return ResponseEntity.ok().build()
    }
}