-- V1__init_devices.sql
-- Initial multi-vendor network device inventory schema with optimistic locking.

CREATE TABLE devices (
    id                  UUID                         NOT NULL,
    hostname            VARCHAR(128)                NOT NULL,
    management_ip       VARCHAR(45)                 NOT NULL,
    vendor              VARCHAR(32)                 NOT NULL,
    region              VARCHAR(64)                 NOT NULL,
    firmware_version    VARCHAR(64)                 NOT NULL,
    status              VARCHAR(32)                 NOT NULL,
    last_seen_at        TIMESTAMP WITH TIME ZONE,
    created_at          TIMESTAMP WITH TIME ZONE    NOT NULL,
    updated_at          TIMESTAMP WITH TIME ZONE    NOT NULL,
    version             BIGINT                      NOT NULL DEFAULT 0,

    CONSTRAINT pk_devices PRIMARY KEY (id),
    CONSTRAINT uq_devices_hostname UNIQUE (hostname),
    CONSTRAINT uq_devices_management_ip UNIQUE (management_ip)
);

CREATE INDEX idx_devices_status_region ON devices (status, region);
CREATE INDEX idx_devices_vendor ON devices (vendor);
