CREATE TABLE IF NOT EXISTS `raffle` (
  `id` varchar(36) NOT NULL,
  `title` VARCHAR(255) NULL,
  `begin_date` DATETIME NULL,
  `end_date` DATETIME NULL,
  `raffle_date` DATETIME NULL,
  PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `raffle_participant` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `raffle_id` VARCHAR(36) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `phone` VARCHAR(255) NOT NULL,
  `raffled_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_participantraffle_raffle`
    FOREIGN KEY (`raffle_id`)
    REFERENCES `raffle` (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

 CREATE TABLE IF NOT EXISTS `user` (
   `id` INT NOT NULL AUTO_INCREMENT,
   `name` VARCHAR(255) NOT NULL,
   `username` VARCHAR(255) NOT NULL,
   `password` VARCHAR(255) NOT NULL,
   PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  CREATE TABLE IF NOT EXISTS `feature_flag` (
   `id` VARCHAR(255) NOT NULL,
   `active` BOOLEAN NOT NULL DEFAULT FALSE,
   PRIMARY KEY (`id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  INSERT INTO user (id, name, username, password) VALUES (1, 'Administrador', 'raffle_admin', '$2a$10$n/DUrEGm6t1CxQxw8gn8fu2XwBZ3T5kfnJ9e50QwRdu7oP1BrFg8.');
  INSERT INTO user (id, name, username, password) VALUES (2, 'ASPMJ User', 'aspmj', '$2a$10$eTpR00.jjewwjrm43fTAKOxm3lKhlrjxBbMmpCXhGz7kQ0VP7dACC');
  INSERT INTO feature_flag (id, active) VALUES ('CREATE_RAFFLE', false);