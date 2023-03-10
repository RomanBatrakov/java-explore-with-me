CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(50)                             NOT NULL,
    email VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name VARCHAR(50)                             NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id),
    CONSTRAINT uq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat FLOAT                                   NOT NULL,
    lon FLOAT                                   NOT NULL,
    CONSTRAINT pk_location PRIMARY KEY (id),
    UNIQUE (lat, lon)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    title  VARCHAR(255)                            NOT NULL,
    pinned BOOLEAN                                 NOT NULL,
    CONSTRAINT pk_compilation PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation         VARCHAR(2000)                           NOT NULL,
    category_id        BIGINT                                  NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    description        VARCHAR(7000)                           NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    initiator_id       BIGINT                                  NOT NULL,
    location_id        BIGINT                                  NOT NULL,
    paid               BOOLEAN                                 NOT NULL,
    participant_limit  BIGINT                                  NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BIGINT                                  NOT NULL,
    state              VARCHAR(255)                            NOT NULL,
    title              VARCHAR(120)                            NOT NULL,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_events_to_users FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_locations FOREIGN KEY (location_id) REFERENCES locations (id) ON DELETE CASCADE,
    CONSTRAINT fk_events_to_categories FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    event_id     BIGINT                                  NOT NULL,
    requester_id BIGINT                                  NOT NULL,
    status       VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT fk_requests_to_events FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_requests_to_users FOREIGN KEY (requester_id) REFERENCES users (id) ON DELETE CASCADE,
    UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS events_compilation
(
    compilation_id BIGINT NOT NULL
        REFERENCES compilations ON DELETE CASCADE,
    event_id       BIGINT NOT NULL
        REFERENCES events ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS reactions
(
    reaction VARCHAR(10) NOT NULL,
    user_id  BIGINT      NOT NULL
        REFERENCES users ON DELETE CASCADE,
    event_id BIGINT      NOT NULL
        REFERENCES events ON DELETE CASCADE,
    PRIMARY KEY (user_id, event_id)
);