CREATE TABLE artifacts
(
id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
artifact_id VARCHAR(255),
group_id VARCHAR(255),
version VARCHAR(255),
exclude VARCHAR(255),
path VARCHAR(255),
name VARCHAR(255),
classifier VARCHAR(255),
extension VARCHAR(255),
artifact_type VARCHAR(255)
);                 