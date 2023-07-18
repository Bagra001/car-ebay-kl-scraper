CREATE TABLE CRAWLERDATA(
    id INTEGER NOT NULL AUTO_INCREMENT,
    url VARCHAR(255) NOT NULL,
    ebay_article_id VARCHAR(50) NOT NULL,
    img BINARY LARGE OBJECT,
    model VARCHAR(30),
    engine VARCHAR(10),
    vintage DATE,
    tuev DATE,
    euro VARCHAR(15),
    ps INTEGER,
    km INTEGER,
    equipment JSON,
    price VARCHAR(15),
    primary key(id, ebay_article_id)
)