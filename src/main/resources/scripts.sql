CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
);

-- an account you can use to login the code-analyser app
INSERT INTO `codeanalyser`.`user`
(
`name`,
`password`)
VALUES
(
'test',
'pass');


CREATE TABLE `properties` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL,
  `few_committers_size` int(11) DEFAULT NULL,
  `many_committers_size` int(11) DEFAULT NULL,
  `huge_file_size` int(11) DEFAULT NULL,
  `large_file_size` int(11) DEFAULT NULL,
  `medium_change_size` int(11) DEFAULT NULL,
  `major_change_size` int(11) DEFAULT NULL,
  `period_of_time` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

-- Some configurable properties for testing
INSERT INTO `codeanalyser`.`properties`
(`user_id`,
`few_committers_size`,
`many_committers_size`,
`huge_file_size`,
`large_file_size`,
`medium_change_size`,
`major_change_size`,
`period_of_time`
)
VALUES
(1,
1,
5,
600,
300,
50,
150,
15);


