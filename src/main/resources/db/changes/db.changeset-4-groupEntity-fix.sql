ALTER TABLE `groups` DROP FOREIGN KEY fk_groups_owner;

ALTER TABLE `groups` MODIFY owner_id VARCHAR(36);

ALTER TABLE `groups`
    ADD CONSTRAINT fk_groups_owner_sub
        FOREIGN KEY (owner_id) REFERENCES users(sub_id) ON DELETE SET NULL;

ALTER TABLE group_members DROP FOREIGN KEY fk_group_members_user;

ALTER TABLE group_members MODIFY user_id VARCHAR(36);

ALTER TABLE group_members
    ADD CONSTRAINT fk_group_members_user_sub
        FOREIGN KEY (user_id) REFERENCES users(sub_id) ON DELETE CASCADE;