DROP DATABASE IF EXISTS crowdsig;
CREATE DATABASE crowdsig;
\connect crowdsig;

CREATE TABLE tweet (
    id NUMERIC(20) PRIMARY KEY,
    text VARCHAR(140) NOT NULL,
    tweeted TIMESTAMP NOT NULL,
    username TEXT NOT NULL,
    latitude DECIMAL(10,6),
    longitude DECIMAL(10,6),
    created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE RULE "tweet_on_duplicate_ignore" AS ON INSERT TO "tweet"
  WHERE EXISTS(SELECT 1 FROM tweet 
              WHERE id=NEW.id)
  DO INSTEAD NOTHING;

CREATE TABLE city (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  search_name TEXT NOT NULL,
  latitude DECIMAL(10,6) NOT NULL,
  longitude DECIMAL(10,6) NOT NULL,
  country_code TEXT NOT NULL,
  state_code TEXT,
  require_country BOOLEAN NOT NULL,
  require_state BOOLEAN NOT NULL,
  population INTEGER NOT NULL,
  created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE city_alias (
  city_id INTEGER NOT NULL REFERENCES city(id),
  alias TEXT NOT NULL,
  PRIMARY KEY(city_id, alias)
);

CREATE TABLE keyword (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  type INTEGER NOT NULL REFERENCES keyword_type(code)
);

CREATE TABLE keyword_alias (
  keyword_id INTEGER NOT NULL REFERENCES keyword(id),
  alias TEXT NOT NULL,
  PRIMARY KEY(keyword_id, alias)
);

CREATE TABLE keyword_type (
  code TEXT PRIMARY KEY,
  description TEXT NOT NULL
);
-- types: manually added/custom, city

CREATE TABLE twitter_api_node (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  updated TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE keyword_twitter_api_node (
  api_node_id INTEGER NOT NULL REFERENCES twitter_api_node(id),
  keyword_id INTEGER NOT NULL REFERENCES keyword(id),
  PRIMARY KEY(api_node_id, keyword_id)
);

CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_city_updated BEFORE UPDATE ON city
  FOR EACH ROW EXECUTE PROCEDURE update_modified_column();
CREATE TRIGGER trigger_twitter_api_node_updated BEFORE UPDATE ON twitter_api_node
  FOR EACH ROW EXECUTE PROCEDURE update_modified_column();

-- VIEWS
CREATE VIEW keywords_cities_us_large
AS SELECT c.search_name keyword, a.alias alias
FROM cities c join city_aliases a on c.id = a.city_id
where c.country_code = 'US' and population >= 200000;


--TODO later: normalize country codes and names
--CREATE TABLE countries (
--  country_code TEXT PRIMARY KEY,
--  country_name TEXT
--);

--CREATE TABLE states (
--  state_code TEXT PRIMARY KEY,
--  state_name TEXT
--);

--TODO later
--CREATE TABLE timezones (
--);
