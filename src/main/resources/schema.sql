CREATE TABLE CRAWLERDATA(
    id INTEGER NOT NULL AUTO_INCREMENT,
    url VARCHAR(255) NOT NULL,
    ebay_article_id VARCHAR(50) NOT NULL,
    img BINARY LARGE OBJECT NOT NULL,
    model VARCHAR(30) NOT NULL,
    vintage DATE,
    ps INTEGER,
    equipment JSON,
    price VARCHAR(15) NOT NULL,
    primary key(id, ebay_article_id)
)