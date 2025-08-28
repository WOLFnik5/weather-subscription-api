ALTER TABLE subscription
ADD CONSTRAINT subscription_email_city_unique UNIQUE (email, city);
