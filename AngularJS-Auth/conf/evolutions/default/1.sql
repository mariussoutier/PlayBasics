# --- !Ups

CREATE TABLE `usertable` (
  `id`                     BIGINT(20)  NOT NULL AUTO_INCREMENT,
  `email`                  VARCHAR(100) NOT NULL,
  `name`                   VARCHAR(255) NOT NULL,
  `password`               VARCHAR(50) NOT NULL,
  `dateofbirth`            DATE DEFAULT NULL,
  `createdat`              DATE NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `IDX_EMAIL` (`email`)
);

INSERT INTO `usertable` VALUES (1, 'jsmith@example.com', 'John Smith', 'secret123',  null, '2013-07-01');
