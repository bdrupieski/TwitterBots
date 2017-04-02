CREATE ROLE anagrams_readwrite WITH LOGIN ENCRYPTED PASSWORD 'therearesomeanagrams';

GRANT
INSERT
, UPDATE
, SELECT
, DELETE
ON ALL TABLES IN SCHEMA public TO anagrams_readwrite;

GRANT
UPDATE
, SELECT
ON ALL SEQUENCES IN SCHEMA public TO anagrams_readwrite;
