# Users schema

# --- !Ups
CREATE TABLE `ChillScore` (
  `id` BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL,
  `positive` BIGINT(20) NOT NULL DEFAULT 0,
  `negative` BIGINT(20) NOT NULL DEFAULT 0,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL
);

CREATE TABLE `Chillax` (
  `id` BIGINT(20) PRIMARY KEY AUTO_INCREMENT NOT NULL,
  `slackRef` VARCHAR(255) NOT NULL,
  `created` DATETIME NOT NULL,
  `updated` DATETIME NOT NULL,
  `chillScoreId`  BIGINT(20) NOT NULL,
  CONSTRAINT fk_chillScoreId FOREIGN KEY (`chillScoreId`)
  REFERENCES `ChillScore`(`id`) ON DELETE CASCADE
);

# --- !Downs
DROP TABLE `Chillax`;
DROP TABLE `ChillScore`;
