CREATE TABLE IF NOT EXISTS `groups` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    owner_id BIGINT,
    CONSTRAINT fk_groups_owner FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS group_members (
    group_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (group_id, user_id),
    CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES `groups`(id) ON DELETE CASCADE,
    CONSTRAINT fk_group_members_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );