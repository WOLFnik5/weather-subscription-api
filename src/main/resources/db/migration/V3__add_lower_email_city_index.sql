CREATE UNIQUE INDEX IF NOT EXISTS idx_subscription_lower_email_city
    ON subscription (lower(email), city);
