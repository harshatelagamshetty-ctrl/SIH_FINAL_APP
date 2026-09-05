-- Push notification support. A separate table, not a column on
-- users — deliberately, so this never requires touching the
-- existing users table or User entity at all, and so one account
-- can register more than one device's token (a farmer with two
-- phones gets notified on both, correctly).
CREATE TABLE push_tokens (
                             id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             user_id           BIGINT NOT NULL REFERENCES users (id),
                             expo_push_token   VARCHAR(200) NOT NULL,
                             updated_at        TIMESTAMP WITH TIME ZONE NOT NULL,
                             CONSTRAINT uq_expo_push_token UNIQUE (expo_push_token)
);

CREATE INDEX idx_push_tokens_user ON push_tokens (user_id);