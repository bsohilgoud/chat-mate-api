ALTER TABLE online_status
    DROP CONSTRAINT IF EXISTS fk_online_status_user;

ALTER TABLE IF EXISTS online_status
    ADD CONSTRAINT fk_online_status_user
    FOREIGN KEY (user_id)
    REFERENCES users(user_id)
    ON DELETE CASCADE
