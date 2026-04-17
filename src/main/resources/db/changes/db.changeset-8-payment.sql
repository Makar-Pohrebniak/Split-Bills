CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    comment VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,

    CONSTRAINT fk_payment_group FOREIGN KEY (group_id) REFERENCES `groups`(id),
    CONSTRAINT chk_payment_status CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED'))
);

CREATE INDEX idx_payments_group_id ON payments(group_id);
CREATE INDEX idx_payments_sender_sub ON payments(sender_id);