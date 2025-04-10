DROP TABLE IF EXISTS users, events, categories, compilations, locations, requests, compilations_events, comments CASCADE;

CREATE TABLE IF NOT EXISTS categories (
    category_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locations (
    location_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    lat FLOAT NOT NULL,
    lon FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(254) NOT NULL UNIQUE,
    name VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    event_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation TEXT NOT NULL,
    category_id BIGINT NOT NULL,
    confirmed_requests INTEGER NOT NULL,
    created_on TIMESTAMP NOT NULL,
    description TEXT NOT NULL,
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER NOT NULL,
    published_on TIMESTAMP,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(50) NOT NULL,
    title VARCHAR(255) NOT NULL,
    views INTEGER NOT NULL,
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories(category_id),
    CONSTRAINT fk_location_id FOREIGN KEY (location_id) REFERENCES locations(location_id),
    CONSTRAINT fk_initiator_id FOREIGN KEY (initiator_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS compilations (
    compilation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilations_events (
    compilation_id BIGINT NOT NULL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    CONSTRAINT fk_compilation_id FOREIGN KEY (compilation_id) REFERENCES compilations(compilation_id),
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events(event_id)
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created TIMESTAMP NOT NULL,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT fk_requester_id FOREIGN KEY (requester_id) REFERENCES users(user_id),
    CONSTRAINT fk_event_id_requests FOREIGN KEY (event_id) REFERENCES events(event_id)
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    event_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    commentator_id BIGINT NOT NULL,
    created_on TIMESTAMP NOT NULL,
    CONSTRAINT fk_event_id FOREIGN KEY (event_id) REFERENCES events(event_id),
    CONSTRAINT fk_commentator_id FOREIGN KEY (commentator_id) REFERENCES users(user_id)
);