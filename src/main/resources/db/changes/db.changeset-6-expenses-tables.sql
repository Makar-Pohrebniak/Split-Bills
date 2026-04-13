CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(19, 2) NOT NULL,
    description VARCHAR(255),
    group_id BIGINT NOT NULL,
    payer_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_expenses_group FOREIGN KEY (group_id) REFERENCES `groups`(id) ON DELETE CASCADE,
    CONSTRAINT fk_expenses_payer FOREIGN KEY (payer_id) REFERENCES users(id)
    );

CREATE TABLE IF NOT EXISTS expense_shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    share_amount DECIMAL(19, 2) NOT NULL,
    CONSTRAINT fk_shares_expense FOREIGN KEY (expense_id) REFERENCES expenses(id) ON DELETE CASCADE,
    CONSTRAINT fk_shares_user FOREIGN KEY (user_id) REFERENCES users(id)
    );