# Users schema

# --- !Ups

CREATE TABLE `Role` (
                        `id`   BIGINT(20) NOT NULL AUTO_INCREMENT,
                        `name` VARCHAR(255),
                        PRIMARY KEY (`id`)
);
INSERT INTO `Role` (`id`, `name`) VALUES (1, 'role.admin');

CREATE TABLE `User` (
                        `id`        VARCHAR(40) NOT NULL,
                        `name` VARCHAR(255),
                        `email`     VARCHAR(255),
                        `roleId`    BIGINT(20) NOT NULL,
                        PRIMARY KEY (`id`),
                        CONSTRAINT FOREIGN KEY (`roleId`) REFERENCES `Role` (`id`)
);
CREATE TABLE `LoginInfo` (
                             `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
                             `providerID`  VARCHAR(255) NOT NULL,
                             `providerKey` VARCHAR(255) NOT NULL,
                             PRIMARY KEY (`id`)
);
CREATE TABLE `UserLoginInfo` (
                                 `id`          BIGINT(20)  NOT NULL AUTO_INCREMENT,
                                 `userID`      VARCHAR(40) NOT NULL,
                                 `loginInfoId` BIGINT(20)  NOT NULL,
                                 PRIMARY KEY (`id`),
                                 CONSTRAINT FOREIGN KEY (`userID`) REFERENCES `User` (`id`),
                                 CONSTRAINT FOREIGN KEY (`loginInfoId`) REFERENCES `LoginInfo` (`id`)
);
CREATE TABLE `PasswordInfo` (
                                `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT,
                                `hasher`      VARCHAR(255) NOT NULL,
                                `password`    VARCHAR(255) NOT NULL,
                                `salt`        VARCHAR(255),
                                `loginInfoId` BIGINT(20)   NOT NULL,
                                PRIMARY KEY (`id`),
                                CONSTRAINT FOREIGN KEY (`loginInfoId`) REFERENCES `LoginInfo` (`id`)
);

# --- !Downs

DROP TABLE `PasswordInfo`;
DROP TABLE `UserLoginInfo`;
DROP TABLE `LoginInfo`;
DROP TABLE `Role`;
DROP TABLE `User`;
