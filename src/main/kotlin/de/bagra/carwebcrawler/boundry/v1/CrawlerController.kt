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
    fun startCrawler(): ResponseEntity<String> {
        crawlerService.startCrawling()
        return ResponseEntity.ok("id")
    }

    @PostMapping("/crawler/stop")
    fun stopCrawler(): ResponseEntity<Boolean> {
        return ResponseEntity.ok(crawlerService.stopCrawling())
    }

    @PostMapping("/crawler/stop/force")
    fun stopForceCrawler(): ResponseEntity<Boolean> {
        return ResponseEntity.ok(crawlerService.stopForceCrawling())
    }
}