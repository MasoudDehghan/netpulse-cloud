CREATE TABLE alert_rules  (
                                     id                  UUID                        NOT NULL,
                                     NAME                varchar(100)                NOT NULL unique ,
                                     metric_type         varchar(32)                 NOT NULL,
                                     threshold_value     DOUBLE PRECISION            NOT NULL,
                                     severity            varchar(16)                 NOT NULL,
                                     enabled             BOOLEAN                     NOT NULL DEFAULT TRUE,
                                     createdAt           TIMESTAMP WITH TIME ZONE    NOT NULL,
                                     recorded_at         TIMESTAMP WITH TIME ZONE    NOT NULL,

                                     CONSTRAINT pk_alert_rules PRIMARY KEY (id)
);
