ALTER TABLE `olog`.`logs_logbooks` MODIFY COLUMN `log_id` INT(11) UNSIGNED NOT NULL,
 MODIFY COLUMN `logbook_id` INT(11) UNSIGNED NOT NULL,
 DROP FOREIGN KEY `logs_logbooks_logbook_id_fk`,
 DROP FOREIGN KEY `logs_logbooks_log_id_fk`;

ALTER TABLE `olog`.`logs_logbooks` MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `olog`.`logs_attributes` MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
 MODIFY COLUMN `log_id` INT(11) UNSIGNED NOT NULL,
 MODIFY COLUMN `attribute_id` INT(11) UNSIGNED NOT NULL,
 DROP FOREIGN KEY `logs_attributes_attribute_id_fk`,
 DROP FOREIGN KEY `logs_attributes_log_id_fk`;

ALTER TABLE `olog`.`logs` MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL;

ALTER TABLE `olog`.`properties` MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `olog`.`attributes` MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
 MODIFY COLUMN `property_id` INT(11) UNSIGNED NOT NULL;

ALTER TABLE `olog`.`subscriptions` MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
 MODIFY COLUMN `tag_id` INT(11) UNSIGNED NOT NULL,
 DROP FOREIGN KEY `subscriptions_tag_id_fk`;

ALTER TABLE `olog`.`logbooks` MODIFY COLUMN `id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
 MODIFY COLUMN `is_tag` INT(1) UNSIGNED NOT NULL DEFAULT 0;

ALTER TABLE `olog`.`attributes` ADD CONSTRAINT `attributes_property_id_fk` FOREIGN KEY `attributes_property_id_fk` (`property_id`)
    REFERENCES `properties` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

ALTER TABLE `olog`.`logs_attributes` ADD CONSTRAINT `logs_attributes_attribute_id_fk` FOREIGN KEY `logs_attributes_attribute_id_fk` (`attribute_id`)
    REFERENCES `attributes` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
 ADD CONSTRAINT `logs_attributes_log_id_fk` FOREIGN KEY `logs_attributes_log_id_fk` (`log_id`)
    REFERENCES `logs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

ALTER TABLE `olog`.`logs_logbooks` ADD CONSTRAINT `log_id_fk` FOREIGN KEY `log_id_fk` (`log_id`)
    REFERENCES `logs` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
 ADD CONSTRAINT `logbook_id_fk` FOREIGN KEY `logbook_id_fk` (`logbook_id`)
    REFERENCES `logbooks` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

ALTER TABLE `olog`.`subscriptions` ADD CONSTRAINT `subscriptions_tag_id_fk` FOREIGN KEY `subscriptions_tag_id_fk` (`tag_id`)
    REFERENCES `logbooks` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;
